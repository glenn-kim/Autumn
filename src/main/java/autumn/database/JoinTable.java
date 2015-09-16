package autumn.database;

/**
 * Created by infinitu on 14. 12. 12..
 */
public abstract class JoinTable<Left extends AbstractTable,Right extends AbstractTable,DataType> extends AbstractTable<DataType> {
    public Left left;
    public Right right;
    protected Condition joinCondition;
    private static final String sqlStrFormat =  "%s INNER JOIN %s ON %s";
    private String sqlStr;


    public JoinTable(Left left, Right right,Class<DataType> dataTypeClass) throws NoSuchFieldException {
        this.left = left;
        this.right = right;
        this.mappingDataType(dataTypeClass);
        this.joinCondition = on(left,right);
        sqlStr = generateSqlStr();
    }
    
    protected String generateSqlStr(){
        return String.format(sqlStrFormat,left.toSQL(),right.toSQL(),joinCondition.toSQL());
    }


    public abstract Condition on(Left left, Right right);

    @Override
    protected String toSQL() {
        return this.generateSqlStr();
    }
}
