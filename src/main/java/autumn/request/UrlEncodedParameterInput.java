package autumn.request;

import autumn.util.KV;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by infinitu on 14. 12. 24..
 */
public class UrlEncodedParameterInput {
    protected List<KV> inputs;
    protected Map<String,String> map;

    public UrlEncodedParameterInput(String payload) {
        this.map = new HashMap<>();
        this.inputs = Parser.getInstance().parseKV(payload);
        inputs.forEach((kv)->map.put(kv.getKey(),(String)kv.getValue()));
    }

    public String getParam(String key){
        return map.get(key);
    }

    public List<KV> getInputs(){
        return inputs;
    }

    public final static class Parser{
        private static Parser instance;
        private final static String regex = "([^\\?=&]+)=([^\\?=&]+)";
        private Pattern pattern;

        public static Parser getInstance(){
            if(Parser.instance==null)
                Parser.instance = new Parser();
            return Parser.instance;
        }

        private Parser(){
            pattern = Pattern.compile(regex);
        }

        public List<KV> parseKV(String s){
            Matcher matches = pattern.matcher(s);
            List<KV> list = new LinkedList<>();

            while(matches.find()){
                KV<String> kv = new KV<>(matches.group(1),matches.group(2));
                list.add(kv);
            }

            return list;
        }
    }
}
