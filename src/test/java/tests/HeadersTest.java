package tests;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class HeadersTest {

    @Test
    public void testHeaders(){

        Map<String,String> headers = new HashMap<>();
        headers.put("myHeader1", "myValue1");
        headers.put("myHeader2", "myValue2");

        Response response = RestAssured
                .given()
                .headers(headers)
                .when()
                .get("https://playground.learnqa.ru/api/show_all_headers")
                .andReturn();

        response.prettyPrint();

        Headers responseHeaders = response.getHeaders();
        System.out.println(responseHeaders);


        Response response1 = RestAssured
                .given()
                .redirects()
                .follow(false)
                .headers(headers)
                .when()
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();

        response1.prettyPrint();

        Headers responseHeaders1 = response1.getHeaders();
        String location = response1.getHeader("Location");
        System.out.println(responseHeaders1);
        System.out.println(location);
    }
}
