package autumn.request;

import autumn.util.KV;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by infinitu on 14. 12. 22..
 */
public class FormUrlEncodedPayload extends UrlEncodedParameterInput {

    protected FormUrlEncodedPayload(InputStream stream) {
        super(RequestPayload.getStringFromInputStream(stream));
    }
}