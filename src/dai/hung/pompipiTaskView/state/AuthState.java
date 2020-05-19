package dai.hung.pompipiTaskView.state;

import dai.hung.pompipiTaskView.models.auth.TokenWriter;

public class AuthState {
    private static String token;
    private static String refreshToken;
    private static String email;
    private static String localId;

    public static void login(String newEmail,String newToken,String newRefreshToken,String newLocalId){
        token = newToken;
        email = newEmail;
        refreshToken = newRefreshToken;
        localId = newLocalId;
    }

    public static String getToken() {
        return token;
    }

    public static String getRefreshToken() {
        return refreshToken;
    }

    public static String getEmail() {
        return email;
    }

    public static String getLocalId(){
        return localId;
    }

    public static void setToken(String token) {
        AuthState.token = token;
    }

    public static void setRefreshToken(String refreshToken) {
        AuthState.refreshToken = refreshToken;
    }

    public static void logout(){
        token = null;
        refreshToken = null;
        email = null;
        localId = null;
        TokenWriter.deleteFile();
    }
}
