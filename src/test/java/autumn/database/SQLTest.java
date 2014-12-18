package autumn.database;

import autumn.Result;
import autumn.annotation.Model;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by infinitu on 14. 12. 17..
 */
public class SQLTest {

    private AbstractQuery<UserTable> userQuery;
    private AbstractQuery<UserJobJoin> joinQuery;
    private UserTable userTable;

    User u1 = new User();
    User u2 = new User();

    @Before
    public void setUp() throws Exception {
        userTable = new UserTable();

        u1.name="u1Name";
        u2.name="u2Name";

        u1.uid = 1;
        u2.uid = 2;

        u1.time = new Timestamp(System.currentTimeMillis());
        u2.time = new Timestamp(System.currentTimeMillis()-36000);

        userQuery = new TableQuery<>(UserTable.class);
        joinQuery = new JoinQuery<>(UserJobJoin.class);
    }

    @Test
    public void testCondition(){
        String tag = userTable.getTag();

        assertEquals(
                String.format("%s.name",tag),
                userTable.name.toSQL()
        );

        assertEquals(
                String.format("%s.uid >= 30",tag),
                (userTable.uid) .isBiggerOrEqual (30) .toSQL()
        );


        assertEquals(
                String.format("%s.uid = %s.uid",tag,tag),
                (userTable.uid) .isEqualTo (userTable.uid) .toSQL()
        );

        assertEquals(
                String.format("(%s.uid = 30) OR (%s.name = 'abc')", tag, tag),
                ((userTable.uid).isEqualTo(30)).or
                        ((userTable.name).isEqualTo("abc")).toSQL()
        );

    }


    @Test
    public void testTableQuery(){

        assertEquals(
                String.format("INSERT INTO users(uid, name, date) " +
                        "VALUES (%d, '%s', '%s'), (%d, '%s', '%s');",
                        u1.uid,u1.name,u1.time.toString(),  u2.uid,u2.name,u2.time.toString()) ,
                userQuery.genInsertSQL(new User[]{u1, u2})
        );


        String tag = userQuery.table.getTag();

        //select
        assertEquals(
                String.format("SELECT %s.uid, %s.name, %s.date FROM users %s WHERE %s.uid = 30;",tag,tag,tag,tag,tag) ,
                userQuery.where((t)-> (t.uid) .isEqualTo (30)).genListSQL()
        );


        //select first
        assertEquals(
                String.format("SELECT %s.uid, %s.name, %s.date FROM users %s WHERE %s.uid = 30 LIMIT 1;",tag,tag,tag,tag,tag) ,
                userQuery.where((t)-> (t.uid) .isEqualTo (30)).genFirstSQL()
        );

        //delete
        assertEquals(
                String.format("DELETE FROM users %s WHERE %s.uid = 30;",tag,tag) ,
                userQuery.where((t)-> (t.uid) .isEqualTo (30)).genDeleteSQL()
        );

        //TODO UPDATE
    }



    @Test
    public void testJoinQuery(){

        String ltag = joinQuery.table.left.getTag();
        String rtag = joinQuery.table.right.getTag();

        //TODO Insert

        //select
        assertEquals(
                String.format("SELECT %s.uid, %s.name, %s.job_name FROM users %s " +
                        "INNER JOIN jobs %s ON %s.uid = %s.user WHERE %s.uid = 30;", ltag, ltag, rtag, ltag, rtag, ltag, rtag, ltag),
                joinQuery.where((t) -> (t.left.uid).isEqualTo(30)).genListSQL()
        );


        //select first
        assertEquals(
                String.format("SELECT %s.uid, %s.name, %s.job_name FROM users %s " +
                        "INNER JOIN jobs %s ON %s.uid = %s.user WHERE %s.uid = 30 LIMIT 1;", ltag, ltag, rtag, ltag, rtag, ltag, rtag, ltag),
                joinQuery.where((t) -> (t.left.uid).isEqualTo(30)).genFirstSQL()
        );

        //delete
        assertEquals(
                String.format("DELETE FROM users %s INNER JOIN jobs %s " +
                        "ON %s.uid = %s.user WHERE %s.uid = 30;", ltag,rtag,ltag, rtag, ltag),
                joinQuery.where((t) -> (t.left.uid).isEqualTo(30)).genDeleteSQL()
        );

        //TODO UPDATE
    }


    @Test(timeout = 10000)
    public void testTableDataMap() throws SQLException {

        List<Object[]> list = new ArrayList<>();
        list.add(new Object[]{null,u1.uid,u1.name,u1.time});
        list.add(new Object[]{null,u2.uid,u2.name,u2.time});

        MockResultSet rs = new MockResultSet(list);

        assertArrayEquals(
                new User[]{u1,u2},
                userQuery.processQueryResult(rs).toArray()
        );
    }
}

@Model("users")
class UserTable extends Table<User>{
    public Column<Integer> uid = intColumn("uid");
    public Column<String> name = stringColumn("name");
    public Column<Timestamp> time = timestampColumn("date");

    public UserTable() throws NoSuchFieldException {
        super(User.class);
    }
}

class User{
    public int uid;
    public String name;
    public Timestamp time;

    @Override
    public boolean equals(Object obj) {
        try {
            User u = (User) obj;
            return
                u.uid == uid &&
                u.name.equals(name) &&
                u.time.equals(time);
        } catch (Exception e){
            return false;
        }
    }
}

@Model("jobs")
class JobTable extends Table<Job>{


    public JobTable() throws NoSuchFieldException {
        super(Job.class);
    }

    public Column<Integer> uid = intColumn("user");
    public Column<String> jobName = stringColumn("job_name");
    public Column<Long> salary = longColumn("salary");
}

class Job{
    public int uid;
    public String jobName;
    public long salary;
}

class UserJobJoin extends JoinTable<UserTable,JobTable,UserJob>{

    public UserJobJoin() throws NoSuchFieldException {
        super(new UserTable() , new JobTable(), UserJob.class);
    }


    @Override
    public Condition on(UserTable userTable, JobTable jobTable) {
        return (userTable.uid) .isEqualTo  (jobTable.uid);
    }

    public Column<Integer> uid = left.uid;
    public Column<String> name = left.name;
    public Column<String> jobName = right.jobName;
}

class UserJob{
    public int uid;
    public String name;
    public String jobName;

    @Override
    public boolean equals(Object obj) {
        try {
            UserJob u = (UserJob) obj;
            return
                u.uid == uid &&
                u.name.equals(name) &&
                u.jobName.equals(jobName);
        } catch (Exception e){
            return false;
        }
    }
}