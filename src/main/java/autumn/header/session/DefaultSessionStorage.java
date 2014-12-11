package autumn.header.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by infinitu on 14. 12. 4..
 */
public class DefaultSessionStorage implements SessionStorage {

    private SessionKeyIssuer issuer;
    private Map<String,Session> storage;
    private String sessionIdCookieName;

    public DefaultSessionStorage(SessionKeyIssuer issuer,String sessionIdCookieName) {
        this.issuer = issuer;
        this.sessionIdCookieName = sessionIdCookieName;
        this.storage = new ConcurrentHashMap<>();
    }

    @Override
    public String getSessionIdCookieName() {
        return sessionIdCookieName;
    }

    @Override
    public Session getSession(String sessionKey) {
        if(sessionKey==null)
            return null;
        return storage.getOrDefault(sessionKey,null);
    }

    @Override
    public Session newSession() {
        String newKey = issuer.issue();
        while(storage.containsKey(newKey)){
            Session oldSession = storage.get(newKey);
            if(oldSession.isExpired()){
                break;
            }
            newKey = issuer.issue();
        }
        Session newSesseion = new Session(newKey);
        storage.put(newKey,newSesseion);
        return newSesseion;
    }
}
