package dai.hung.pompipiTaskView.UIcontrollers;

import dai.hung.pompipiTaskView.state.AuthState;
import dai.hung.pompipiTaskView.widgets.Tile;
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

public class ListUIController {
    public Label emailText;


    public VBox projectList;

    @FXML
    public void initialize(){
        emailText.setText(AuthState.getEmail());
        try {
            FXMLLoader load = new FXMLLoader(getClass().getResource("/fxml/widgets/project-tile.fxml"));
            load.setController(new Tile("a","b","c"));
            projectList.getChildren().add(load.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
