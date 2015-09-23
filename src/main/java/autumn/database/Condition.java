package autumn.database;

import java.sql.Date;
import java.sql.Timestamp;

import static autumn.database.Condition.ConditionComparer.*;

/**
 * Created by infinitu on 14. 12. 9..
 */
public class Condition {
    private String sqlStr;

    public static enum ConditionComparer{
        BIGGER_THAN,
        SMALLER_THAN,
        BIGGER_OR_EQUAL,
        SMALLER_OR_EQUAL,
        EQUALS,
        NOT_EQUALS,
        LIKE
    }

    public static final Condition TRUE_CONDITION = null;//new Condition();


    protected <T> Condition(Column<T> left, Column<T> right, ConditionComparer compare){
        this(String.format("%s %s %s", left.toSQL(), getComparer(compare), right.toSQL()));
    }

    protected <T> Condition(T left, Column<T> right, ConditionComparer compare) {
        this(right,left,reverseComparer(compare));
    }

    protected <T> Condition(Column<T> left, T right, ConditionComparer compare){
        this(String.format("%s %s %s", left.toSQL(), getComparer(compare), toSQLStr(right)));
    }

    private Condition(String sqlStr){
        this.sqlStr = sqlStr;
    }

    private static ConditionComparer reverseComparer(ConditionComparer compare){
        switch (compare){
            case BIGGER_THAN:
                return SMALLER_THAN;
            case SMALLER_THAN:
                return BIGGER_THAN;
            case BIGGER_OR_EQUAL:
                return SMALLER_OR_EQUAL;
            case SMALLER_OR_EQUAL:
                return BIGGER_OR_EQUAL;
            case EQUALS:
                return EQUALS;
            case NOT_EQUALS:
                return NOT_EQUALS;
            case LIKE:
                return LIKE;

        }
        return null;
    }

    private static String getComparer(ConditionComparer compare){
        switch (compare){
            case BIGGER_THAN:
                return ">";
            case SMALLER_THAN:
                return "<";
            case BIGGER_OR_EQUAL:
                return ">=";
            case SMALLER_OR_EQUAL:
                return "<=";
            case EQUALS:
                return "=";
            case NOT_EQUALS:
                return "<>";
            case LIKE:
                return "LIKE";
        }
        return null;
    }

    public Condition or(Condition condition){
        return new Condition(String.format("(%s) OR (%s)",this.toSQL(),condition.toSQL()));
    }
    public Condition and(Condition condition){
        return new Condition(String.format("(%s) AND (%s)",this.toSQL(),condition.toSQL()));
    }
    protected String toSQL(){
        return sqlStr;
    }
    private static String toSQLStr(Object obj){
        if(obj.getClass().equals(String.class)
                | obj.getClass().equals(Timestamp.class)
                | obj.getClass().equals(Date.class)){
            return String.format("'%s'",filterQuots(obj.toString()));
        }
        return obj.toString();
    }

    private static String filterQuots(String str){
        return str.replace("'", "''");
    }

}
