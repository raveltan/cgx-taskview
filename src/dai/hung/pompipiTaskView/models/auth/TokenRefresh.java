package dai.hung.pompipiTaskView.models.auth;

import dai.hung.pompipiTaskView.models.ResultInterface;
import dai.hung.pompipiTaskView.state.AuthState;

import java.io.IOException;
import java.util.Map;

public class TokenRefresh implements Runnable{

    String idToken;
    ResultInterface resultInterface;

    public TokenRefresh(String idToken, ResultInterface resultInterface) {
        this.idToken = idToken;
        this.resultInterface = resultInterface;
    }

    private Map doRefresh(String token) {
        Map result = ConnectionBase.request("https://securetoken.googleapis.com/v1/token?key=" + ConnectionBase.apiKey, "POST",
                new String[]{"refresh_token", token}, new String[]{"grant_type", "refresh_token"});
        if (result != null && result.containsKey("error")) {
            try {
                String errorMessage = ((Map) result.get("error")).get("message").toString();
                switch (errorMessage) {
                    case "USER_NOT_FOUND":
                        System.out.println(" The user corresponding to the refresh token was not found. It is likely the user was deleted.");
                        break;
                    case "USER_DISABLED":
                        System.out.println("The user account has been disabled by an administrator.");
                        break;
                    case "TOKEN_EXPIRED":
                        System.out.println("The user's credential is no longer valid. The user must sign in again.");
                        break;
                    case "Invalid JSON payload received":
                        System.out.println("Unknown name \\\"refresh_tokens\\\": Cannot bind query parameter. Field 'refresh_tokens' could not be found in request message.");
                        break;
                    case "INVALID_GRANT_TYPE":
                        System.out.println("the grant type specified is invalid.");
                        break;
                    default:
                        System.out.println("Unknown Error - Token Refresh");
                }
            } catch (Exception e) {
                System.out.println("Error - Token Refresh");
            }
        } else {
            try {
                System.out.println(result);
                TokenWriter.writeFile(result.get("id_token") +
                        "\n" +
                        result.get("refresh_token") + "\n" + AuthState.getEmail() + "\n" +
                        AuthState.getLocalId());
                AuthState.setToken(result.get("id_token").toString());
                AuthState.setRefreshToken(result.get("refresh_token").toString());
            } catch (IOException e) {
                System.out.println("Unable to write JWT");
                e.printStackTrace();
            }
            return result;
        }
        return null;
    }

    @Override
    public void run() {
        doRefresh(idToken);
        resultInterface.onFinish(null,null);
    }
}
