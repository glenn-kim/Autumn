package autumn.result;

import autumn.Result;
import autumn.header.Cookie;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by infinitu on 14. 12. 8..
 */
public class SendFileResult extends Result<SendFileResult> {

    private File file = null;
    private String sendName = null;

    protected SendFileResult(int status, File file) {
        super(status);
        this.file = file;
    }

    protected SendFileResult(int status, String fileName) {
        this(status, new File(fileName));
    }

    public SendFileResult withFileName(String fileName){
        this.sendName = fileName;
        return this;
    }


    @Override
    protected void writeBody(OutputStream stream) {
        if(sendName == null)
            sendName = file.getName();
        FileInputStream src;
        long fileLength;
        try {
            src = new FileInputStream(file);
            fileLength = file.length();

            withCookie(Cookie.CONTENT_LENGTH, String.valueOf(fileLength));
            withCookie(Cookie.CONTENT_DISPOSITION, "attachment; filename=\""+sendName+"\"");

            IOUtils.copy(src,stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //todo Optimize File Transfer in Netty
}
