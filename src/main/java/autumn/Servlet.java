package autumn;

import autumn.database.jdbc.ConnectionPool;
import autumn.database.jdbc.JDBCConnectionPool;
import autumn.header.Cookie;
import autumn.header.Header;
import autumn.header.session.DefaultSessionStorage;
import autumn.header.session.Session;
import autumn.header.session.SessionKeyIssuer;
import autumn.header.session.SessionStorage;
import autumn.result.ExceptionResult;
import autumn.result.ResultResolver;
import autumn.route.ControllerReflector;
import autumn.route.PathRouter;

import javax.management.MalformedObjectNameException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by infinitu on 14. 10. 31..
 */
public class Servlet extends HttpServlet{

    public static final String DEFAULT_SESSION_KEY_COOKIE_NAME = "AUTUMN_SESSION";

    PathRouter.ActionInvoker invoker;
    SessionStorage sessionStorage;
    JDBCConnectionPool connectionPool;

    @Override
    public void init() throws ServletException {
        super.init();

        //Must not to commit
        Properties p = System.getProperties();
        p.put("db.url", "***REMOVED***");
        p.put("db.user", "***REMOVED***");
        p.put("db.password","***REMOVED***");


        //System.out.println("init called");
        try{
            invoker = (new ControllerReflector()).generateActionInvoker();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        SessionKeyIssuer issuer = null;
        try {
            issuer = new SessionKeyIssuer(DEFAULT_SESSION_KEY_COOKIE_NAME);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        sessionStorage = new DefaultSessionStorage(issuer,DEFAULT_SESSION_KEY_COOKIE_NAME);
        Result.initializeTemplateEngine();

        connectionPool = JDBCConnectionPool.Instance();
    }
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.getWriter().println("hello!!");
//    }

    @Override
    protected void service(HttpServletRequest servletReq, HttpServletResponse servletResp) throws ServletException, IOException {
        Result res = null;
        Request request = new Request(servletReq,sessionStorage.getSession(extractSessionId(servletReq)),connectionPool);

        try {
            res = invoker.doAct(request);
        }
        catch (ExceptionResult result){
            res = result.getResult();
        }
        catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace(System.err);
            //TODO 500 error
            return;
        }

        request.freeDBConn();

        if(res == null){
            //TODO 404 error
            return;
        }

        servletResp.setContentType(res.getContentType());
        servletResp.setStatus(res.getStatusCode());
        @SuppressWarnings("unchecked")
        Set<Map.Entry<String, String>> newCookies = res.mergeCookies(sessionStorage, request).entrySet();

        for(Map.Entry<String,String> cookie : newCookies){
            javax.servlet.http.Cookie servCookie = new javax.servlet.http.Cookie(cookie.getKey(),cookie.getValue());
            servCookie.setPath("/");
            servletResp.addCookie(servCookie);
        }

        @SuppressWarnings("unchecked")
        List<Header> headerList = res.getHeaderInput();
        for(Header h : headerList){
            servletResp.addHeader(h.getKey(),h.getValue());
        }

        res.writeBodyServlet(servletReq,servletResp,this.getServletContext(), servletResp.getOutputStream());


    }

    private String extractSessionId(HttpServletRequest req){
        javax.servlet.http.Cookie[] cookies = req.getCookies();
        if(cookies == null)
            return null;
        for(javax.servlet.http.Cookie cookie : cookies){
            if(cookie.getName().equals(DEFAULT_SESSION_KEY_COOKIE_NAME)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
