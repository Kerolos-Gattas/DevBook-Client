package com.example.kento.devbookandroidclient;

/**
 * Created by Kento on 27/11/2016.
 */
public class Resources {

    public static final String SERVER_URL = "http://192.168.0.21:8080";
    public static final String SERVER_WEB_APP_NAME = SERVER_URL + "/DevBook/authentication";
    public static final String SERVER_WEB_APP_PROJECT = SERVER_URL + "/DevBook/project";


    public static final String USERNAME = "userName";
    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";
    public static final String CITY ="city";
    public static final String COUNTRY ="country";
    public static final String BIO = "bio";

    public static final String ADMIN = "admin";
    public static final String PROJECTTITLE = "projectName";
    public static final String DESCRIPTION = "description";
    public static final String TOOLS = "tools";
    public static final String LANGUAGES = "languages";

    public static final String CREATE_REQUEST = "/create-p";
    public static final String LOGIN_REQUEST = "/login-p";
    public static final String LOGOUT_REQUEST = "/logout-p";
    public static final String VALIDATE_REQUEST = "/validate-p";

    public static final String NEARBY_PROJECTS_REQUEST = "/projectsNearBy-p";
    public static final String PROJECTS_BY_ID_REQUEST = "/projectID-p";
    public static final String REQUEST_ADD = "/addRequest-p";
    public static final String MEMBER_ADD = "/addMember-p";
    public static final String GET_REQUESTS = "/projectsRequests-p";
    public static final String GET_USER_INFO = "/userBio-p";
    public static final String ACCEPT_REQUEST = "/acceptRequest-p";
    public static final String REFUSE_REQUEST = "/refuseRequest-p";
    public static final String CURRENT_PROJECTS = "/currentProjects-p";
    public static final String MESSAGES = "/getMessages-p";
    public static final String ADD_MESSAGE = "/addMessages-p";

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
