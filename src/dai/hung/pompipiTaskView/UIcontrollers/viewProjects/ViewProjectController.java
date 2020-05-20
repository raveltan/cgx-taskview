package dai.hung.pompipiTaskView.UIcontrollers.viewProjects;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import dai.hung.pompipiTaskView.UIcontrollers.kanban.KanBanController;
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
import sun.misc.UUDecoder;

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
    private void initialize() {
        emailText.setText(AuthState.getEmail());
        errorText.setVisible(false);
        updateData();
    }

    private void clearProjectTile() {
        if (projectList.getChildren().size() > 1) {
            projectList.getChildren().remove(1, projectList.getChildren().size());
        }
    }

    private void updateData() {
        setEnabled(false);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        System.out.println("update data");
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
                                            if (error == null) {
                                                System.out.println("update done");
                                                clearProjectTile();
                                                if (projects != null) {
                                                    projects.forEach((a, b) -> {

                                                        addProjectTile(b.toString(),a.toString());
                                                    });
                                                }
                                                setEnabled(true);
                                            } else {
                                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                                alert.setTitle("Error");
                                                alert.setContentText("There is problem connecting to server, try again later");
                                                alert.show();
                                                alert.setOnCloseRequest((e) -> {
                                                    Platform.exit();
                                                    System.exit(1);
                                                });
                                            }
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
        if(projects!=null){
            if(projects.size() > 5){
                errorText.setText("Free user may only have 5 max projects");
                errorText.setVisible(true);
                return;
            }
        }
        if (errorText.isVisible()) return;
        if (projectTextField.getText().length() < 3) {
            errorText.setText("Project name must be at least 3 letters!");
            errorText.setVisible(true);
            return;
        }
        Map<String, String> data = new HashMap<>();
        String randomUUID = UUID.randomUUID().toString();
        data.put(randomUUID, projectTextField.getText());
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
                            alert.setTitle("Connection Problem");
                            alert.setContentText("Unable to add project");
                            alert.show();
                        } else {
                            addProjectTile(projectTextField.getText(),randomUUID);
                            projectTextField.clear();
                            setEnabled(true);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setContentText("Project successfully created");
                            alert.show();

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
        });
    }

    private void goToPage(String name,String UUID) {
        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) ((Node) emailText).getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/screens/kanban.fxml"));
                loader.setController(new KanBanController(name, UUID));
                stage.setScene(new Scene(loader.load(), 800, 600));
                stage.setMaximized(true);
                stage.setResizable(true);
                stage.setMinWidth(800);
                stage.setMinHeight(600);
                stage.setTitle("CGX TaskView - " + name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void addProjectTile(String title,String UUID) {
        try {
            FXMLLoader load = new FXMLLoader(getClass().getResource("/fxml/widgets/project-tile.fxml"));
            load.setController(new Tile(title,UUID, (name) -> {
                goToPage(name,UUID);
            }));
            projectList.getChildren().add(load.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void projectNameTextChanged(KeyEvent actionEvent) {
        if (errorText.isVisible() && projectTextField.getText().length() > 3) {
            errorText.setVisible(false);
        }
    }
}
