package dai.hung.pompipiTaskView.models;

import javafx.scene.control.Alert;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Login extends ConnectionBase{
    public static Map register(String email, String password){
        Map result = request("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key="+apiKey,"POST",
                    new String[] {"email",email},new String[] {"password",password},new String[] {"returnSecureToken","true"});
        if(result != null && result.containsKey("error")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            try {
                String errorMessage =((Map)result.get("error")).get("message").toString();
                switch (errorMessage){
                    case "EMAIL_EXISTS":
                        alert.setContentText("User with this email already exists.");break;
                    case "OPERATION_NOT_ALLOWED":
                        alert.setContentText("Registration is temporarily disallowed.");break;
                    case "TOO_MANY_ATTEMPTS_TRY_LATER":
                        alert.setContentText("Too many attempts, please try again later.");break;
                    default:
                        alert.setContentText("Unable to register.");
                }
                alert.show();
            }catch (Exception e) {
                alert.setContentText("Unknown Error");
                alert.show();
            }
        } else{
            return result;
        }
        return null;
    }

    public static Map signIn(String email, String password){
        Map result = request("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key="+apiKey,"POST",
                new String[] {"email",email},new String[] {"password",password},new String[] {"returnSecureToken","true"});
        if(result != null && result.containsKey("error")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            try {
                String errorMessage =((Map)result.get("error")).get("message").toString();
                switch (errorMessage){
                    case "EMAIL_NOT_FOUND":
                        alert.setContentText("User with this email is not found.");break;
                    case "INVALID_PASSWORD":
                        alert.setContentText("The email or password is invalid.");break;
                    case "USER_DISABLED":
                        alert.setContentText("This user is temporarily disabled.");break;
                    default:
                        alert.setContentText("Unable to login.");
                }
                alert.show();
            }catch (Exception e){
                alert.setContentText("Unknown Error");
                alert.show();
            }
        } else{
            return result;
        }
        return null;
    }
}
