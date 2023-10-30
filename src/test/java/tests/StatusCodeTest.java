package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class StatusCodeTest {

    @Test
    public void testStatusCode(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/check_type")
                .andReturn();

        int statusCode = response.getStatusCode();
        System.out.println(statusCode);

        Response response1 = RestAssured
                .get("https://playground.learnqa.ru/api/get_500")
                .andReturn();

        int statusCode1 = response1.getStatusCode();
        System.out.println(statusCode1);
    }
}
