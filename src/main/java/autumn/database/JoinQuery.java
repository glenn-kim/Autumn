package autumn.database;

import autumn.database.jdbc.DBConnection;

import java.sql.SQLException;

/**
 * Created by infinitu on 14. 12. 17..
 */
public class JoinQuery<T extends JoinTable> extends AbstractQuery<T>{
    public JoinQuery(Class<T> cls) throws InstantiationException, IllegalAccessException {
        super(cls);
    }

    public JoinQuery(Class<T> cls, T tableInstance) {
        super(cls, tableInstance);
    }

    @Override
    protected void initDeleteSQLStr() {

    }

    @Override
    protected void initInertSQLStr() {

    }

    @Override
    protected String genDeleteSQL() {
        return null;
    }

    @Override
    public int insert(DBConnection conn, Object[] data) throws SQLException {
        throw new SQLException("not supported now");
    }

    @Override
    public int delete(DBConnection conn) throws SQLException {
        throw new SQLException("not supported now");
    }

    @Override
    protected String genInsertSQL(Object[] data) {
        return null;
    }
}
