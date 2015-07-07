package autumn;

import autumn.database.jdbc.DBConnection;
import autumn.database.jdbc.JDBCConnectionPool;
import autumn.database.jdbc.JDBCDConnection;
import autumn.header.session.Session;
import autumn.request.RequestPayload;
import autumn.request.UrlEncodedParameterInput;
import autumn.route.PathRouter;
import autumn.util.KV;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by infinitu on 14. 11. 2..
 */
public class Request{

    private HttpServletRequest request = null;
    private int method;
    private String path;
    private String contentType;
    private Map<String,String> cookieMap;
    private Map<String,String> headerMap;
    private Session session;
    private InputStream inputStream;
    private RequestPayload body;
    private JDBCConnectionPool pool;
    private JDBCDConnection conn;
    private UrlEncodedParameterInput urlquery;

    //TODO Extract
    public Request(HttpServletRequest req, Session session, JDBCConnectionPool connectionPool) throws IOException {
        this(parseMethod(req), parsePath(req),req.getQueryString(),req.getContentType(),parseCookies(req),parseHeaders(req),session,parseInputStream(parseMethod(req), req), connectionPool);
        this.request = req;
    }

    public Request(int method, String path) {
        this(method,path,null,null,null,null,null,null,null);
    }

    public Request(int method, String path, String queryStr, String contentType, Map<String,String> cookieMap, Map<String,String> headerMap, Session session,InputStream inputStream, JDBCConnectionPool connectionPool){
        this.method = method;
        this.path = path;
        this.contentType=contentType;
        this.cookieMap = cookieMap;
        this.headerMap = headerMap;
        this.session = session;
        this.inputStream = inputStream;
        this.pool = connectionPool;
        this.body = RequestPayload.holdPayload(contentType, inputStream);
        if(queryStr!=null && queryStr.length()>2){
            urlquery = new UrlEncodedParameterInput(queryStr);
        }
    }

    public HttpServletRequest getRequest() {
        return request;
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

    public String getHeader(String key) {
        if(cookieMap == null)
            return null;
        return headerMap.get(key.toLowerCase());
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

    public RequestPayload body(){
        return body;
    }

    public String getUrlQueryParam(String key){
        if(urlquery==null)return null;
        return urlquery.getParam(key);
    }

    public List<KV> getUrlQueryInputs(){
        if(urlquery==null)return null;
        return urlquery.getInputs();
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

    static private Map<String,String> parseHeaders(HttpServletRequest req){
        Map<String,String> headerMap = new LinkedHashMap<>();
        Enumeration<String> keys = req.getHeaderNames();
        while(keys.hasMoreElements()){
            String key = keys.nextElement();
            headerMap.put(key,req.getHeader(key));
        }
        return headerMap;
    }

    static private InputStream parseInputStream(int method, HttpServletRequest req) throws IOException {
        InputStream in = null;
        if(method != PathRouter.REST_METHOD_ID_GET)
            in = req.getInputStream();
        return in;
    }

    public String getAcceptType() {
        return request.getHeader("Accept");
    }

    public String getContentType() {
        return contentType;
    }
}
