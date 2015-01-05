package autumn.result;

import autumn.Result;
import com.google.gson.*;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by infinitu on 14. 12. 8..
 */
public class JsonResult extends Result<JsonResult> {
    private static ThreadLocal<Gson> gson = new ThreadLocal<Gson>(){
        @Override
        protected Gson initialValue() {
            GsonBuilder gb = new GsonBuilder();
            return gb.create();
        }
    };

    private JsonElement elem;
    private static JsonElement makeNewElem(boolean jsonObject){
        if(jsonObject)
            return new JsonObject();
        else
            return new JsonArray();
    }
    protected JsonResult(int status, boolean isJsonObject) {
        this(status,makeNewElem(isJsonObject));
    }
    protected JsonResult(int status, Object src) {
        this(status, gson.get().toJsonTree(src));
    }

    protected JsonResult(int status, JsonElement elem) {
        super(status);
        this.contentType("application/json");
        this.elem = elem;
    }

    public JsonObject getElementAsJsonObject(){
        return elem.getAsJsonObject();
    }

    public JsonArray getElementAsJsonArray(){
        return elem.getAsJsonArray();
    }

    public JsonElement getElement(){
        return elem;
    }


    @Override
    protected void writeBody(OutputStream stream) {
        try {
            stream.write(gson.get().toJson(elem).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
