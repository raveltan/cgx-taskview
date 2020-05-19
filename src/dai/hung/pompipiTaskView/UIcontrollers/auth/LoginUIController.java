package dai.hung.pompipiTaskView.UIcontrollers.auth;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import dai.hung.pompipiTaskView.models.auth.LoginModel;
import dai.hung.pompipiTaskView.models.auth.RegisterModel;
import dai.hung.pompipiTaskView.state.AuthState;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.apache.commons.validator.routines.EmailValidator;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginUIController {
    public JFXButton createAccountButton;

    private enum State {
        LOGIN,
        REGISTER,
    }

    @FXML
    private JFXTextField emailField;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private Label emailErrorText;
    @FXML
    private Label passwordErrorText;
    private boolean firstStart = true;
    @FXML
    private JFXButton signInButton;
    @FXML
    private Label forgotPasswordButton;
    @FXML
    private Label titleText;
    private final EmailValidator validator = EmailValidator.getInstance();
    private State state = State.LOGIN;

    @FXML
    public void initialize(){
        if(AuthState.getEmail() != null){
            goToProjects();
        }
    }

    public void setFormEnabled(boolean enabled) {
        emailField.setEditable(enabled);
        passwordField.setEditable(enabled);
        signInButton.setDisable(!enabled);
        createAccountButton.setDisable(!enabled);
        titleText.setText(enabled ? state == State.LOGIN ? "Login" : "Register" : "Loading...");
    }

    public void signIn(ActionEvent actionEvent) {
        boolean anyError = false;
        firstStart = false;
        if (!validator.isValid(emailField.getText())) {
            emailErrorText.setText("Email is invalid");
            emailErrorText.setVisible(true);
            anyError = true;
        }
        if (passwordField.getText().length() < 8) {
            passwordErrorText.setText("Password must be at least 8 characters");
            passwordErrorText.setVisible(true);
            anyError = true;
        }

        if (!anyError) {
            ExecutorService executor = Executors.newFixedThreadPool(1);
            setFormEnabled(false);
            if (state == State.LOGIN) {
                executor.execute(new LoginModel(emailField.getText(), passwordField.getText(), (result, error) -> {
                   onFinish(result,error);
                }));
            } else {
                executor.execute(new RegisterModel(emailField.getText(), passwordField.getText(), (result, error) -> {
                    onFinish(result,error);
                }));
            }
        }
    }

    private void onFinish(Map result, String error){
        if (error != null) {
            Platform.runLater(() -> {
//                emailErrorText.setVisible(true);
//                emailErrorText.setText(error);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failed");
                alert.setContentText(error);
                alert.show();
                setFormEnabled(true);
            });
        } else {
            AuthState.login(
                    result.get("email").toString(),
                    result.get("idToken").toString(),
                    result.get("refreshToken").toString(),
                    result.get("localId").toString()
            );
            goToProjects();
        }
    }

    private void goToProjects(){
        Platform.runLater(() -> {
            setFormEnabled(true);
            Stage stage = (Stage) ((Node) emailField).getScene().getWindow();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("/fxml/screens/view-project.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(root, 800, 600));
            // TODO : Handle on close to terminate application
        });
    }


    public void createAccount(ActionEvent actionEvent) {
        if (state == State.LOGIN) {
            createAccountButton.setText("Log In");
            clearFields();
            state = State.REGISTER;
            signInButton.setText("Register");
            titleText.setText("Register");
        } else {
            createAccountButton.setText("Create a new Account");
            clearFields();
            state = State.LOGIN;
            signInButton.setText("Login");
            titleText.setText("Login");
        }
    }

    private void clearFields() {
        emailField.setText("");
        passwordField.setText("");
        passwordErrorText.setVisible(false);
        emailErrorText.setVisible(false);
        firstStart = true;
    }

    public void checkEmail(KeyEvent inputMethodEvent) {
        if (!firstStart) {
            emailErrorText.setVisible(!validator.isValid(emailField.getText()));
        }
    }

    public void checkPassword(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            this.signIn(null);
            return;
        }
        if (!firstStart) {
            passwordErrorText.setVisible(!(passwordField.getText().length() > 7));
        }
    }

    public void submitForm(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            this.signIn(null);
        }
    }

    /*
   <div>Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from
   <a href="https://www.flaticon.com/"     title="Flaticon">
   www.flaticon.com</a></div>
     */

}
