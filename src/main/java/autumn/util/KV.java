package autumn.util;

/**
 * Created by infinitu on 14. 12. 4..
 */
public class KV<T> {
    String key;
    T value;

    @Override
    public String toString() {
        return "KV{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public String getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    public KV(String key, T value) {

        this.key = key;
        this.value = value;
    }
}
