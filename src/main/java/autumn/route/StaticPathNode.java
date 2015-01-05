package autumn.route;

import autumn.Request;
import autumn.Result;
import autumn.result.ResultResolver;
import autumn.result.StaticResourceResult;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by infinitu on 15. 1. 5..
 */
public class StaticPathNode extends PathRouter.PathNode {
    @Override
    protected Result invokeAction(ListIterator<String> path, List<String> param, Request req) throws InvocationTargetException, IllegalAccessException {
        Result ret = super.invokeAction(path, param, req);
        if(ret == null && req.getPath().startsWith(StaticResourceResult.DEFAULT_STATIC_URI)) {
            String staticPath = StaticResourceResult.DEFAULT_STATIC_DIRECTORY +"/"+
                    req.getPath().substring(StaticResourceResult.DEFAULT_STATIC_URI.length());
            File res;
            try {
                res = new File(this.getClass().getClassLoader().getResource(staticPath).getFile());
                if (res.canRead())
                    return ResultResolver.staticResource(200, res);
            }
            catch (Exception e) {
                //no such resource;
                return null;
            }
        }
        return ret;
    }
}
