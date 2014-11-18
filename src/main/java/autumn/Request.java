package autumn;

/**
 * Created by infinitu on 14. 11. 2..
 */
public class Request {
    public int method;
    private String path;

    public Request(int method, String path) {
        this.method = method;
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
