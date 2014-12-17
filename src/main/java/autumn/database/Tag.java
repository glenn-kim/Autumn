package autumn.database;

/**
 * Created by infinitu on 14. 12. 15..
 */

public class Tag {
    private static Object lock = new Object();
    private static int cnt = -1;
    private String tagName;

    protected Tag(){
        int thisCnt ;
        synchronized (lock){
            cnt++;
            thisCnt = cnt;
        }

        tagName = "";
        do{
            int m = cnt%26;
            tagName += Character.toString((char)('a' + m));
            thisCnt/=26;
        }while(thisCnt>0);
    }

    public String getTagName() {
        return tagName;
    }

    @Override
    public String toString() {
        return tagName;
    }
}
