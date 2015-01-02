package autumn.header.session;

/**
 * Created by infinitu on 14. 12. 4..
 */
public interface SessionStorage {
    public String getSessionIdCookieName();
    public Session getSession(String sessionKey);
    public Session newSession();
}
