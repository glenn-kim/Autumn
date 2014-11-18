package autumn;

import autumn.annotation.*;
import org.reflections.Reflections;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by infinitu on 14. 10. 31..
 */
@WebServlet("/aaa/bbb")
public class Servlet extends HttpServlet{


    @Override
    public void init() throws ServletException {
        super.init();
        Reflections reflections = new Reflections("controllers");
        //reflections.getMethodsAnnotatedWith();


        Set<Class<?>> getMethod = reflections.getTypesAnnotatedWith(GET.class);
        Set<Class<?>> postMethod = reflections.getTypesAnnotatedWith(POST.class);
        Set<Class<?>> putMethod = reflections.getTypesAnnotatedWith(PUT.class);
        Set<Class<?>> deleteMethod = reflections.getTypesAnnotatedWith(DELETE.class);
        Set<Class<?>> updateMethod = reflections.getTypesAnnotatedWith(UPDATE.class);
    }

    private void makeRouteMap(Set<Class<?>> classSet){
        Map<String,?> map = new HashMap<String, Object>();
//        for(Class<?> c : classSet){
//            c.getAnnotation();
//        }
    }
}
