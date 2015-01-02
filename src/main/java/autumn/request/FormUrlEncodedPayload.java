package autumn.request;

import java.io.InputStream;

/**
 * Created by infinitu on 14. 12. 22..
 */
public class FormUrlEncodedPayload extends UrlEncodedParameterInput {

    protected FormUrlEncodedPayload(InputStream stream) {
        super(RequestPayload.getStringFromInputStream(stream));
    }
}