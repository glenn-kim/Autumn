package autumn;

import autumn.header.Cookie;
import autumn.header.Header;
import autumn.header.session.Session;
import autumn.header.session.SessionData;
import autumn.header.session.SessionStorage;
import autumn.result.*;
import com.google.gson.JsonElement;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
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

        Properties messages = new Properties();
        try {
            messages.load(Result.class.getResourceAsStream("/messages.properties"));


            StandardMessageResolver standardMessageResolver = new StandardMessageResolver();
            standardMessageResolver.setDefaultMessages(messages);

            templateEngineInstance.addMessageResolver(standardMessageResolver);

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
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
        Map<String,String> cookies = new LinkedHashMap<>();

        //Create New Session Or Use Original
        if(!newSessions){
            session = storage.getSession(request.getCookie(sessionIdCookie));
        }

        if(session == null){
            session = storage.newSession();
            cookies.put(sessionIdCookie, session.getSessionKey());

        }

        //Insert SessionData
        for(SessionData sd : sessionDataInput){
            session.addSessionData(sd);
        }


        //Insert Cookie Input
        for(Cookie kv : cookieInput){
            cookies.put(kv.getKey(),kv.getValue());
        }

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

        public  JsonResult jsonObject(){
            return ResultResolver.jsonObject(status);
        }

        public  JsonResult jsonArray(){
            return ResultResolver.jsonArray(status);
        }
        public  JsonResult json(Object object){
            return ResultResolver.json(status, object);
        }
        public JsonResult json(JsonElement jsonElement){
            return ResultResolver.json(status, jsonElement);
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
            return (TemplateResult) super.template(templateName).with(new Header(Header.LOCATION, to));
        }

        @Override
        public TemplateResult template(TemplateEngine templateEngineInstance, String templateName) {
            return (TemplateResult) super.template(templateEngineInstance, templateName).with(new Header(Header.LOCATION, to));
        }

        @Override
        public SendFileResult sendFile(String filePath) {
            return super.sendFile(filePath).with(new Header(Header.LOCATION,to));
        }

        @Override
        public SendFileResult sendFile(File file) {
            return super.sendFile(file).with(new Header(Header.LOCATION, to));
        }

        @Override
        public PlainTextResult plainText(String text) {
            return super.plainText(text).with(new Header(Header.LOCATION, to));
        }
    }
}
