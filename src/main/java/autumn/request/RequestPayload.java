package autumn.request;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by infinitu on 14. 12. 22..
 */
public class RequestPayload {
    private String contentType;
    private InputStream stream;

    private FormUrlEncodedPayload   urlEncodedPayload   = null;
    private JsonPayload             jsonPayload         = null;
    private XmlPayload              xmlPayload          = null;
    private MultiPartsPayload       multipartPayload    = null;
    private PlainTextPayload        plainTextPayload    = null;

    public static RequestPayload holdPayload(String contentType, InputStream stream){
        return new RequestPayload(contentType,stream);
    }

    private RequestPayload(String contentType, InputStream stream) {
        this.contentType = contentType;
        this.stream = stream;
    }

    public FormUrlEncodedPayload asFormUrlEncoded(){
        if(urlEncodedPayload==null)
            urlEncodedPayload = new FormUrlEncodedPayload(stream);
        return urlEncodedPayload;
    }

    public JsonPayload asJson(){
        if(jsonPayload==null)
            jsonPayload=new JsonPayload(stream);
        return jsonPayload;
    }

    public JsonArray asJsonArray(){
        return asJson().arr();
    }

    public JsonObject asJsonObject(){
        return asJson().obj();
    }

    public XmlPayload asXml(){
        //TODO Not Implemented
        if(xmlPayload == null)
            xmlPayload = new XmlPayload();
        return xmlPayload;
    }

    public MultiPartsPayload asMultiParts(){
        //TODO Not Implemented
        if(multipartPayload == null)
            multipartPayload = new MultiPartsPayload();
        return multipartPayload;
    }

    public PlainTextPayload asText(){
        //TODO Not Implemented
        if(plainTextPayload == null)
            plainTextPayload = new PlainTextPayload();
        return plainTextPayload;
    }

    public InputStream stream(){
        return stream;
    }



    // convert InputStream to String
    protected static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

}
