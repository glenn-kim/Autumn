package controllers;

import autumn.Request;
import autumn.Result;
import autumn.annotation.Controller;
import autumn.annotation.GET;
import autumn.annotation.INP;
import autumn.annotation.PATH;
import autumn.route.ControllerReflector;
import autumn.route.PathRouter;
import org.junit.Assert;
import org.junit.Test;

import javax.management.MalformedObjectNameException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by infinitu on 14. 11. 18..
 */
@Controller
@PATH("/aaa")
public class ControllerTest {

    @Test
    public void test(){

        PathRouter.ActionInvoker invoker=null;
        try {
            invoker = (new ControllerReflector()).generateActionInvoker();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
            Assert.fail();
            return;
        }
        try {
            Result result = invoker.doAct(new Request(PathRouter.REST_METHOD_ID_GET,String.format("/aaa/%s/%s",param1,param2)));
            if(result != returns) Assert.fail();

        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            Assert.fail();
            return;
        }

    }

    private static String param1 = "param1";
    private static String param2 = "param2";
    private static Result returns = new Result(200) {
        @Override
        protected void writeBody(OutputStream stream) {

        }
    };
    @GET("/{a}/{b}")
    public static Result testCode(@INP("a") String a, @INP("b") String b){
        Assert.assertEquals(a,param1);
        Assert.assertEquals(b,param2);
        return returns;
    }

}
