package autumn.route;

import autumn.Request;
import autumn.Result;
import autumn.annotation.INP;
import org.junit.Assert;
import org.junit.Test;

import javax.management.MalformedObjectNameException;
import java.lang.reflect.Method;

import static autumn.route.PathRouter.PathNode;


/**
 * Created by infinitu on 14. 10. 31..
 */
public class PathRouterTest {

    @Test
    public void testMakingPathTree(){

        PathNode root = new PathNode(null);

        // it should create child nodes in degree 1.

        root.path("/aaa");
        root.path("/bbb");

        Assert.assertTrue(root.childNodes.size() == 2);
        Assert.assertTrue(root.childWildNode == null);

        // it should not create duplicated nodes
        root.path("/aaa");
        Assert.assertTrue(root.childNodes.size() == 2);


        // it should create whildcard child when input is in case "{~~}".

        root.path("/{aaa}");
        Assert.assertTrue(root.childWildNode != null);


        // it should create child nodes continuously

        root.path("/ccc").path("/ddd");

        Assert.assertTrue(
                root.childNodes.getOrDefault("ccc",new PathNode(null))
                    .childNodes.get("ddd")
                != null
        );

        // it should create all child nodes in degree over 2

        root.path("/eee/fff/ggg");

        Assert.assertTrue(
                root.childNodes.getOrDefault("eee",new PathNode(null))
                        .childNodes.getOrDefault("fff",new PathNode(null))
                        .childNodes.get("ggg")
                        != null
        );

    }

    @Test
    public void testMakingActionTree() throws MalformedObjectNameException {

        PathNode root = new PathNode(null);
        Method testMethod1=null,testMethod2=null;
        try {
            testMethod1 = this.getClass().getDeclaredMethod("dummyMethod_no_param");
            testMethod2 = this.getClass().getDeclaredMethod("dummyMethod_with_param", String.class, String.class);
        } catch (NoSuchMethodException e) {
            Assert.fail(e.getMessage());
        }

        // it should add action in route node.
        root.addAction("/aaa",testMethod1, PathRouter.REST_METHOD_ID_GET);

        Assert.assertTrue(
                testMethod1.equals(((PathNode) root.path("aaa")).actions[PathRouter.REST_METHOD_ID_GET].method)
        );


        // it should call method and return result.
        try{
            Assert.assertTrue(
                    Result.class.isInstance(
                            root.doAct(new Request(PathRouter.REST_METHOD_ID_GET, "/aaa"))
                    )
            );
        }
        catch(Exception e){
            Assert.fail(e.getMessage());
        }

        // it should add action that has some parameters and can call that action and return result.
        root.addAction("/{a}/{b}/aaa",testMethod2, PathRouter.REST_METHOD_ID_POST);

        try{
            Assert.assertTrue(
                    returns == root.doAct(new Request(PathRouter.REST_METHOD_ID_POST,
                                String.format("/%s/%s/aaa",testParam1,testParam2)))
            );
        }
        catch(Exception e){
            Assert.fail(e.getMessage());
        }


        // it should recognize method parameter names.
            root.addAction("/{b}/{a}/aaa",testMethod2, PathRouter.REST_METHOD_ID_POST);

        try{
            Assert.assertTrue(
                    Result.class.isInstance(
                            root.doAct( new Request(PathRouter.REST_METHOD_ID_POST,
                                    String.format("/%s/%s/aaa",testParam2,testParam1)))
                    )
            );
        }
        catch(Exception e){
            Assert.fail(e.getMessage());
        }

    }

    @SuppressWarnings("UnusedDeclaration")
    public static Result dummyMethod_no_param(){
        return new Result() {};
    }

    private static String testParam1 = "param1";

    private static String testParam2 = "param2";

    private static Result returns = new Result() {};

    @SuppressWarnings("UnusedDeclaration")
    public static Result dummyMethod_with_param(@INP("a") String a, @INP("b") String b){
        Assert.assertEquals(a,testParam1);
        Assert.assertEquals(b,testParam2);
        return returns;
    }

}
