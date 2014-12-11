package autumn.header.session;

import java.util.Map;

/**
 * Created by infinitu on 14. 12. 4..
 */
public interface SessionStorage {
    public String getSessionIdCookieName();
    public Session getSession(String sessionKey);
    public Session newSession();
}
