package tests.fwTests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Get user data cases")
@Feature("Get user information")

public class UserGetTest extends BaseTestCase {
    private static final String USER_URL = "https://playground.learnqa.ru/api/user/";
    private static final String LOGIN_URL = "https://playground.learnqa.ru/api/user/login";

    private static final String GET_INFO_URL = "https://playground.learnqa.ru/api/user/2";
    private static final int BAD_REQUEST = 400;

    public final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testGetUserDataNotAuth(){
        Response responseUserDate = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        Assertions.assertJsonHasField(responseUserDate, "username");
        Assertions.assertJsonHasNotField(responseUserDate, "firstName");
        Assertions.assertJsonHasNotField(responseUserDate, "lastName");
        Assertions.assertJsonHasNotField(responseUserDate, "email");
    }

    @Test
    @Description("The test checks that an authorised user can get full user information")
    @DisplayName("Positive test, getting user info after authorization")
    public void testGetUserDataAuthAsSameUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        String header = this.getHeader(responseAuth, "x-csrf-token");
        String cookie = this.getCookie(responseAuth, "auth_sid");

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        String[] expectedFields = {"username", "firstName", "lastName", "email"};

        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    @Test
    @Description("The test checks that it is impossible to get full user information being authorized as another user")
    @DisplayName("Negative case, getting user information being logged in as another user")
    public void testGetUserDataAuthAsDifUser(){
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseRegNewUser = apiCoreRequests
                .makePostRequest(USER_URL, userData);

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseAuthNewUser = apiCoreRequests
                .makePostRequest(LOGIN_URL, authData);

        String header = this.getHeader(responseAuthNewUser, "x-csrf-token");
        String cookie = this.getCookie(responseAuthNewUser, "auth_sid");

        Response responseUserData = apiCoreRequests
                .makeGetRequest(GET_INFO_URL, header, cookie);

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }
}
