package autumn.result;

import autumn.Result;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by infinitu on 14. 12. 4..
 */
public class TemplateResult extends Result {

    private String templateName;
    private AbstractContext ctx = null;
    private TemplateEngine templateEngine = null;
    private Map<String,Object> variableMap = new LinkedHashMap<>();

    protected TemplateResult(int status, TemplateEngine templateEngine, String templateName) {
        super(status);
        this.templateEngine = templateEngine;
        this.templateName = templateName;
        this.contentType(HTML);
    }

    public TemplateResult withVariable(String key, Object value){
        variableMap.put(key,value);
        return this;
    }

    @Override
    protected void writeBody(OutputStream stream) throws IOException {
        if(ctx == null){
            ctx = new Context();
        }
        ctx.setVariables(variableMap);
        Writer writer = new OutputStreamWriter(stream);
        templateEngine.process(templateName,ctx, writer);
        writer.flush();
    }

    @Override
    protected void writeBodyServlet( HttpServletRequest request,
                                     HttpServletResponse response,
                                     ServletContext servletContext,
                                     OutputStream stream ) throws IOException {
        ctx = new WebContext(request, response, servletContext);
        writeBody(stream);
    }
}
