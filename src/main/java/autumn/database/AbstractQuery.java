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
    protected final static String SELECT_DISTINCT_QUERY_FORMAT = "SELECT DISTINCT %s FROM %s WHERE %s;";
    protected final static String DELETE_QUERY_FORMAT = "DELETE %s FROM %s WHERE %s;";
    protected final static String UPDATE_QUERY_FORMAT = "UPDATE %s SET %s WHERE %s;";
    protected String insertSQLFormat;
    protected String deleteSQLFormat;
    protected T table = null;
    protected List<Column> columnList = new LinkedList<>();
    protected List<Field> mappingFields = new LinkedList<>();
    protected Condition whereCondition;
    private String selectSQLFormat;
    private String selectDistinctSQLFormat;
    private String selectLimitSQLFormat;
    private String selectDistinctLimitSQLFormat;
    private String selectFirstSQLFormat;

    public AbstractQuery(Class<T> cls) {
        try {
            table = cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Field[] fields = cls.getFields();

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
        selectDistinctSQLFormat = String.format(SELECT_DISTINCT_QUERY_FORMAT, selectSQLPart, table.toSQL(),"%s");
        selectLimitSQLFormat = String.format(SELECT_QUERY_FORMAT, selectSQLPart, table.toSQL(),"%s LIMIT %d OFFSET %d");
        selectDistinctLimitSQLFormat = String.format(SELECT_DISTINCT_QUERY_FORMAT, selectSQLPart, table.toSQL(),"%s LIMIT %d OFFSET %d");
        selectFirstSQLFormat = String.format(SELECT_QUERY_FORMAT, selectSQLPart, table.toSQL(),"%s LIMIT 1");

    }

    protected abstract void initInertSQLStr();

    public <DT> List<DT> list(DBConnection conn) throws SQLException { //select
        String sql = genListSQL();
        whereCondition = null;
        return select(conn, sql);
    }

    protected String genListSQL() {
        return genSeletDeletSQL(selectSQLFormat);
    }

    public <DT> List<DT> list(DBConnection conn, int offset, int limit) throws SQLException { //select
        String sql = genLimitSQL(offset, limit);
        whereCondition = null;
        return select(conn, sql);
    }

    public <DT> List<DT> listDistinct(DBConnection conn) throws SQLException { //select
        String sql = genListDistinctSQL();
        whereCondition = null;
        return select(conn, sql);
    }

    protected String genListDistinctSQL() {
        return genSeletDeletSQL(selectDistinctSQLFormat);
    }

    public <DT> List<DT> listDistinct(DBConnection conn, int offset, int limit) throws SQLException { //select
        String sql = genDistinctLimitSQL(offset, limit);
        whereCondition = null;
        return select(conn, sql);
    }

    public <DT> DT first(DBConnection conn) throws SQLException { //select one
        String sql = genFirstSQL();
        whereCondition = null;
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
        whereCondition = null;
        return conn.executeUpdate(sql);
    }

    protected abstract String genDeleteSQL();

    private String genSeletDeletSQL(String format) {

        return String.format(format, genWhereCondition());
    }

    private String genLimitSQL(int offset, int limit) {
        return String.format(selectLimitSQLFormat, genWhereCondition(),limit , offset);
    }

    private String genDistinctLimitSQL(int offset, int limit) {  //todo refactor
        return String.format(selectDistinctLimitSQLFormat, genWhereCondition(),limit , offset);
    }

    public <DT> int insert(DBConnection conn, DT[] data) throws SQLException {//insert
        String sql = genInsertSQL(data);
        whereCondition = null;
        return conn.executeUpdate(sql);
    }

    public <DT> int insert(DBConnection conn, DT data) throws SQLException {//insert
        return insert(conn,new Object[]{data});
    }

    public <DT> List<Integer> insertReturningGenKey(DBConnection conn, DT[] data) throws SQLException {//insert
        String sql = genInsertSQL(data);
        whereCondition = null;
        return conn.executeUpdateReturningGenkey(sql);
    }

    public <DT> Integer insertReturningGenKey(DBConnection conn, DT data) throws SQLException {//insert
        List<Integer> list = insertReturningGenKey(conn, new Object[]{data});
        if(list == null || list.size()==0)
            return null;
        return list.get(0);
    }

    protected abstract String genInsertSQL(Object[] data);



    public AbstractQuery<T> where(Function<T,Condition> conditionFunc) {
        if(this.whereCondition == null)
            this.whereCondition=conditionFunc.apply(table);
        else
            this.whereCondition.and(conditionFunc.apply(table));
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

    public void reset(){
        whereCondition = null;
    }
}
