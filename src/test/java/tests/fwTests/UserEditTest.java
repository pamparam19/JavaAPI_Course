package tests.fwTests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;

public class UserEditTest extends BaseTestCase {

    public final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    private static final String USER_URL = "https://playground.learnqa.ru/api/user/";
    private static final String USER_LOGIN_URL = "https://playground.learnqa.ru/api/user/login";
    private static final int BAD_REQUEST = 400;
    String cookie;
    String header;

    private String userIdForTest;

    @Test
    public void testEditJustCreated(){
        //generate user
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        String userId = responseCreateAuth.getString("id");

        //login
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //edit
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth,"x-csrf-token"))
                .cookies("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //get
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth,"x-csrf-token"))
                .cookies("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        System.out.println(responseUserData.asString());
        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    public void testEditNotAuth(){
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseNewUser = apiCoreRequests
                .makePostRequest(USER_URL,userData);

        userIdForTest = responseNewUser.jsonPath().getString("id");

        String changedUsername = "new username";
        Map<String, String> editData = new HashMap<>();
        editData.put("username", changedUsername);
        Response responseEditNotAuth = apiCoreRequests
                .makeNonAuthPutRequest(USER_URL + userIdForTest, editData);

        Assertions.assertResponseCodeEquals(responseEditNotAuth,BAD_REQUEST);
        Assertions.assertResponseTextEquals(responseEditNotAuth, "Auth token not supplied");
    }

    @Test
    public void testEditAuthAsAnotherUser(){

        //create the first user (to be changed)
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateFirstUser = apiCoreRequests
                .makePostRequest(USER_URL, userData);

        String userToBeChanged = responseCreateFirstUser.jsonPath().getString("id");

        // create user to change the first user
        Map<String, String> data = DataGenerator.getRegistrationData();
        Response responseUserForLogin = apiCoreRequests
                .makePostRequest(USER_URL, data);

        //login as the second user
        Map<String, String> authData = new HashMap<>();
        authData.put("email", data.get("email"));
        authData.put("password", data.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(USER_LOGIN_URL, authData);

        //edit the first user
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");

        Response responseAuthEdit = apiCoreRequests
                .makeAuthPutRequest(USER_URL + userToBeChanged,
                        header,
                        cookie,
                        editData);

        Assertions.assertResponseCodeEquals(responseAuthEdit, BAD_REQUEST);
    }

    @Test
    public void testEditEmail(){
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateFirstUser = apiCoreRequests
                .makePostRequest(USER_URL, userData);

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(USER_LOGIN_URL, authData);

        String newEmail = userData.get("email").replace("@", "");
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");

        Response responseAuthEdit = apiCoreRequests
                .makeAuthPutRequest(USER_URL + responseCreateFirstUser.jsonPath().getString("id"),
                        header,
                        cookie,
                        editData);

        Assertions.assertResponseCodeEquals(responseAuthEdit, BAD_REQUEST);
        Assertions.assertResponseTextEquals(responseAuthEdit,"Invalid email format");
    }

    @Test
    public void testChangeFirstName(){
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateFirstUser = apiCoreRequests
                .makePostRequest(USER_URL, userData);

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest(USER_LOGIN_URL, authData);

        String firstName = DataGenerator.getStringOfNeededLength(1);
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", firstName);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");

        Response responseAuthEdit = apiCoreRequests
                .makeAuthPutRequest(USER_URL + responseCreateFirstUser.jsonPath().getString("id"),
                        header,
                        cookie,
                        editData);

        Assertions.assertResponseCodeEquals(responseAuthEdit, BAD_REQUEST);
        Assertions.assertFieldValueEquals(responseAuthEdit, "error", "Too short value for field firstName");
    }
}
