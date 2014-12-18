package autumn.database.jdbc;

import java.sql.Connection;

/**
 * Created by infinitu on 14. 12. 18..
 */
public interface ConnectionPool {
    Connection getConnection();

    void freeConnection(Connection conn);

    void closeAll();

    boolean addConnection();
}
