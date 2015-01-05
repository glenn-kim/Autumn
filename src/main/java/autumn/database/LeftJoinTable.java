package autumn.database;

/**
 * Created by infinitu on 14. 12. 12..
 */
public abstract class LeftJoinTable<Left extends AbstractTable,Right extends AbstractTable,DataType> extends JoinTable<Left,Right,DataType> {

    private static final String sqlStrFormat =  "%s LEFT JOIN %s ON %s";
    
    public LeftJoinTable(Left left, Right right, Class<DataType> dataTypeClass) throws NoSuchFieldException {
        super(left, right, dataTypeClass);
    }

    @Override
    protected String generateSqlStr(){
        return String.format(sqlStrFormat, left.toSQL(), right.toSQL(), joinCondition.toSQL());
    }
}
