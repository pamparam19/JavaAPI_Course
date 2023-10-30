package lib;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataGenerator {

    public static String getRandomEmail(){
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return "learnqa" + timestamp + "@example.com";
    }

    public static String getRandomInvalidEmail(){
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return "learnqa" + timestamp + "example.com";
    }

    public static Map<String,String> getRegistrationData(){
        Map<String, String> data = new HashMap<>();
        data.put("email", getRandomEmail());
        data.put("password", "123");
        data.put("username", "learnqa");
        data.put("firstName", "learnqa");
        data.put("lastName", "learnqa");

        return data;
    }

    public static Map<String, String> getRegistrationData(Map<String, String> notDefaultValues){
        Map<String, String> defaultValues = getRegistrationData();

        Map<String, String> userData = new HashMap<>();
        String[] keys = {"email", "password", "username", "firstName", "lastName"};
        for(String key : keys){
            if(notDefaultValues.containsKey(key)){
                userData.put(key, notDefaultValues.get(key));
            } else {
                userData.put(key, defaultValues.get(key));
            }
        }
        return userData;
    }

    public static String getStringOfNeededLength(int length){
        String ABC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ ";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for(int i=0; i<length; i++){
            sb.append(ABC.charAt(rnd.nextInt(ABC.length())));
        }
        return sb.toString();
    }


}
