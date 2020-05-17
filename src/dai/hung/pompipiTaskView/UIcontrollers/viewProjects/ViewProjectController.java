package dai.hung.pompipiTaskView.UIcontrollers.viewProjects;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import dai.hung.pompipiTaskView.state.AuthState;
import dai.hung.pompipiTaskView.UIcontrollers.widgets.Tile;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewProjectController {
    public Label emailText;
    public VBox projectList;
    public Label errorText;
    public JFXTextField projectTextFiled;
    public JFXButton logoutButton;
    public JFXButton createProjectButton;


    @FXML
    public void initialize(){
        emailText.setText(AuthState.getEmail());
        errorText.setVisible(false);
        addProjectList("Project 1");
        addProjectList("Project 2");
        setEnabled(true);
    }

    private void setEnabled(boolean enabled){
        projectTextFiled.setEditable(enabled);
        createProjectButton.setDisable(!enabled);
        logoutButton.setDisable(!enabled);
        projectList.setDisable(!enabled);
    }

    public void logout(ActionEvent actionEvent) {
        AuthState.logout();
        goToLogin();
    }

    public void createProject(ActionEvent actionEvent) {

    }
    private void goToLogin(){
        Platform.runLater(() -> {
            Stage stage = (Stage) ((Node) emailText).getScene().getWindow();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("/fxml/screens/login.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(new Scene(root, 800, 600));
            // TODO : Handle on close to terminate application
        });
    }
    private void addProjectList(String title){
        try {
            FXMLLoader load = new FXMLLoader(getClass().getResource("/fxml/widgets/project-tile.fxml"));
            load.setController(new Tile(title));
            projectList.getChildren().add(load.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
