package com.example.kento.devbookandroidclient;

/**
 * Created by Kento on 27/11/2016.
 */
public class Resources {

    public static final String SERVER_URL = "http://192.168.0.21:8080";
    public static final String SERVER_WEB_APP_NAME = SERVER_URL + "/DevBook/authentication";
    public static final String USERNAME = "userName";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    public static final String CITY ="city";
    public static final String COUNTRY ="country";
    public static final String CREATE_REQUEST = "/create-p";
    public static final String LOGIN_REQUEST = "/login-p";
    public static final String LOGOUT_REQUEST = "/logout-p";
    public static final String VALIDATE_REQUEST = "/validate-p";
    public static final String SERVER_RESPONSE = "SERVER_RESPONSE";
    public static final String LOGIUTCREDENTIALS = "logoutCredentials";


    public static String issueToken(String username) throws Exception {
        // Issue a token (can be a random String persisted to a database or a JWT token)
        // The issued token must be associated to a user
        // Return the issued token
        try {
            Encryption encrypter = Encryption.getDefaultEncrypter();
            return encrypter.encrypt(username);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Failed to create encrypted token");
        }
    }
}
