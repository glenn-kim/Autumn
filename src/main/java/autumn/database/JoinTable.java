package autumn.database;

/**
 * Created by infinitu on 14. 12. 12..
 */
public class JoinTable<Left extends Table,Right extends Table,DataType> extends AbstractTable<DataType> {
    protected Left left;
    protected Right right;

    public JoinTable(Left left, Right right,Class<DataType> dataTypeClass) throws NoSuchFieldException {
        this.left = left;
        this.right = right;
        this.mappingDataType(dataTypeClass);
    }
}
