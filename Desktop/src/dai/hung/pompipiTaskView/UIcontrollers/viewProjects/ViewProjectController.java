package dai.hung.pompipiTaskView.UIcontrollers.viewProjects;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;
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

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <h1>ViewProjectController</h1>
 * A JavaFX Controller for view project
 * contains all projects of the user and allows creation of new project.
 */
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

    /**
     * Initialize lifecycle of Javafx
     * Set data of the email to current user email and update the projects.
     */
    @FXML
    private void initialize() {
        emailText.setText(AuthState.getEmail());
        errorText.setVisible(false);
        updateData();
    }

    /**
     * Clear all project tile from the scene.(except title)
     */
    private void clearProjectTile() {
        if (projectList.getChildren().size() > 1) {
            projectList.getChildren().remove(1, projectList.getChildren().size());
        }
    }

    /**
     * Update the data of the application from REST API
     */
    private void updateData() {
        setEnabled(false);
        //Create a new thread to run the async task
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(
                // call a get request to REST API and set the callback
                new RestRequest(
                        AuthState.getLocalId() + "/projects",
                        RestRequest.HttpVerb.GET,
                        new ResultInterface() {
                            @Override
                            public void onFinish(Map result, String error) {
                                projects = result;
                                // Run task on main thread
                                Platform.runLater(
                                        () -> {
                                            if (error == null) {
                                                // Add data to the view
                                                clearProjectTile();
                                                if (projects != null) {
                                                    projects.forEach((a, b) -> {

                                                        addProjectTile(b.toString(),a.toString());
                                                    });
                                                }
                                                setEnabled(true);
                                            } else {
                                                //Show error dialog
                                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                                alert.setTitle("Error");
                                                alert.setHeaderText("Unable to connect to server");
                                                alert.setContentText(error);
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

    /**
     * Enable or disable the whole scene.
     * @param enabled whether a the scene should or should not be enabled
     */
    private void setEnabled(boolean enabled) {
        projectTextField.setEditable(enabled);
        createProjectButton.setDisable(!enabled);
        logoutButton.setDisable(!enabled);
        projectList.setDisable(!enabled);
    }

    /**
     * Logout from current account
     * Delete the token file and set the authstate to black
     * @param actionEvent event data from event source(ignored)
     */
    public void logout(ActionEvent actionEvent) {
        AuthState.logout();
        goToLogin();
    }

    /**
     * Open the public link on a browser
     * @param actionEvent event data from event source(ignored)
     */
    public void openLink(ActionEvent actionEvent) {
        try {
            Desktop.getDesktop().browse(new URL("https://taskview-6358e.web.app/"+AuthState.getLocalId()).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a new project with data from the textfield.
     * @param actionEvent event data from event source(ignored)
     */
    public void createProject(ActionEvent actionEvent) {
        // Check if project is more than 5
        if(projects!=null){
            if(projects.size() > 5){
                errorText.setText("Free user may only have 5 max projects");
                errorText.setVisible(true);
                return;
            }
        }
        // Check if project name is more than 3 characters
        if (errorText.isVisible()) return;
        if (projectTextField.getText().length() < 3) {
            errorText.setText("Project name must be at least 3 letters!");
            errorText.setVisible(true);
            return;
        }
        //Create the REST request body with map, will be transformed to JSON
        Map<String, String> data = new HashMap<>();
        String randomUUID = UUID.randomUUID().toString();
        data.put(randomUUID, projectTextField.getText());
        setEnabled(false);
        //Create a new thread to run the HTTP Request
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(new RestRequest(
                AuthState.getLocalId() + "/projects/",
                data,
                RestRequest.HttpVerb.PATCH,
                (result, error) -> {
                    Platform.runLater(() -> {
                        // Add the data to the view if no error, else show error.
                        if (error != null) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText("Unable to add project");
                            alert.setTitle("Connection Problem");
                            alert.setContentText(error);
                            alert.show();
                        } else {
                            addProjectTile(projectTextField.getText(),randomUUID);
                            projectTextField.clear();
                        }
                        setEnabled(true);
                    });
                }
        ));
    }

    /**
     * GoToLogin
     * Change to login screen.
     */
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

    /**
     * Go the kanban page of each task
     * @param name name of the task
     * @param UUID UUID of the task
     */
    private void goToPage(String name,String UUID) {
        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) ((Node) emailText).getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/screens/kanban.fxml"));
                loader.setController(new KanBanController(name, UUID));
                stage.setScene(new Scene(loader.load(), 800, 600));
                stage.setMaximized(true);
                stage.setResizable(true);
                stage.setMinWidth(1080);
                stage.setMinHeight(600);
                stage.setTitle("CGX TaskView - " + name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Add a new tile for a project (task)
     * @param title title of the task
     * @param UUID uuid of the task
     */
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

    /**
     * Verify the correctness and validity of the new project(task) name
     * @param actionEvent
     */
    public void projectNameTextChanged(KeyEvent actionEvent) {
        if (errorText.isVisible() && projectTextField.getText().length() > 3) {
            errorText.setVisible(false);
        }
    }

    /**
     * Trigger create project
     * @param keyEvent event data from event source(ignored)
     */
    public void createProjectEnter(KeyEvent keyEvent) {
        createProject(null);
    }
}
