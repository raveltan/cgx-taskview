package dai.hung.pompipiTaskView.UIcontrollers.kanban;

import com.jfoenix.controls.JFXSpinner;
import dai.hung.pompipiTaskView.UIcontrollers.widgets.Task;
import dai.hung.pompipiTaskView.UIcontrollers.widgets.TaskAction;
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
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <h1>KanBanController</h1>
 * A FXML Controller class which is responsible for each task
 */
public class KanBanController {
    @FXML
    private JFXSpinner loadingIndicator;
    @FXML
    private VBox container;
    @FXML
    private VBox plannedBox;
    @FXML
    private VBox progressBox;
    @FXML
    private VBox doneBox;
    @FXML
    private Label projectNameLabel;

    private final String projectName;
    private final String projectUUID;
    private ArrayList<String> planned = new ArrayList<>();
    private ArrayList<String> progress = new ArrayList<>();
    private ArrayList<String> done = new ArrayList<>();

    /**
     * Initialize the controller with name and UUID
     * @param projectName project name
     * @param projectUUID project UUID
     */
    public KanBanController(String projectName, String projectUUID) {
        this.projectName = projectName;
        this.projectUUID = projectUUID;
    }


    /**
     * Set whether the scene is enable.
     * @param enabled form enabled
     */
    private void setEnabled(boolean enabled) {
        container.setDisable(!enabled);
        container.setOpacity(enabled ? 1 : .5);
    }

    /**
     * Initialize the scene with data from the rest API and set the name label
     */
    @FXML
    private void initialize() {
        this.projectNameLabel.setText(projectName);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        setEnabled(false);
        executor.execute(new RestRequest(
                AuthState.getLocalId() + "/" + this.projectName,
                RestRequest.HttpVerb.GET,
                (res, err) -> {
                    Platform.runLater(() -> {
                        setEnabled(true);
                        showError(err);
                    });
                    if (res != null) {
                        res.forEach((key, value) -> {
                            if (key.equals("done") && value != null) {
                                ((ArrayList) value).forEach(e -> {
                                    if (e != null) {
                                        if (e != "") {
                                            done.add(e.toString());
                                        }
                                    }
                                });
                            } else if (key.equals("progress") && value != null) {
                                ((ArrayList) value).forEach(e -> {
                                    if (e != null) {
                                        if (e != "") {
                                            progress.add(e.toString());
                                        }
                                    }
                                });
                            } else if (key.equals("planned") && value != null) {
                                ((ArrayList) value).forEach(e -> {
                                    if (e != null) {
                                        if (e != "") {
                                            planned.add(e.toString());
                                        }
                                    }
                                });
                            }
                        });
                        Platform.runLater(() -> {
                            done.forEach((data) -> {
                                if (data != null) {
                                    if (data != "") {
                                        addTaskTile(Task.TaskType.done, data.toString());
                                    }

                                }

                            });
                            progress.forEach((data) -> {
                                if (data != null) {
                                    if (data != "") {
                                        addTaskTile(Task.TaskType.progress, data.toString());
                                    }
                                }

                            });
                            planned.forEach((data) -> {
                                if (data != null) {
                                    if (data != "") {
                                        addTaskTile(Task.TaskType.planned, data.toString());
                                    }
                                }

                            });

                        });
                    }
                }
        ));
    }

    /**
     * Show error if there is any
     * @param error error string (may be null)
     */
    private void showError(String error) {
        if (error != null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("There is problem connecting to server, try again later");
            alert.setContentText(error);
            alert.show();
            alert.setOnCloseRequest((e) -> {
                Platform.exit();
                System.exit(1);
            });
        }
    }

    /**
     * Go back to the projects scene.
     * @param actionEvent event data from event source(ignored)
     */
    @FXML
    private void goBack(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            Stage stage = (Stage) ((Node) projectNameLabel).getScene().getWindow();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("/fxml/screens/view-project.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setMaximized(false);
            stage.setTitle("CGX TaskView");
            stage.setScene(new Scene(root, 800, 600));
            stage.setMinWidth(800);
            stage.setWidth(800);
        });
    }

    /**
     * Add a new planned data
     * @param actionEvent event data from event source(ignored)
     */
    @FXML
    private void addPlan(ActionEvent actionEvent) {
        addTask("planned");
    }

    /**
     * Add a new progressed data
     * @param actionEvent event data from event source(ignored)
     */
    @FXML
    private void addProgress(ActionEvent actionEvent) {
        addTask("progress");
    }

    /**
     * Add a new done data
     * @param actionEvent event data from event source(ignored)
     */
    @FXML
    private void addDone(ActionEvent actionEvent) {
        addTask("done");
    }

    /**
     * Add a new task with spesific name.
     * @param name name of the task
     */
    private void addTask(String name) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Task");
        dialog.setHeaderText(null);
        dialog.setContentText("Input Task Name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent((task) -> {
            changeTask(name, task);
        });
    }

    /**
     * Reach to the REST API to overwrite task
     * @param name type of the event
     * @param task name of the event
     */
    private void changeTask(String name, String task) {
        setEnabled(false);
        ExecutorService service = Executors.newFixedThreadPool(2);
        Map<String, String> data = new HashMap<>();
        switch (name) {
            case "planned":
                if (task != null) planned.add(task);
                for (int i = 0; i < planned.size(); i++) {
                    data.put(Integer.toString(i), planned.get(i));
                }

                break;
            case "progress":
                if (task != null) progress.add(task);
                for (int i = 0; i < progress.size(); i++) {
                    data.put(Integer.toString(i), progress.get(i));
                }
                break;
            case "done":
                if (task != null) done.add(task);
                for (int i = 0; i < done.size(); i++) {
                    data.put(Integer.toString(i), done.get(i));
                }
                break;
        }
        service.execute(new RestRequest(
                AuthState.getLocalId() + "/" + projectName + "/" + name,
                data,
                RestRequest.HttpVerb.PUT,
                (res, err) -> {
                    Platform.runLater(() -> {
                        showError(err);
                        if (err != null && task != null) {
                            switch (name) {
                                case "planned":
                                    planned.remove(planned.size() - 1);
                                    break;
                                case "progress":
                                    progress.remove(progress.size() - 1);
                                    break;
                                case "done":
                                    done.remove(done.size() - 1);
                                    break;
                            }
                        }
                        if (res != null && task != null) {

                            switch (name) {
                                case "planned":

                                    addTaskTile(Task.TaskType.planned, task);
                                    break;
                                case "done":

                                    addTaskTile(Task.TaskType.done, task);
                                    break;
                                case "progress":

                                    addTaskTile(Task.TaskType.progress, task);
                                    break;
                            }

                        }
                        setEnabled(true);
                    });
                }
        ));
    }

    /**
     * Delete the project(Task)
     * make a http request to delete the project collection
     * then make another request to delete project data
     * @param actionEvent event data from event source(ignored)
     */
    @FXML
    private void deleteProject(ActionEvent actionEvent) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        setEnabled(false);
        executorService.execute(new RestRequest(
                AuthState.getLocalId() + "/" + projectName,
                RestRequest.HttpVerb.DELETE,
                (res, err) -> {
                    if (err == null) {
                        ExecutorService service = Executors.newFixedThreadPool(2);
                        service.execute(new RestRequest(
                                AuthState.getLocalId() + "/projects/" + projectUUID,
                                RestRequest.HttpVerb.DELETE,
                                (res2, err2) -> {
                                    Platform.runLater(() -> {
                                        setEnabled(true);
                                        if (err2 != null) {
                                            showError("Unable to delete project!");
                                        } else {
                                            goBack(null);
                                        }
                                    });
                                }
                        ));
                    } else {
                        Platform.runLater(() -> {
                            showError("Unable to delete! Check your connection");
                        });

                    }
                }
        ));
    }

    /**
     * Fetch new data of the project(task) from the server
     * @param actionEvent
     */
    @FXML
    private void refreshProject(ActionEvent actionEvent) {
        planned.clear();
        progress.clear();
        done.clear();
        if (plannedBox.getChildren().size() > 1) {
            plannedBox.getChildren().remove(1, plannedBox.getChildren().size());
        }
        if (doneBox.getChildren().size() > 1) {
            doneBox.getChildren().remove(1, doneBox.getChildren().size());
        }
        if (progressBox.getChildren().size() > 1) {
            progressBox.getChildren().remove(1, progressBox.getChildren().size());
        }
        initialize();
    }

    /**
     * Add task to the scene
     * @param type type of the task(planned,progress,done)
     * @param name name of the task
     */
    private void addTaskTile(Task.TaskType type, String name) {
        Platform.runLater(() -> {
            try {
                FXMLLoader load = new FXMLLoader(getClass().getResource("/fxml/widgets/task-tile.fxml"));
                load.setController(new Task(type, name, new TaskAction() {
                    /**
                     * Set the ondelete of the task
                     * remove the task from scene if http requst is success
                     */
                    @Override
                    public void onDelete() {
                        setEnabled(false);
                        String taskType;
                        int taskIndex;
                        if (type == Task.TaskType.done) {
                            taskIndex = done.indexOf(name);
                            taskType = "done";
                        } else if (type == Task.TaskType.planned) {
                            taskIndex = planned.indexOf(name);
                            taskType = "planned";
                        } else {
                            taskIndex = progress.indexOf(name);
                            taskType = "progress";
                        }

                        Platform.runLater(() -> {
                            if (type == Task.TaskType.done) {
                                done.remove(name);
                                doneBox.getChildren().remove(taskIndex +1);
                            } else if (type == Task.TaskType.planned) {
                                planned.remove(name);
                                plannedBox.getChildren().remove(taskIndex+1);
                            } else {
                                progress.remove(name);
                                progressBox.getChildren().remove(taskIndex +1);
                            }
                            changeTask(taskType,null);

                        });

                    }
                }));
                if (type == Task.TaskType.done) {
                    doneBox.getChildren().add(load.load());
                } else if (type == Task.TaskType.planned) {
                    plannedBox.getChildren().add(load.load());
                } else {
                    progressBox.getChildren().add(load.load());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
