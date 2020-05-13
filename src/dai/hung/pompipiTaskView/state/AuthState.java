package dai.hung.pompipiTaskView.state;

import dai.hung.pompipiTaskView.models.auth.TokenWriter;

public class AuthState {
    private static String token = "";
    private static String refreshToken = "";
    private static String email;


    public static void login(String newEmail,String newToken,String newRefreshToken){
        token = newToken;
        email = newEmail;
        refreshToken = newRefreshToken;
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

    public static void logout(){
        token = null;
        refreshToken = null;
        email = null;
        TokenWriter.deleteFile();
    }

}
