package autumn.database;

import java.util.Objects;

/**
 * Created by infinitu on 14. 12. 9..
 */
public class Query<T extends AbstractTable> {

    public Objects[] list(){

    }

    public Object first(){

    }

    public Query<T> where(Condition condition) {
        return null;
    }
}
