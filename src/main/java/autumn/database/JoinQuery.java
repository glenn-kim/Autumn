package autumn.database;

/**
 * Created by infinitu on 14. 12. 17..
 */
public class JoinQuery<T extends JoinTable> extends AbstractQuery<T>{
    public JoinQuery(Class<T> cls) {
        super(cls);
    }

    @Override
    protected void initInertSQLStr() {

    }

    @Override
    public InsertResult insert(Object[] data) {
        return null;
    }

    @Override
    protected String genInsertSQL(Object[] data) {
        return null;
    }
}
