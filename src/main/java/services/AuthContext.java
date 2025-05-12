package services;

public class AuthContext {

    private static AuthContext instance;
    private String userId;
    private String userEmail;
    private String accessToken;

    private AuthContext() {}

    public static AuthContext getInstance() {
        if (instance == null) {
            instance = new AuthContext();
        }
        return instance;
    }

    public void setSession(String userId, String userEmail, String accessToken) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.accessToken = accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return userEmail;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void clearSession() {
        userId = null;
        userEmail = null;
        accessToken = null;
    }
}
