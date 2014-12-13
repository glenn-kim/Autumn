package autumn.header;

import autumn.util.KV;

/**
 * Created by infinitu on 14. 12. 4..
 */
public class Cookie extends KV<String> {
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_DISPOSITION = "Content=Disposition";

    public Cookie(String key, String value) {
        super(key, value);
    }
}
