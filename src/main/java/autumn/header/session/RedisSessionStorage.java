package autumn.header.session;

/**
 * Created by infinitu on 14. 12. 4..
 */
public class RedisSessionStorage implements SessionStorage {
    @Override
    public String getSessionIdCookieName() {
        return null;
    }

    @Override
    public Session getSession(String sessionKey) {
        return null;
    }

    @Override
    public Session newSession() {
        return null;
    }
}
