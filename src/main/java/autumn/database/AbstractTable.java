package autumn.database;

import java.lang.reflect.Field;
import java.lang.reflect.MalformedParametersException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by infinitu on 14. 12. 12..
 */
public abstract class AbstractTable<DataType> {

    protected Class<DataType> dataTypeClass;
    protected List<Field> columns;
    protected List<Field> columnMapping;

    protected void mappingDataType(Class<DataType> dataTypeClass) throws NoSuchFieldException {
        if(dataTypeClass == null)
            throw new MalformedParametersException("dataTypeClass can not be null.");

        this.dataTypeClass = dataTypeClass;

        Class type = this.getClass();

        Field[] allField = type.getFields();
        columns= new LinkedList<>();


        //filter Column Type
        for(Field f : allField){
            if(f.getType().isAssignableFrom(Column.class))
                columns.add(f);
        }


        columnMapping = new LinkedList<>();

        for(Field f:columns){
            String name = f.getName();

            Field dataField = dataTypeClass.getField(name);

            columnMapping.add(dataField);
        }
    }

    protected abstract String toSQL();

    protected DataType makeData(ResultSet rs){
        DataType data;
        try {
             data = dataTypeClass.newInstance();
            int cnt = 1;
            for(Field f : columnMapping){
                try {
                    f.set(data, rs.getObject(cnt));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                cnt++;
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        return data;

    }


//    public <R> JoinTable<AbstractTable<DataType>, AbstractTable<R>, DefaultJoinData>
//        join(AbstractTable<R> rightTable,
//             BiFunction<AbstractTable<DataType>, AbstractTable<R>, Condition> conditionFunc) throws NoSuchFieldException {
//
//
//        return new JoinTable<AbstractTable<DataType>, AbstractTable<R>, DefaultJoinData>(this,rightTable,DefaultJoinData.class) {
//
//            @Override
//            public Condition on(AbstractTable<DataType> left, AbstractTable<R> right) {
//                return conditionFunc.apply(left,right);
//            }
//        };
//    }
}
