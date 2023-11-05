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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static lib.DataGenerator.getRandomInvalidEmail;

@Epic("User registration cases")
@Feature("Registration")
public class UserRegisterTest extends BaseTestCase {
    private static final String USER_URL = "https://playground.learnqa.ru/api/user/";
    private static final int BAD_REQUEST = 400;

    public final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("The test checks that it is impossible to create a new user with an email that is already in use")
    @DisplayName("Negative case, Register a new user with an email already in use")
    public void testCreateUserWithExistingEmail(){
        String email = "vinkotov@example.com";
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);


        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test
    @Description("The test for a successful registration of a new user with valid registration data")
    @DisplayName("Test positive new user registration")
    public void createUserSuccessfully(){
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();


        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
    @Description("The test checks that it is impossible to create a new user with an invalid email")
    @DisplayName("Test negative user registration with an invalid email")
    public void createUserInvalidEmail(){
        String email = getRandomInvalidEmail();
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.replace("email", email);

        Response responseInvalidEmail = apiCoreRequests
                .makePostRequest(USER_URL, userData);

        Assertions.assertResponseCodeEquals(responseInvalidEmail, BAD_REQUEST);
        Assertions.assertResponseTextEquals(responseInvalidEmail, "Invalid email format");
    }


    @ParameterizedTest
    @Description("The test checks user registration data validation for every required attribute")
    @DisplayName("Test negative user registration case when any of the required attributes missing")
    @ValueSource(strings = {"username", "firstName", "lastName", "email", "password"})
    public void testInvalidRequestValidation(String attribute){
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.remove(attribute);
        Response responseInvalidRequest = apiCoreRequests
                .makePostRequest(USER_URL, userData);

        Assertions.assertResponseCodeEquals(responseInvalidRequest, BAD_REQUEST);
        Assertions.assertResponseTextEquals(responseInvalidRequest,
                "The following required params are missed: " + attribute);
    }

    @Test
    @Description("The test checks that it is not possible to create a new user with invalid first name value")
    @DisplayName("Test negative user registration case, first name is too short")
    public void testShortNameReg(){
        String firstName = DataGenerator.getStringOfNeededLength(1);
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.replace("firstName", firstName);

        Response responseShortName = apiCoreRequests
                .makePostRequest(USER_URL, userData);

        Assertions.assertResponseCodeEquals(responseShortName, BAD_REQUEST);
        Assertions.assertResponseTextEquals(responseShortName,
                "The value of 'firstName' field is too short");
    }

    @Test
    @Description("The test checks that it is not possible to create a new user with invalid first name value")
    @DisplayName("Test negative user registration case, first name is too short")
    public void testLongNameReg(){
        String firstName = DataGenerator.getStringOfNeededLength(251);
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.replace("firstName", firstName);

        Response responseLongName = apiCoreRequests
                .makePostRequest(USER_URL, userData);

        Assertions.assertResponseCodeEquals(responseLongName, BAD_REQUEST);
        Assertions.assertResponseTextEquals(responseLongName, "The value of 'firstName' field is too long");

    }




}
