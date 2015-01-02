package autumn.header.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by infinitu on 14. 12. 4..
 */
public class Session {
    public final static long MAX_TIME_TO_LIVE = 24*3600000L;
    private String sessionKey;
    private Map<String,Object> sessionData;
    private long expires;

    public Session(String sessionKey) {
        this.sessionKey = sessionKey;
        this.sessionData = new ConcurrentHashMap<>();
        renewExpire();
    }

    private void renewExpire(){
        this.expires = System.currentTimeMillis()+MAX_TIME_TO_LIVE;
    }

    public void addSessionData(SessionData... sessionData){
        renewExpire();
        for(SessionData sd:sessionData){
            this.sessionData.put(sd.getKey(),sd.getValue());
        }
    }

    public Object getSessionData(String key){
        renewExpire();
        return sessionData.get(key);
    }

    public Map<String,Object> getAllSessionData(){
        this.expires = System.currentTimeMillis()+MAX_TIME_TO_LIVE;
        return sessionData;
    }

    public Object getSessionDataOrDefault(String key,Object defaultValue){
        return sessionData.getOrDefault(key,defaultValue);
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public boolean isExpired(){
        return System.currentTimeMillis()>expires;
    }
}
