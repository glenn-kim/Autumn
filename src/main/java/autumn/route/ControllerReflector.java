package autumn.route;

import autumn.annotation.*;
import org.reflections.Reflections;

import javax.management.MalformedObjectNameException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by infinitu on 14. 11. 18..
 */
public class ControllerReflector {

    private List<Class> controllerList = new ArrayList<>();

    public ControllerReflector(){
        this("controllers");
    }

    public ControllerReflector(String anotherPackage){
        this(new String[]{anotherPackage});
    }

    public ControllerReflector(String[] anotherPackages){

        for(String s : anotherPackages){
            Reflections reflections = new Reflections(s);
            controllerList.addAll(reflections.getTypesAnnotatedWith(Controller.class));
        }

    }

    public PathRouter.ActionInvoker generateActionInvoker() throws MalformedObjectNameException {

        PathRouter.PathAllocator pathRouter = PathRouter.createNewRouter();

        for(Class cls : controllerList){
            PathRouter.PathAllocator innerNode = pathRouter;
            Annotation anno = cls.getAnnotation(PATH.class);
            if(anno !=null){
                innerNode = pathRouter.path(PATH.class.cast(anno).value());
            }

            Method[] methods = cls.getMethods();

            for(Method method:methods){
                int httpMethod = -1;
                String path = "";
                if(method.isAnnotationPresent(GET.class)){
                    httpMethod = PathRouter.REST_METHOD_ID_GET;
                    path = method.getAnnotation(GET.class).value();
                }
                else if(method.isAnnotationPresent(POST.class)){
                    httpMethod = PathRouter.REST_METHOD_ID_POST;
                    path = method.getAnnotation(POST.class).value();
                }
                else if(method.isAnnotationPresent(PUT.class)){
                    httpMethod = PathRouter.REST_METHOD_ID_PUT;
                    path = method.getAnnotation(PUT.class).value();
                }
                else if(method.isAnnotationPresent(DELETE.class)){
                    httpMethod = PathRouter.REST_METHOD_ID_DELETE;
                    path = method.getAnnotation(DELETE.class).value();
                }
                else if(method.isAnnotationPresent(UPDATE.class)){
                    httpMethod = PathRouter.REST_METHOD_ID_UPDATE;
                    path = method.getAnnotation(UPDATE.class).value();
                }

                if(httpMethod < 0) continue;

                innerNode.addAction(path,method,httpMethod);

            }

        }

        return pathRouter;
    }
}
