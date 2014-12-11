package autumn.result;

import org.thymeleaf.TemplateEngine;

import java.io.File;

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


}