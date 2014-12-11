package autumn.header.session;

import javax.crypto.KeyGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Created by infinitu on 14. 12. 4..
 */
public class SessionKeyIssuer {
    String sessionKeyCokkieName = null;
    KeyGenerator keygen;
    public SessionKeyIssuer(String sessionKeyCokkieName) throws NoSuchAlgorithmException {
        this.sessionKeyCokkieName = sessionKeyCokkieName;
        keygen = KeyGenerator.getInstance("HmacSHA256");
    }

    public String issue(){

        return Base64.getEncoder().encodeToString(keygen.generateKey().getEncoded());
    }
}
