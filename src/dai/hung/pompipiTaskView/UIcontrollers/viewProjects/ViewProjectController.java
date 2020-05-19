package dai.hung.pompipiTaskView.UIcontrollers.viewProjects;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import dai.hung.pompipiTaskView.UIcontrollers.widgets.Tile;
import dai.hung.pompipiTaskView.models.ResultInterface;
import dai.hung.pompipiTaskView.models.data.RestRequest;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewProjectController {
    @FXML
    private Label emailText;
    @FXML
    private VBox projectList;
    @FXML
    private Label errorText;
    @FXML
    private JFXTextField projectTextField;
    @FXML
    private JFXButton logoutButton;
    @FXML
    private JFXButton createProjectButton;
    private Map projects;

    @FXML
    public void initialize() {
        emailText.setText(AuthState.getEmail());
        errorText.setVisible(false);
        updateData();
    }

    private void clearProjectTile(){
        if(projectList.getChildren().size() > 1){
            projectList.getChildren().remove(1,projectList.getChildren().size());
        }
    }

    private void updateData() {
        setEnabled(false);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(
                new RestRequest(
                        AuthState.getLocalId() + "/projects",
                        RestRequest.HttpVerb.GET,
                        new ResultInterface() {
                            @Override
                            public void onFinish(Map result, String error) {
                                projects = result;
                                Platform.runLater(
                                        () -> {
                                            clearProjectTile();
                                            if (projects != null) {
                                                projects.forEach((a, b) -> {

                                                    addProjectTile(b.toString());
                                                });
                                            }
                                            setEnabled(true);
                                        }
                                );
                            }
                        }
                )
        );
    }


    private void setEnabled(boolean enabled) {
        projectTextField.setEditable(enabled);
        createProjectButton.setDisable(!enabled);
        logoutButton.setDisable(!enabled);
        projectList.setDisable(!enabled);
    }

    public void logout(ActionEvent actionEvent) {
        AuthState.logout();
        goToLogin();
    }

    public void createProject(ActionEvent actionEvent) {
        projectNameTextChanged(null);
        if(errorText.isVisible()) return;
        if (projectTextField.getText().length() < 3) {
            errorText.setText("Project name must be at least 3 letters!");
            errorText.setVisible(true);
            return;
        }
        Map<String, String> data = new HashMap<>();
        data.put(UUID.randomUUID().toString(), projectTextField.getText());
        setEnabled(false);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(new RestRequest(
                AuthState.getLocalId() + "/projects/",
                data,
                RestRequest.HttpVerb.PATCH,
                (result, error) -> {
                    Platform.runLater(() -> {
                        if (error != null) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setContentText("Unable to create project");
                            alert.show();
                        } else {
                            projectTextField.clear();
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setContentText("Project successfully created");
                            alert.show();
                            updateData();
                        }
                    });
                }
        ));
    }


    private void goToLogin() {
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

    private void addProjectTile(String title) {
        try {
            FXMLLoader load = new FXMLLoader(getClass().getResource("/fxml/widgets/project-tile.fxml"));
            load.setController(new Tile(title));
            projectList.getChildren().add(load.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void projectNameTextChanged(KeyEvent actionEvent) {
        if (errorText.isVisible() && projectTextField.getText().length() > 3) {
            errorText.setVisible(false);
        }
        if(projects != null) {
            if (projects.containsValue(projectTextField.getText())) {
                errorText.setText("Project name must be unique");
                errorText.setVisible(true);
            }
        }
    }
}
