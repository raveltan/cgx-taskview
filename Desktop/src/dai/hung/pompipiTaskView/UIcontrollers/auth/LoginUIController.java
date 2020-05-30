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

/**
 * <h1>LoginUIController</h1>
 * A FXML Controller for the login view.
 * take care of logging in and registering
 * also takes care of validation
 *
 * @author Ravel Tanjaya
 * @version 1.1.0
 */
public class LoginUIController {
    @FXML
    private JFXButton createAccountButton;

    /**
     * State of the registration scene whether it's login or register.
     */
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
    private Label titleText;
    private final EmailValidator validator = EmailValidator.getInstance();
    private State state = State.LOGIN;

    /**
     * Redirect the scene to the projects if the email is not null
     */
    @FXML
    public void initialize(){
        if(AuthState.getEmail() != null){
            goToProjects();
        }
    }

    /**
     * Set whether the form is enabled
     * @param enabled
     */
    public void setFormEnabled(boolean enabled) {
        emailField.setEditable(enabled);
        passwordField.setEditable(enabled);
        signInButton.setDisable(!enabled);
        createAccountButton.setDisable(!enabled);
        titleText.setText(enabled ? state == State.LOGIN ? "Login" : "Register" : "Loading...");
    }

    /**
     * Sign/register in with email and password from the textfield,
     * perform validation before  singin/register
     * @param actionEvent event data from event source(ignored)
     */
    @FXML
    private void signIn(ActionEvent actionEvent) {
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

    /**
     * OnFinish callback that will be called after the REST request to server is done
     * will show auth error if any or will register the user and go to projects scene.
     * @param result
     * @param error
     */
    private void onFinish(Map result, String error){
        if (error != null) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
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

    /**
     * Go to projects scene.
     */
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
        });
    }

    /**
     * Change the state of the form to register or login.
     * @param actionEvent event data from event source(ignored)
     */
    @FXML
    private void createAccount(ActionEvent actionEvent) {
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

    /**
     * Reset the form
     */
    private void clearFields() {
        emailField.setText("");
        passwordField.setText("");
        passwordErrorText.setVisible(false);
        emailErrorText.setVisible(false);
        firstStart = true;
    }

    /**
     * A listener method that will be called on each keystroke
     * @param inputMethodEvent event data from event source(ignored)
     */
    @FXML
    private void checkEmail(KeyEvent inputMethodEvent) {
        if (!firstStart) {
            emailErrorText.setVisible(!validator.isValid(emailField.getText()));
        }
    }

    /**
     * A listener method that will be called on each keystroke
     * @param keyEvent event data from event source(ignored)
     */
    @FXML
    private void checkPassword(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            this.signIn(null);
            return;
        }
        if (!firstStart) {
            passwordErrorText.setVisible(!(passwordField.getText().length() > 7));
        }
    }
    /**
     * A listener method that will submit the form on enter key
     * @param keyEvent event data from event source(ignored)
     */
    @FXML
    private void submitForm(KeyEvent keyEvent) {
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
