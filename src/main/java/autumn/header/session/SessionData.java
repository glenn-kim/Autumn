package autumn.header.session;

import autumn.util.KV;

/**
 * Created by infinitu on 14. 12. 4..
 */
public class SessionData<T> extends KV<T> {
    public SessionData(String key, T value) {
        super(key, value);
    }
}
