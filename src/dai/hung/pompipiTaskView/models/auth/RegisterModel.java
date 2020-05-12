package dai.hung.pompipiTaskView.models.auth;

import dai.hung.pompipiTaskView.models.ConnectionBase;
import dai.hung.pompipiTaskView.models.ResultInterface;

import java.io.IOException;
import java.util.Map;

public class RegisterModel implements Runnable {

    String username;
    String password;
    ResultInterface resultInterface;
    String error;

    public RegisterModel(String username, String password, ResultInterface resultInterface) {
        this.username = username;
        this.password = password;
        this.resultInterface = resultInterface;
    }

    private Map doRegister(String username, String password) {
        Map result = ConnectionBase.request("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key="+ConnectionBase.apiKey,"POST",
                new String[] {"email",username},new String[] {"password",password},new String[] {"returnSecureToken","true"});
        if(result != null && result.containsKey("error")){
            try {
                String errorMessage =((Map)result.get("error")).get("message").toString();
                switch (errorMessage) {
                    case "EMAIL_EXISTS":
                        error = ("User with this email already exists.");
                        break;
                    case "OPERATION_NOT_ALLOWED":
                        error = ("Registration is temporarily disallowed.");
                        break;
                    case "TOO_MANY_ATTEMPTS_TRY_LATER":
                        error = ("Too many attempts, please try again later.");
                        break;
                    default:
                        error = ("Unable to register.");
                }
            }catch (Exception e) {
                error = "Unknown Error";
            }
        } else {
            try {
                TokenWriter.writeFile(result.get("idToken") +
                        "\n" +
                        result.get("refreshToken"));
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
        Map result = doRegister(username, password);
        resultInterface.onFinish(result,error);
    }
}
