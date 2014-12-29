package autumn.result;

import com.google.gson.JsonElement;
import org.thymeleaf.TemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by infinitu on 14. 12. 10..
 */
public final class ResultResolver {
    private ResultResolver(){}

    public static TemplateResult template(int statusCode, TemplateEngine templateEngineInstance, String templateName){
        return new TemplateResult(statusCode, templateEngineInstance,templateName);
    }

    public static SendFileResult sendFile(int statusCode, String filePath){
        return new SendFileResult(statusCode, filePath);
    }

    public static SendFileResult sendFile(int statusCode, File file){
        return new SendFileResult(statusCode, file);
    }

    public  static PlainTextResult plainText(int statusCode, String text){
        return new PlainTextResult(statusCode, text);
    }

    public  static JsonResult jsonObject(int statusCode){
        return new JsonResult(statusCode, true);
    }

    public  static JsonResult jsonArray(int statusCode){
        return new JsonResult(statusCode, false);
    }
    public  static JsonResult json(int statusCode, Object object){
        return new JsonResult(statusCode, object);
    }
    public  static JsonResult json(int statusCode, JsonElement jsonElement){
        return new JsonResult(statusCode, jsonElement);
    }

    public static StaticResourceResult staticResource(int statusCode, File file) throws FileNotFoundException {
        return new StaticResourceResult(statusCode,file);
    }


}