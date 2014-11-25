package autumn;

import autumn.annotation.*;
import autumn.route.ControllerReflector;
import autumn.route.PathRouter;
import org.reflections.Reflections;

import javax.management.MalformedObjectNameException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import autumn.route.PathRouter.*;

/**
 * Created by infinitu on 14. 10. 31..
 */
public class Servlet extends HttpServlet{

    PathRouter.ActionInvoker invoker;

    @Override
    public void init() throws ServletException {
        super.init();
        //System.out.println("init called");
        try{
            invoker = (new ControllerReflector()).generateActionInvoker();
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.getWriter().println("hello!!");
//    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();

        Result res = null;
        Request reqest = new Request(req);

        out.println(reqest.getPath());

        try {
            res = invoker.doAct(reqest);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace(out);
        }
        resp.setContentType("text/plain");
        out.println("result is ::");
        if (res != null) {
            out.print(res.toString());
        }
        else
        {
            out.print("null");
        }
        out.flush();
    }
}
