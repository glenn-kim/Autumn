package autumn.database.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by infinitu on 14. 12. 18..
 */
public class JDBCDConnection implements DBConnection {
    private Connection conn;
    private Statement stmt;

    public JDBCDConnection(ConnectionPool pool) {
        this.conn = pool.getConnection();
        try {
            conn.setAutoCommit(true);
            this.stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JDBCDConnection transaction() throws SQLException {
        conn.setAutoCommit(false);
        return this;
    }

    @Override
    public JDBCDConnection session() throws SQLException {
        if(!conn.getAutoCommit()) conn.commit();
        conn.setAutoCommit(true);
        return this;
    }

    @Override
    public JDBCDConnection commit() throws SQLException {
        conn.commit();
        return this;
    }

    @Override
    public JDBCDConnection rollBack() throws SQLException {
        conn.rollback();
        return this;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return stmt.executeQuery(sql);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException { //todo encapsulate
        return stmt.executeUpdate(sql);
    }

    @Override
    public List<Integer> executeUpdateReturningGenkey(String sql) throws SQLException { //todo encapsulate
        stmt.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
        ResultSet result = stmt.getGeneratedKeys();
        List<Integer> ret = new ArrayList<>();
        while(result.next()){
            ret.add(result.getInt(1));
        }
        return ret;
    }

    public void free(ConnectionPool pool) throws SQLException {
        if(!conn.getAutoCommit()) conn.commit();
        pool.freeConnection(conn);
        conn = null;
    }
}
