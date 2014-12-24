package autumn.request;

import autumn.util.LazyHolder;
import com.google.gson.*;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by infinitu on 14. 12. 22..
 */
public class JsonPayload {
    private static ThreadLocal<Gson> gson = new ThreadLocal<Gson>(){
        @Override
        protected Gson initialValue() {
            return new Gson();
        }
    };
    private static JsonParser jsonParsor = new JsonParser();

    private LazyHolder<JsonElement> jsonElem;

    protected JsonPayload(InputStream stream) {
        jsonElem = new LazyHolder<>(()->jsonParsor.parse(new InputStreamReader(stream)));
    }

    protected JsonPayload(String payload) {
        jsonElem = new LazyHolder<>(()->jsonParsor.parse(payload));
    }

    public <T> T mapping(Class<T> cls){
        return gson.get().fromJson(jsonElem.get(),cls);
    }

    public JsonArray arr(){
        return jsonElem.get().getAsJsonArray();
    }

    public JsonObject obj(){
        return jsonElem.get().getAsJsonObject();
    }

    public JsonElement elem(){
        return jsonElem.get();
    }

}
