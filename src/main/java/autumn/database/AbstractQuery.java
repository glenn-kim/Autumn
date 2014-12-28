package autumn.database;

import autumn.database.jdbc.DBConnection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by infinitu on 14. 12. 17..
 */
public abstract class AbstractQuery<T extends AbstractTable> {
    protected final static String INSERT_QUERY_FORMAT = "INSERT INTO %s VALUES %s;";
    protected final static String SELECT_QUERY_FORMAT = "SELECT %s FROM %s WHERE %s;";
    protected final static String DELETE_QUERY_FORMAT = "DELETE %s FROM %s WHERE %s;";
    protected final static String UPDATE_QUERY_FORMAT = "UPDATE %s SET %s WHERE %s;";
    protected String insertSQLFormat;
    protected String deleteSQLFormat;
    protected T table = null;
    protected List<Column> columnList = new LinkedList<>();
    protected List<Field> mappingFields = new LinkedList<>();
    private Condition whereCondition;
    private String selectSQLFormat;
    private String selectFirstSQLFormat;
    private String updateSQLFormat;

    public AbstractQuery(Class<T> cls) {
        try {
            table = cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Field[] fields = cls.getDeclaredFields();

        for (Field f : fields) {
            if((f.getModifiers()&Modifier.PUBLIC)==0)
                continue;
            try {
                Column col = (Column) f.get(table);
                Class colCls = col.getContentsType();
                Field mapField = table.dataTypeClass.getField(f.getName());
                if (!isAssignableFrom(mapField, colCls)) {
                    continue;
                }
                columnList.add(col);
                mappingFields.add(mapField);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        initInertSQLStr();
        initSelectSQLStr();
        initDeleteSQLStr();

    }

    protected static boolean isAssignableFrom(final Field field, final Class cls) {


        if (cls==Integer.class) {
            return field.getType()==Integer.TYPE||field.getType()==Integer.class;
        } else if (cls==Float.class) {
            return field.getType()==Float.TYPE||field.getType()==Float.class;
        } else if (cls==Double.class) {
            return field.getType()==Double.TYPE||field.getType()==Double.class;
        } else if (cls==Character.class) {
            return field.getType()==Character.TYPE||field.getType()==Character.class;
        } else if (cls==Long.class) {
            return field.getType()==Long.TYPE||field.getType()==Long.class;
        } else if (cls==Short.class) {
            return field.getType()==Short.TYPE||field.getType()==Short.class;
        } else if (cls==Boolean.class) {
            return field.getType()==Boolean.TYPE||field.getType()==Boolean.class;
        } else if (cls==Byte.class) {
            return field.getType() == Byte.TYPE || field.getType() == Byte.class;
        }
        return field.getType().isAssignableFrom(cls);
    }

    protected abstract void initDeleteSQLStr();

    protected void initSelectSQLStr() {
        StringBuilder stringBuilder = new StringBuilder();

        String prefix = "";

        for(Column col : columnList){
            stringBuilder.append(prefix);
            prefix = ", ";
            stringBuilder.append(col.toSQL());
        }


        String selectSQLPart = stringBuilder.toString();
        selectSQLFormat = String.format(SELECT_QUERY_FORMAT, selectSQLPart, table.toSQL(),"%s");
        selectFirstSQLFormat = String.format(SELECT_QUERY_FORMAT, selectSQLPart, table.toSQL(),"%s LIMIT 1");

    }

    protected abstract void initInertSQLStr();

    public <DT> List<DT> list(DBConnection conn) throws SQLException { //select
        String sql = genListSQL();
        return select(conn, sql);
    }

    protected String genListSQL() {
        return genSeletDeletSQL(selectSQLFormat);
    }

    public <DT> DT first(DBConnection conn) throws SQLException { //select one
        String sql = genFirstSQL();
        List<DT> l = select(conn, sql);
        if(l.isEmpty())
            return null;
        return l.get(0);
    }

    private <DT> List<DT> select(DBConnection conn, String sql) throws SQLException {
        ResultSet result;
        result = conn.executeQuery(sql);
        return processQueryResult(result);
    }

    protected <DT> List<DT> processQueryResult(ResultSet result) throws SQLException {
        List<DT> objs = new ArrayList<>();
        while(result.next()){
            objs.add((DT) table.makeData(result));
        }

        return objs;
    }

    protected String genFirstSQL() {
        return genSeletDeletSQL(selectFirstSQLFormat);
    }

    public int delete(DBConnection conn) throws SQLException { //delete
        String sql = genDeleteSQL();
        return conn.executeUpdate(sql);
    }

    protected abstract String genDeleteSQL();

    private String genSeletDeletSQL(String format) {

        return String.format(format, genWhereCondition());
    }

    public <DT> int insert(DBConnection conn, DT[] data) throws SQLException {//insert
        String sql = genInsertSQL(data);
        return conn.executeUpdate(sql);
    }

    public <DT> int insert(DBConnection conn, DT data) throws SQLException {//insert
        return insert(conn,new Object[]{data});
    }

    public <DT> List<Integer> insertRetunningGenKey(DBConnection conn, DT[] data) throws SQLException {//insert
        String sql = genInsertSQL(data);
        return conn.executeUpdateReturningGenkey(sql);
    }

    public <DT> Integer insertRetunningGenKey(DBConnection conn, DT data) throws SQLException {//insert
        List<Integer> list = insertRetunningGenKey(conn,new Object[]{data});
        if(list == null || list.size()==0)
            return null;
        return list.get(0);
    }

    protected abstract String genInsertSQL(Object[] data);


    /**
     * @deprecated not implemented
     */
    @Deprecated()
    public int update(){ //update
        return 0;
    }

    protected String genUpdateSQL(){return "";}

    public AbstractQuery<T> where(Function<T,Condition> conditionFunc) {
        this.whereCondition=conditionFunc.apply(table);
        return this;
    }

    public class InsertResult{}

    protected String genWhereCondition(){
        String condi;
        if(whereCondition==null)
            condi = "1=1";
        else
            condi = whereCondition.toSQL();
        return condi;
    }
}
