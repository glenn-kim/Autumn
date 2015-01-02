package autumn.result;

import autumn.Result;

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
