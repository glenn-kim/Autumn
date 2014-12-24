package autumn.request;

import autumn.Request;

import autumn.util.KV;
import org.junit.Test;
import org.junit.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by infinitu on 14. 12. 22..
 */
public class HttpQueryTest{

    String urlforminput = "?abc=def&123=456&aaa=bbb";
    KV[] urlformexpected = new KV[]{
            new KV<>("abc","def"),
            new KV<>("123","456"),
            new KV<>("aaa","bbb")
    };

    String jsoninput = "{\"aaa\":\"hello\",\"bbb\":999,\"ccc\":10.38}";
    testDataSet jsonExpected = new testDataSet("hello",999,10.38);

    @Test
    public void testFormUrlEncoded(){
        FormUrlEncodedPayload.Parser parser = FormUrlEncodedPayload.Parser.getInstance();
        List<KV> list = parser.parseKV(urlforminput);
        assertArrayEquals(list.toArray(), urlformexpected);
    }

    @Test
    public void testGetParameterInUrlEncodedForm(){

        Request req = new Request(0,null,urlforminput,null,null,null,new ByteArrayInputStream(urlforminput.getBytes()),null);
        assertArrayEquals(urlformexpected,req.getUrlQueryInputs().toArray());
    }

    @Test
    public void testBodyMappingToJSON(){
        Request req = new Request(0,null,urlforminput,null,null,null,new ByteArrayInputStream(jsoninput.getBytes()),null);
        testDataSet set = req.body().asJson().mapping(testDataSet.class);
        assertEquals(set,jsonExpected);

        assertEquals(jsonExpected.aaa, req.body().asJsonObject().get("aaa").getAsString());
    }

    public static class testDataSet{
        public testDataSet(){}
        public testDataSet(String aaa,int bbb,double ccc){this.aaa=aaa;this.bbb=bbb;this.ccc=ccc;}
        String aaa;
        int bbb;
        double ccc;

        @Override
        public boolean equals(Object obj) {
            testDataSet set = (testDataSet) obj;
            return aaa.equals(set.aaa) && bbb == set.bbb && ccc == set.ccc;
        }
    }

}
