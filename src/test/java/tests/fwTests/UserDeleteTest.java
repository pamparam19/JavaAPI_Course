package tests.fwTests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;

@Epic("Delete user cases")
@Feature("Deleting user")
public class UserDeleteTest extends BaseTestCase {
    public final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    private static final String USER_URL = "https://playground.learnqa.ru/api/user/";
    private static final String USER_LOGIN_URL = "https://playground.learnqa.ru/api/user/login";

    private static final int BAD_REQUEST = 400;
    private static final int SUCCESS = 200;

    private static final int NOT_FOUND = 404;
    private static final String TEST_EMAIL = "vinkotov@example.com";
    private static final String TEST_PASSWORD = "1234";
    private static final String TEST_USER_ID = "2";
    String cookie;
    String header;

    @Test
    @Description("This test checks that user with ID=2 cannot be deleted")
    @DisplayName("Delete a User with ID = 2")
    public void testDeleteTestUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", TEST_EMAIL);
        authData.put("password", TEST_PASSWORD);

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(USER_LOGIN_URL, authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");

        Response responseDelete = apiCoreRequests
                .makeDeleteRequest(USER_URL + TEST_USER_ID,header, cookie);

        Assertions.assertResponseCodeEquals(responseDelete, BAD_REQUEST);
        Assertions.assertResponseTextEquals(responseDelete,
                "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");

    }

    @Test
    @Description("The test checks that a newly created user can be successfully deleted after authorization")
    @DisplayName("Positive test, deleting a user")
    public void testDeleteJustCreated(){
        Map<String, String> regData = DataGenerator.getRegistrationData();
        Response newUser = apiCoreRequests
                .makePostRequest(USER_URL, regData);

        String userId = newUser.jsonPath().getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", regData.get("email"));
        authData.put("password", regData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(USER_LOGIN_URL, authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");

        Response responseDelete = apiCoreRequests
                .makeDeleteRequest(USER_URL + userId,header, cookie);

        Response responseGetAfterDelete = apiCoreRequests
                .makeGetRequest(USER_URL + userId, header, cookie);

        Assertions.assertResponseCodeEquals(responseGetAfterDelete, NOT_FOUND);
        Assertions.assertResponseTextEquals(responseGetAfterDelete, "User not found");
    }

    @Test
    @Description("The test checks that it is impossible to delete a user being logged in as another user")
    @DisplayName("Negative test. Deleting a user being logged in as another user")
    public void testDeleteAuthAnotherUser(){
        Map<String, String> regData = DataGenerator.getRegistrationData();
        Response newUser = apiCoreRequests
                .makePostRequest(USER_URL, regData);

        String userId = newUser.jsonPath().getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", TEST_EMAIL);
        authData.put("password", TEST_PASSWORD);

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(USER_LOGIN_URL, authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");

        Response responseDelete = apiCoreRequests
                .makeDeleteRequest(USER_URL + userId, header, cookie);
        responseDelete.then().log().all();

        Assertions.assertResponseCodeEquals(responseDelete, BAD_REQUEST);
    }

}
