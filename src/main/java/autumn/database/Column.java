package autumn.database;

import java.lang.reflect.ParameterizedType;

import static autumn.database.Condition.ConditionComparer.*;

/**
 * Created by infinitu on 14. 12. 9..
 */
public class Column<Type> {
    private final String columnName;
    private final Tag tag;
    private final String sqlStr;
    private final Class<Type> typeClass;

    public Column(String columnName, Tag tag,Class<Type> type) {

        this.columnName = columnName;
        this.tag = tag;
        this.typeClass = type;
        sqlStr = tag.toString()+"." +columnName;
    }

    public Condition isBiggerThan(Column<Type> column){
        return new Condition(this,column,BIGGER_THAN);
    }

    public Condition isBiggerOrEqual(Column<Type> column){
        return new Condition(this,column,BIGGER_OR_EQUAL);
    }

    public Condition isSmallerThan(Column<Type> column){
        return new Condition(this,column,SMALLER_THAN);
    }

    public Condition isSmallerOrEqual(Column<Type> column){
        return new Condition(this,column,SMALLER_OR_EQUAL);
    }

    public Condition isEqualTo(Column<Type> column){
        return new Condition(this,column,EQUALS);
    }

    public Condition isLike(Column<Type> column){
        return new Condition(this,column,LIKE);
    }

    public Condition isBiggerThan(Type data){
        return new Condition(this,data,BIGGER_THAN);
    }

    public Condition isBiggerOrEqual(Type data){
        return new Condition(this,data,BIGGER_OR_EQUAL);
    }

    public Condition isSmallerThan(Type data){
        return new Condition(this,data,SMALLER_THAN);
    }

    public Condition isSmallerOrEqual(Type data){
        return new Condition(this,data,SMALLER_OR_EQUAL);
    }

    public Condition isEqualTo(Type data){
        return new Condition(this,data,EQUALS);
    }

    public Condition isLike(Type data){
        return new Condition(this,data,LIKE);
    }

    protected String toSQL(){
        return sqlStr;
    }
    protected String getColumnName(){
        return columnName;
    }

    protected Class<Type> getContentsType(){
        return typeClass;
    }

}
