package autumn.database.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;

/**
 * Created by infinitu on 14. 12. 15..
 */
public class JDBCConnectionPool implements ConnectionPool {

    private final int INITCON = 16;

    private static JDBCConnectionPool mPool = new JDBCConnectionPool();
    public static JDBCConnectionPool Instance() {return mPool;}

    public int maxConnection;

    private int numUsedConn = 0;

    private LinkedList<Connection> freeList = new LinkedList <Connection> ();
    private String url, user, password;

    public JDBCConnectionPool() {
        Properties p = System.getProperties();
        url = p.getProperty("db.url");
        user = p.getProperty("db.user");
        password = p.getProperty("db.password");
        maxConnection = 20;//Integer.parseInt(p.getProperty("maxConnection"));

        for(int i = 0; i < INITCON; i++) {
            boolean ret = addConnection();
            if(!ret) {
                String msg = "DBPool Init Fail!!";
                System.err.println(msg);
                System.exit(1);
            }
        }
    }

    @Override
    public synchronized Connection getConnection() {
        Connection conn = null;
        if (freeList.size() > 0 ) {
            conn = freeList.removeFirst();
            try {
                if(conn.isClosed()) {
                    numUsedConn--;
                    conn = getConnection();
                }
                else {
                    numUsedConn++;
                    return conn;
                }
            } catch (SQLException e) {
                conn = getConnection();
            }
        }
        //no more connections in list
        if (freeList.isEmpty() && numUsedConn < maxConnection) {
            String msg = String.format(
                    "no more connections(current: %d max: %d). add 1.",
                    numUsedConn, maxConnection);
            addConnection();
            conn = getConnection();
        }
        if (conn != null)
            return conn;
        else {
            //can't get connections
            String msg = String.format(
                    "can't get connections (free: %d current: %d max: %d)",
                    freeList.size(), numUsedConn, maxConnection);
            return null;
        }
    }

    @Override
    public synchronized void freeConnection(Connection conn) {
        numUsedConn--;
        try {
            if(!conn.isClosed())
                freeList.add(conn);
            else
                addConnection();

        } catch (SQLException e) {
            //do nothing
        }
        //for debug
        System.out.println(numUsedConn + ", " + freeList.size());
    }

    @Override
    public synchronized void closeAll() {
        while(!freeList.isEmpty()) {
            try {
                freeList.remove().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean addConnection() {
        Connection c = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            return false;
        }
        try {
            c = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            return false;
        }
        freeList.add(c);
        return true;
    }


}
