package autumn.database;

import autumn.annotation.Model;
import autumn.annotation.POST;
import org.reflections.Reflections;

import javax.xml.crypto.Data;
import java.lang.reflect.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by infinitu on 14. 12. 9..
 */
public class Table<DataType> extends AbstractTable<DataType> {

    Reflections reflections = new Reflections("my.package.prefix");

    private String tableName = null;
    private final Tag tag;
    private final String sqlStr;

    Constructor<DataType> constructor;

    public Table(Class<DataType> dataTypeClass) throws NoSuchFieldException {
        this(null, dataTypeClass);
    }

    public Table(String tableName, Class<DataType> dataTypeClass) throws NoSuchFieldException {

        if(tableName==null) {
            tableName = getAnnotatedTableName();
            if (tableName == null)
                throw new MalformedParametersException("table name is not defined");
        }

        this.tableName=tableName;
        tag = new Tag();
        this.sqlStr = tableName+" "+tag.toString();

        mappingDataType(dataTypeClass);
    }

    public Column<Integer>      intColumn(String columnName){
        return new Column<>(columnName,tag,Integer.class);
    }
    public Column<Long>         longColumn(String columnName){
        return new Column<>(columnName,tag,Long.class);
    }
    public Column<Double>       doubleColumn(String columnName){
        return new Column<>(columnName,tag,Double.class);
    }
    public Column<Float>        floatColumn(String columnName){
        return new Column<>(columnName,tag,Float.class);
    }
    public Column<Byte>         byteColumn(String columnName){
        return new Column<>(columnName,tag,Byte.class);
    }
    public Column<String>       stringColumn(String columnName){
        return new Column<>(columnName,tag,String.class);
    }
    public Column<Character>    characterColumn(String columnName){
        return new Column<>(columnName,tag,Character.class);
    }
    public Column<Timestamp>    timestampColumn(String columnName){
        return new Column<>(columnName,tag,Timestamp.class);
    }
    public Column<Date>         dateColumn(String columnName){
        return new Column<>(columnName,tag,Date.class);
    }


    protected String getTableName(){
        return tableName;
    }


    private String getAnnotatedTableName() {
        try {
            return this.getClass().getAnnotation(Model.class).value();
        } catch (Exception e) {
            return null;
        }
    }

    protected String getTag(){
        return this.tag.getTagName();
    }

    @Override
    protected String toSQL(){
        return sqlStr;
    }
}

