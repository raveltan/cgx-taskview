package dai.hung.pompipiTaskView.controllers;

import com.jfoenix.controls.*;
import dai.hung.pompipiTaskView.models.Login;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.Map;

public class LoginController {
    private enum State{
        LOGIN,
        REGISTER,
    }
    public JFXTextField emailField;
    public JFXPasswordField passwordField;
    public Label emailErrorText;
    public Label passwordErrorText;
    private boolean firstStart = true;
    public JFXButton signInButton;
    public Label forgotPasswordButton;
    public Label titleText;
    private final EmailValidator validator = EmailValidator.getInstance();
    private State state = State.LOGIN;

    public void forgotPassword(MouseEvent mouseEvent) {
        //TODO:DH implement forgot password
    }

    public void signIn(ActionEvent actionEvent){
        boolean anyError = false;
        firstStart = false;
        if(!validator.isValid(emailField.getText())){
            emailErrorText.setText("Email is invalid");
            emailErrorText.setVisible(true);
            anyError = true;
        }
        if(passwordField.getText().length() < 8){
            passwordErrorText.setText("Password must be at least 8 characters");
            passwordErrorText.setVisible(true);
            anyError = true;
        }

        if(!anyError){
            if(state == State.LOGIN){
                // TODO:implement firebase signin
                try {
                    Map loginResult = Login.signIn(emailField.getText(),passwordField.getText());
                    if(loginResult != null) {
                        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                        Parent root = FXMLLoader.load(getClass().getResource("/fxml/screens/list.fxml"));
                        stage.setScene(new Scene(root, 800, 600));
                    }
                }catch (Exception e){
                    System.out.println(e);
                }
            }else {
                //TODO:implement firebase createNEwUser
                Login.register(emailField.getText(),passwordField.getText());
            }
        }
    }

    public void createAccount(ActionEvent actionEvent) {
        if(state == State.LOGIN){
            clearFields();
            state = State.REGISTER;
            signInButton.setText("Register");
            forgotPasswordButton.setVisible(false);
            titleText.setText("Register");
        } else{
            clearFields();
            state = State.LOGIN;
            signInButton.setText("Login");
            forgotPasswordButton.setVisible(true);
            titleText.setText("Login");
        }
    }

    private void clearFields(){
        emailField.setText("");
        passwordField.setText("");
        passwordErrorText.setVisible(false);
        emailErrorText.setVisible(false);
        firstStart = true;
    }

    public void checkEmail(KeyEvent inputMethodEvent) {
        if(!firstStart){
            emailErrorText.setVisible(!validator.isValid(emailField.getText()));
        }
    }

    public void checkPassword(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            this.signIn(null);
            return;
        }
        if(!firstStart){
            passwordErrorText.setVisible(!(passwordField.getText().length() > 7));
        }
    }

    public void submitForm(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)){
            this.signIn(null);
        }
    }

    /*
   <div>Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from
   <a href="https://www.flaticon.com/"     title="Flaticon">
   www.flaticon.com</a></div>
     */

}
