package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {

    @Step("Make a GET-Request with token and auth cookie")
    public Response makeGetRequest(String url, String token, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token",token))
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-Request with auth cookie only")
    public Response makeGetRequestWithCookie(String url, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-Request with token only")
    public Response makeGetRequestWithToken(String url, String token){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token",token))
                .get(url)
                .andReturn();
    }

    @Step("Make a POST-Request")
    public Response makePostRequest(String url, Map<String,String> authData){
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .andReturn();
    }

    @Step("Make a PUT-Request not authorized")
    public Response makeNonAuthPutRequest(String url, Map<String, String> body){
        return given()
                .filter(new AllureRestAssured())
                .body(body)
                .put(url)
                .andReturn();
    }

    @Step("Make an authorized PUT-Request")
    public Response makeAuthPutRequest(String url, String header, String cookie, Map<String, String> body){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token",header))
                .cookie("auth_sid", cookie)
                .body(body)
                .put(url)
                .andReturn();
    }

    @Step("Make a DELETE-Request")
    public Response makeDeleteRequest(String url, String header, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token",header))
                .cookie("auth_sid", cookie)
                .delete(url)
                .andReturn();
    }


}
