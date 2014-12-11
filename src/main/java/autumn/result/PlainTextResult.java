package autumn.result;

import autumn.Result;
import autumn.header.Cookie;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by infinitu on 14. 12. 8..
 */
public class PlainTextResult extends Result<PlainTextResult> {

    private String text;

    protected PlainTextResult(int status, String text) {
        super(status);
        this.text = text;
        this.contentType("text/plain");
    }

    @Override
    protected void writeBody(OutputStream stream) {
        try {
            stream.write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
