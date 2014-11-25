package autumn.route;

import autumn.Request;
import autumn.Result;
import autumn.annotation.INP;

import javax.annotation.Nullable;
import javax.management.MalformedObjectNameException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by infinitu on 14. 10. 31..
 */
public class PathRouter {

    public static final int REST_METHOD_ID_GET = 0;
    public static final int REST_METHOD_ID_POST = 1;
    public static final int REST_METHOD_ID_PUT = 2;
    public static final int REST_METHOD_ID_DELETE = 3;
    public static final int REST_METHOD_ID_UPDATE = 4;
    public static final int REST_METHOD_ID_OPTION = 5;
    public static final int REST_METHOD_ID_INFO = 6;
    public static final int REST_METHOD_ID_HEAD = 7;
    public static final int REST_METHOD_ID_CUSTUM = -1; //todo 커스텀 메서드 대응.

    private static final String WILD_CARD_REGEX_1 = "^\\{[^}]+\\}$";
    private static final String WILD_CARD_REGEX_2 = "^:[^:]+$";

    public static PathAllocator createNewRouter(){
        return new PathNode(null);
    }

    protected interface PathAllocator extends ActionInvoker{
        public PathAllocator path(String Path);

        public PathAllocator addAction(String actionPath, Method method, int RESTMethod)
                throws MalformedObjectNameException;
    }

    public interface ActionInvoker{
        public Result doAct(Request req) throws InvocationTargetException, IllegalAccessException;
    }

    protected static class PathNode implements PathAllocator{
        String nodeName;
        Map<String,PathNode> childNodes;
        PathNode childWildNode;
        ActionWrap[] actions = {null,null,null,null,null,null,null,null};

        //temporary status.
        List<String> whildcardNamespaceStatus = null;

        PathNode(String node){
            this.nodeName = node;
            this.childNodes = new HashMap<>();
            this.childWildNode = null;
        }

        PathNode(){
            this.nodeName = null;
            this.childNodes = new HashMap<>();
            this.childWildNode = null;
        }

        @Override
        public PathAllocator path(String path) {
            Iterator<String> iter = splitURLPath(path);
            List<String> nsStatus = whildcardNamespaceStatus;
            return createTree(iter,nsStatus);
        }

        @Override
        public PathAllocator addAction(@Nullable String pathAction, Method method, int RESTMethod) throws MalformedObjectNameException {
            if(pathAction != null && !pathAction.equals("")){
                path(pathAction).addAction(null,method,RESTMethod);
                return this;
            }

            ActionWrap action = new ActionWrap(method,whildcardNamespaceStatus);
            whildcardNamespaceStatus = null;
            this.actions[RESTMethod] = action;

            return this;
        }

        @Override
        public Result doAct(Request req) throws InvocationTargetException, IllegalAccessException {
            return invokeAction(splitURLPath(req.getPath()),new LinkedList<>(),req);
        }

        private ListIterator<String> splitURLPath(String path){
            String[] actionPathArr = path.split("/");
            LinkedList<String> pathList = new LinkedList<>();
            Collections.addAll(pathList,actionPathArr);
            if(pathList.getFirst().equals("")) pathList.removeFirst();
            return pathList.listIterator();
        }

        private PathAllocator createTree(Iterator<String> pathIter,
                                         @Nullable List<String> whildcardNamespace){
            if(!pathIter.hasNext()){
                this.whildcardNamespaceStatus = whildcardNamespace;
                return this;
            }

            String nodeName = pathIter.next();

            PathNode childNode;
            boolean isWildCard = false;
            if(nodeName.matches(WILD_CARD_REGEX_1)){
                isWildCard=true;
                nodeName = nodeName.substring(1,nodeName.length()-1);
            }
            else if(nodeName.matches(WILD_CARD_REGEX_2)){
                isWildCard=true;
                nodeName = nodeName.substring(1,nodeName.length());
            }


            if(isWildCard){
                childNode = childWildNode;

                if(whildcardNamespace == null)
                    whildcardNamespace = new ArrayList<>();

                if(childNode == null){
                    childNode = new PathNode();
                    childWildNode = childNode;
                }
                whildcardNamespace.add(nodeName);
                return childNode.createTree(pathIter,whildcardNamespace);
            }
            childNode = childNodes.get(nodeName);
            if(childNode == null){
                childNode = new PathNode(nodeName);
                childNodes.put(nodeName,childNode);
            }
            return childNode.createTree(pathIter,whildcardNamespace);
        }

        private Result invokeAction(ListIterator<String> path, List<String> param, Request req)
                throws InvocationTargetException, IllegalAccessException {

            if(!path.hasNext()){
                ActionWrap action = actions[req.method];
                if(action == null)
                    return null;
                return action.invoke(req,param);
            }
            String frag = path.next();
            PathNode namedChild = childNodes.get(frag);
            Result ret = null;
            if(namedChild != null){
                ret = namedChild.invokeAction(path,param,req);
            }
            if(ret==null && childWildNode != null){
                param.add(frag);
                ret = childWildNode.invokeAction(path, param, req);
                param.remove(param.size()-1);
            }

            path.previous();

            return ret;
        }

    }

    protected static class ActionWrap {
        Method method;
        int[] paramIdxMap;

        private ActionWrap(Method method, List<String> whildcardNamespace)
                throws MalformedObjectNameException {

            this.method = method;
            Parameter[] actionParam = method.getParameters();

            if(!Modifier.isStatic(method.getModifiers()))
                throw new MalformedParametersException("method should be static.");

            if(!Result.class.isAssignableFrom(method.getReturnType()))
                throw new MalformedParametersException("method should returns Result Type.");

            if(whildcardNamespace!=null){
                if(actionParam.length > whildcardNamespace.size()+1)
                    throw new MalformedParametersException("wrong whild card parameters.");
            }
            else{
                if(actionParam.length > 1)
                    throw new MalformedParametersException("wrong whild card parameters." +
                            " it request "+actionParam.length+" parameter");
            }

            paramIdxMap = new int[actionParam.length];
            for(int i = 0 ; i < actionParam.length ; i++){
                if(Request.class.equals(actionParam[i].getType())){
                    paramIdxMap[i] = -1;
                }
                else if(String.class.isAssignableFrom(actionParam[i].getType())){

                    String urlPatternParamName = null;
                    Annotation[] annotations = actionParam[i].getAnnotations();
                    for(Annotation anno : annotations){
                        if(INP.class.isAssignableFrom(anno.annotationType())){
                            urlPatternParamName = INP.class.cast(anno).value();
                            break;
                        }
                    }

                    if(urlPatternParamName == null)
                        throw new MalformedParametersException("Parameter should be annotated by @INP");

                    int paramIdx = whildcardNamespace != null ?
                                        whildcardNamespace.indexOf(urlPatternParamName) :
                                        -1;

                    if(paramIdx < 0)
                        throw new MalformedObjectNameException("can not find parameter '"+
                                actionParam[i].getName()+"' in url pattern");
                    paramIdxMap[i]=paramIdx;
                }
                else
                    throw new MalformedParametersException("Parameter Router only support type of String.");
            }
        }

        protected Result invoke(Request req, List<String> param)
                throws InvocationTargetException, IllegalAccessException {
            Object[] input = new Object[paramIdxMap.length];
            for(int i = 0 ; i < input.length ; i++){
                if(paramIdxMap[i]<0)
                    input[i]=req;
                else{
                    input[i] = param.get(paramIdxMap[i]);
                }
            }
            return (Result)method.invoke(null,input);
        }
    }
}
