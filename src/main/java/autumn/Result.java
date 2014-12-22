package autumn;

import autumn.header.Cookie;
import autumn.header.Header;
import autumn.header.session.Session;
import autumn.header.session.SessionData;
import autumn.header.session.SessionStorage;
import autumn.result.PlainTextResult;
import autumn.result.ResultResolver;
import autumn.result.SendFileResult;
import autumn.result.TemplateResult;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by infinitu on 14. 11. 2..
 */
public abstract class Result<This extends Result>{

    public final static String PLAIN_TEXT = "text/plain";
    public final static String HTML = "text/html";

    private static TemplateEngine templateEngineInstance = null;
    static void initializeTemplateEngine(){
        templateEngineInstance = new TemplateEngine();

        TemplateResolver resolver = new ServletContextTemplateResolver();
                //new FileTemplateResolver();//new ClassLoaderTemplateResolver();
        resolver.setPrefix("/WEB-INF/templates/"); //TODO Enable Configurable.
        resolver.setSuffix(".html");
        templateEngineInstance.addTemplateResolver(resolver);
    }

    private List<Header> headerInput = new ArrayList<>();
    private List<Cookie> cookieInput = new ArrayList<>();
    private List<SessionData> sessionDataInput = new ArrayList<>();
    private String contentType = PLAIN_TEXT; //by default
    private boolean newCookies = false;
    private boolean newSessions = false;
    private int statusCode = 200;

    public static final StatusCodeHolder Ok                             = new StatusCodeHolder(200);
    public static final StatusCodeHolder MovedPermanently(String to)    {return new RedirectHolder(301,to);}
    public static final StatusCodeHolder TemporaryRedirect(String to)   {return new RedirectHolder(307,to);}
    public static final StatusCodeHolder BadRequest                     = new StatusCodeHolder(400);
    public static final StatusCodeHolder Unauthorized                   = new StatusCodeHolder(401);
    public static final StatusCodeHolder Forbidden                      = new StatusCodeHolder(403);
    public static final StatusCodeHolder NotFound                       = new StatusCodeHolder(404);
    public static final StatusCodeHolder InternalServerError            = new StatusCodeHolder(500);

    public static StatusCodeHolder Status(int code){
        return new StatusCodeHolder(code);
    }


    protected Result(int statusCode) {
        this.statusCode = statusCode;
    }

    public This withCookie(String key, String value){
        return with(new Cookie(key, value));
    }

    public This with(Cookie... cookie){
        Collections.addAll(cookieInput,cookie);
        return (This)this;
    }

    public This withNewCookie(String key, String value){
        return withNewCookie(new Cookie(key,value));
    }

    public This withNewCookie(Cookie cookie){
        this.newCookies = true;
        Collections.addAll(cookieInput,cookie);
        return (This)this;
    }

    public This withSessionData(String key, Object value){
        return with(new SessionData<>(key, value));
    }

    public This with(SessionData... sessionData){
        Collections.addAll(sessionDataInput, sessionData);
        return (This)this;
    }

    public This withNewSession(String key, Object value){
        return withNewSession(new SessionData<>(key, value));
    }

    public This withNewSession(SessionData... sessionData){
        this.newSessions=true;
        Collections.addAll(sessionDataInput, sessionData);
        return (This)this;
    }

    public This with(Header... header){
        Collections.addAll(headerInput,header);
        return (This)this;
    }

    public This contentType(String contentType){
        this.contentType = contentType;
        return (This)this;
    }

    protected Map<String,String> mergeCookies(SessionStorage storage, Request request){
        Session session=null;
        String sessionIdCookie = storage.getSessionIdCookieName();
        Map<String,String> tempCookie = request.getAllCookies();;

        //Create New Session Or Use Original
        if(!newSessions){
            session = storage.getSession(request.getCookie(sessionIdCookie));
        }

        if(session == null){
            session = storage.newSession();
            tempCookie.remove(sessionIdCookie);
        }

        //Insert SessionData
        for(SessionData sd : sessionDataInput){
            session.addSessionData(sd);
        }

        Map<String,String> cookies;

        //Use Original Cookies or New Cookie Set
        if(!newCookies)
            cookies = tempCookie;
        else
            cookies = new HashMap<>();

        //Insert Cookie Input
        for(Cookie kv : cookieInput){
            cookies.put(kv.getKey(),kv.getValue());
        }

        //Insert Session Key
        cookies.put(sessionIdCookie, session.getSessionKey());

        //Insert Content Type
        cookies.put(Cookie.CONTENT_TYPE,contentType);

        return cookies;
    }

    protected List<Header> getHeaderInput(){
        return headerInput;
    }

    protected abstract void writeBody(OutputStream stream) throws IOException;

    protected void writeBodyServlet( HttpServletRequest request,
                                              HttpServletResponse response,
                                              ServletContext servletContext,
                                              OutputStream stream ) throws IOException {
        writeBody(stream);
    }

    protected List<Cookie> getCookieInput() {
        return cookieInput;
    }

    protected String getContentType() {
        return contentType;
    }

    protected int getStatusCode() {
        return statusCode;
    }

    public static class StatusCodeHolder{
        private int status;
        private StatusCodeHolder(int status){
            this.status = status;
        }
        public TemplateResult template(String templateName){
            return template(Result.templateEngineInstance,templateName);
        }

        public TemplateResult template(TemplateEngine templateEngineInstance, String templateName){
            return ResultResolver.template(status, templateEngineInstance, templateName);
        }

        public SendFileResult sendFile(String filePath){
            return ResultResolver.sendFile(status, filePath);
        }

        public SendFileResult sendFile(File file){
            return ResultResolver.sendFile(status, file);
        }

        public PlainTextResult plainText(String text){
            return ResultResolver.plainText(status, text);
        }
    }
    public static class RedirectHolder extends StatusCodeHolder{
        private String to;
        private RedirectHolder(int status, String to) {
            super(status);
            this.to = to;
        }

        @Override
        public TemplateResult template(String templateName) {
            return super.template(templateName);
        }

        @Override
        public TemplateResult template(TemplateEngine templateEngineInstance, String templateName) {
            return super.template(templateEngineInstance, templateName);
        }

        @Override
        public SendFileResult sendFile(String filePath) {
            return super.sendFile(filePath);
        }

        @Override
        public SendFileResult sendFile(File file) {
            return super.sendFile(file);
        }

        @Override
        public PlainTextResult plainText(String text) {
            return super.plainText(text);
        }
    }
}
