package autumn.database;

import java.lang.reflect.Field;
import java.lang.reflect.MalformedParametersException;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Created by infinitu on 14. 12. 12..
 */
public abstract class AbstractTable<DataType> {

    protected Class<DataType> dataTypeClass;
    protected Set<Field> columns;
    protected Set<Field> columnMapping;

    protected void mappingDataType(Class<DataType> dataTypeClass) throws NoSuchFieldException {
        if(dataTypeClass == null)
            throw new MalformedParametersException("dataTypeClass can not be null.");

        this.dataTypeClass = dataTypeClass;

        Class type = this.getClass();

        Field[] allField = type.getFields();
        columns= new HashSet<>(allField.length);


        //filter Column Type
        for(Field f : allField){
            if(f.getType().isAssignableFrom(Column.class))
                columns.add(f);
        }


        columnMapping = new HashSet<>(columns.size());

        for(Field f:columns){
            String name = f.getName();

            Field dataField = dataTypeClass.getField(name);

            Class t = ((ParameterizedType)f.getGenericType()).getActualTypeArguments()[0].getClass();
            if(!dataField.getClass().isAssignableFrom(t)){
                throw new MalformedParametersException(name+" can not be assignable from "+t.toString());
            }

            columnMapping.add(dataField);
        }
    }

    protected abstract String toSQL();


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
