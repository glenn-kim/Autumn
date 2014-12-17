package autumn.database;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by infinitu on 14. 12. 9..
 */
public class TableQuery<T extends Table> extends AbstractQuery<T> {


    public TableQuery(Class<T> cls){
        super(cls);
    }

    @Override
    protected void initInertSQLStr() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(table.getTableName());
        stringBuilder.append('(');

        String prefix = "";

        for(Column col : columnList){
            stringBuilder.append(prefix);
            prefix = ", ";
            stringBuilder.append(col.getColumnName());
        }

        stringBuilder.append(')');

        String insertTableSQL = stringBuilder.toString();
        insertSQLFormat = String.format(INSERT_QUERY_FORMAT, insertTableSQL,"%s");
    }

    @Override
    public InsertResult insert(Object[] data){//insert
        genInsertSQL(data);
        return null;
    }

    @Override
    protected String genInsertSQL(Object[] data) {
        StringBuilder sb = new StringBuilder();
        String prefix = "";

        for (Object obj : data){

            sb.append(prefix);
            prefix=", ";


            sb.append('(');
            String prefix_ = "";
            for(Field f : mappingFields){
                try {
                    Object o = f.get(obj);

                    sb.append(prefix_);
                    prefix_=", ";

                    if(o.getClass().equals(String.class)
                            | o.getClass().equals(Timestamp.class)
                            | o.getClass().equals(Date.class)){
                        sb.append('\'');
                        sb.append(o.toString());
                        sb.append('\'');
                    }
                    else
                        sb.append(o.toString());

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            sb.append(')');
        }

        return String.format(insertSQLFormat, sb.toString());
    }


}
