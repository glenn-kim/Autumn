package autumn.database;

import autumn.annotation.Model;
import autumn.annotation.POST;
import org.reflections.Reflections;

import javax.xml.crypto.Data;
import java.lang.reflect.*;
import java.util.*;

/**
 * Created by infinitu on 14. 12. 9..
 */
public class Table<DataType> extends AbstractTable<DataType> {

    Reflections reflections = new Reflections("my.package.prefix");

    String tableName = null;

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

        mappingDataType(dataTypeClass);
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

}

