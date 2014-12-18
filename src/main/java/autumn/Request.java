package autumn;

import autumn.database.jdbc.DBConnection;
import autumn.database.jdbc.JDBCConnectionPool;
import autumn.database.jdbc.JDBCDConnection;
import autumn.header.Cookie;
import autumn.header.session.Session;
import autumn.route.PathRouter;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by infinitu on 14. 11. 2..
 */
public class Request {

    private int method;
    private String path;
    private Map<String,String> cookieMap;
    private Session session;
    private JDBCConnectionPool pool;
    private JDBCDConnection conn;

    public Request(HttpServletRequest req, Session session, JDBCConnectionPool connectionPool){
        this(parseMethod(req), parsePath(req),parseCookies(req),session, connectionPool);
    }

    public Request(int method, String path) {
        this(method,path,null,null,null);
    }

    public Request(int method, String path, Map<String,String> cookieMap, Session session, JDBCConnectionPool connectionPool){
        this.method = method;
        this.path = path;
        this.cookieMap = cookieMap;
        this.session = session;
        this.pool = connectionPool;
    }

    public String getPath() {
        return path;
    }

    public int getMethod() {
        return method;
    }

    public String getCookie(String key) {
        if(cookieMap == null)
            return null;
        return cookieMap.get(key);
    }

    public Object getSession(String key) {
        if(session==null)
            return null;
        return session.getSessionData(key);
    }

    public Map<String,String> getAllCookies() {
        return cookieMap;
    }

    public DBConnection getDBConnection(){
        if(conn == null)
            conn = new JDBCDConnection(pool);
        return conn;
    }
    protected void freeDBConn(){
        if(conn!=null){
            try {
                conn.free(pool);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    static private int parseMethod(HttpServletRequest req){
        switch (req.getMethod()){
            case "GET":
                return PathRouter.REST_METHOD_ID_GET;
            case "POST":
                return PathRouter.REST_METHOD_ID_POST;
            case "PUT":
                return PathRouter.REST_METHOD_ID_PUT;
            case "DELETE":
                return PathRouter.REST_METHOD_ID_DELETE;
            case "UPDATE":
                return PathRouter.REST_METHOD_ID_UPDATE;
            default:
                return -1;
        }
    }

    static private String parsePath(HttpServletRequest req){
        return req.getContextPath()+req.getPathInfo();
    }

    static private Map<String,String> parseCookies(HttpServletRequest req){
        Map<String,String> cookieMap = new LinkedHashMap<>();
        javax.servlet.http.Cookie[] cookies = req.getCookies();
        if(cookies == null)
            return cookieMap;
        for(javax.servlet.http.Cookie cookie:cookies){
            cookieMap.put(cookie.getName(),cookie.getValue());
        }
        return cookieMap;
    }

    //TODO Add Parsing Http Request Query Parameters.
}
