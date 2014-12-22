package autumn.header;

import autumn.util.KV;

/**
 * Created by infinitu on 14. 12. 18..
 */
public class Header extends KV<String> {

    public final static String LOCATION = "Location";


    public Header(String key, String value) {
        super(key, value);
    }
}
