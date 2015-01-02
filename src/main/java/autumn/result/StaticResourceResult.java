package autumn.result;

import autumn.Result;
import autumn.header.Cookie;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.*;
import java.util.Properties;

/**
 * Created by infinitu on 14. 12. 29..
 */
public class StaticResourceResult extends Result<StaticResourceResult> {

    public static final String DEFAULT_STATIC_DIRECTORY;
    public static final String DEFAULT_STATIC_URI;

    static {
        Properties p = System.getProperties();

        String directory = p.getProperty("static.directory");
        String uri = p.getProperty("static.uri");

        if(directory==null) directory = "/public";
        if(uri == null) uri = "/static/";

        DEFAULT_STATIC_DIRECTORY= directory;
        DEFAULT_STATIC_URI = uri;
    }

    private InputStream inStream;
    private long length;

    protected StaticResourceResult(int statusCode, File file) throws FileNotFoundException {
        super(statusCode);
        inStream = new FileInputStream(file);
        length = file.length();
        String fname = file.getName();
        if(fname.endsWith(".js"))
            contentType("application/x-javascript");
        else if(fname.endsWith(".css"))
            contentType("text/css");
        else if(fname.endsWith(".html"))
            contentType("text/html");
    }

    @Override
    protected void writeBody(OutputStream stream) {
        try {
            withCookie(Cookie.CONTENT_LENGTH, String.valueOf(length));
            IOUtils.copy(inStream, stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
