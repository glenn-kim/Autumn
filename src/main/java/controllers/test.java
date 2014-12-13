package controllers;

import autumn.Request;
import autumn.Result;
import autumn.annotation.*;
import autumn.header.Cookie;
import autumn.header.session.SessionData;

/**
 * Created by infinitu on 14. 11. 18..
 */

@Controller
//@PATH("/user")
public class test {

    @GET("/{aaa}/:bbb")
    public static Result echo(@INP("aaa")String param,
                              @INP("bbb")String param2,
                              Request request){

        String cntStr = (String) request.getSession("cnt");

        if(cntStr==null) cntStr="0";
        int cnt = Integer.parseInt(cntStr);

        cnt++;

        return Result.Ok.plainText(param +"  "+ cnt + "th call")
                .with(new Cookie("testCookie", "testHOHOHO"))
                .with(new SessionData<>("cnt", cnt + "")).
                        .

    }



    @GET("/template/:message")
    public static Result templateTest(@INP("message")String message, Request request){

        return Result.Ok.template("thymtest")
                .withVariable("testtext",message);

    }
}