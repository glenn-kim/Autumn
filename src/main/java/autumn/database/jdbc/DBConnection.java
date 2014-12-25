package autumn.database.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by infinitu on 14. 12. 18..
 */
public interface DBConnection {
    JDBCDConnection transaction() throws SQLException;

    JDBCDConnection session() throws SQLException;

    JDBCDConnection commit() throws SQLException;

    JDBCDConnection rollBack() throws SQLException;

    ResultSet executeQuery(String sql) throws SQLException;
    int executeUpdate(String sql) throws SQLException;

    List<Integer> executeUpdateReturningGenkey(String sql) throws SQLException;
}
