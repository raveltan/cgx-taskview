package dai.hung.pompipiTaskView.models.auth;

import dai.hung.pompipiTaskView.models.ResultInterface;

import java.io.IOException;
import java.util.Map;

public class LoginModel implements Runnable {

    String username;
    String password;
    ResultInterface resultInterface;
    String error;

    public LoginModel(String username, String password, ResultInterface resultInterface) {
        this.username = username;
        this.password = password;
        this.resultInterface = resultInterface;
    }

    private Map doLogin(String user, String password) {
        Map result = ConnectionBase.request("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + ConnectionBase.apiKey, "POST",
                new String[]{"email", user}, new String[]{"password", password}, new String[]{"returnSecureToken", "true"});
        if (result != null && result.containsKey("error")) {
            try {
                String errorMessage = ((Map) result.get("error")).get("message").toString();
                switch (errorMessage) {
                    case "EMAIL_NOT_FOUND":
                        error = ("User with this email is not found.");
                        break;
                    case "INVALID_PASSWORD":
                        error = ("Invalid password.");
                        break;
                    case "USER_DISABLED":
                        error = ("This user is temporarily disabled.");
                        break;
                    default:
                        error = ("Unable to login.");
                }
            } catch (Exception e) {
                error = ("Unknown Error");
            }
        } else {
            try {
                TokenWriter.writeFile(result.get("idToken") +
                        "\n" +
                        result.get("refreshToken") + "\n" + result.get("email") + "\n" +
                        result.get("localId"));
            } catch (IOException e) {
                error = "Unable to write jwt!";
                e.printStackTrace();
            }
            return result;
        }
        return null;
    }

    @Override
    public void run() {
        Map result = doLogin(username, password);
        resultInterface.onFinish(result, error);
    }
}
