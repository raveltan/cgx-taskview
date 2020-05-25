package dai.hung.pompipiTaskView.UIcontrollers.kanban;

import com.jfoenix.controls.JFXSpinner;
import dai.hung.pompipiTaskView.UIcontrollers.widgets.Task;
import dai.hung.pompipiTaskView.UIcontrollers.widgets.TaskAction;
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

    public KanBanController(String projectName, String projectUUID) {
        this.projectName = projectName;
        this.projectUUID = projectUUID;
    }

    private void setEnabled(boolean enabled) {
        container.setDisable(!enabled);
        container.setOpacity(enabled ? 1 : .5);
    }

    @FXML
    private void initialize() {
        this.projectNameLabel.setText(projectName);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        setEnabled(false);
        executor.execute(new RestRequest(
                AuthState.getLocalId() + "/" + this.projectName,
                RestRequest.HttpVerb.GET,
                (res, err) -> {
                    Platform.runLater(()->{
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
                        Platform.runLater(()->{
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

                        System.out.println(res);
                    }
                }
        ));
    }

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
        });
    }

    @FXML
    private void addPlan(ActionEvent actionEvent) {
        addTask("planned");
    }

    @FXML
    private void addProgress(ActionEvent actionEvent) {
        addTask("progress");
    }

    @FXML
    private void addDone(ActionEvent actionEvent) {
        addTask("done");
    }

    private void addTask(String name) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Task");
        dialog.setHeaderText(null);
        dialog.setContentText("Input Task Name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent((task) -> {
            setEnabled(false);
            ExecutorService service = Executors.newFixedThreadPool(2);
            service.execute(new RestRequest(
                    AuthState.getLocalId() + "/" + projectName + "/" + name,
                    new HashMap() {{
                        put(Integer.toString(name =="planned" ? planned.size() : name =="done" ? done.size() : progress.size()), task);
                    }},
                    RestRequest.HttpVerb.PATCH,
                    (res, err) -> {
                        Platform.runLater(()->{
                        showError(err);
                        if (res != null) {

                                switch (name) {
                                    case "planned":
                                        planned.add(task);
                                        addTaskTile(Task.TaskType.planned, task);
                                        break;
                                    case "done":
                                        done.add(task);
                                        addTaskTile(Task.TaskType.done, task);
                                        break;
                                    case "progress":
                                        progress.add(task);
                                        addTaskTile(Task.TaskType.progress, task);
                                        break;
                                }
                                System.out.println(task);
                                setEnabled(true);


                        }
                        });
                    }
            ));
        });
    }

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
                                    Platform.runLater(()->{
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
                        Platform.runLater(()->{
                            showError("Unable to delete! Check your connection");
                        });

                    }
                }
        ));
    }

    @FXML
    private void refreshProject(ActionEvent actionEvent){
        planned.clear();
        progress.clear();
        done.clear();
        if(plannedBox.getChildren().size()>1){
            plannedBox.getChildren().remove(1,plannedBox.getChildren().size());
        }
        if(doneBox.getChildren().size()>1){
            doneBox.getChildren().remove(1,doneBox.getChildren().size());
        }
        if(progressBox.getChildren().size()>1){
            progressBox.getChildren().remove(1,progressBox.getChildren().size());
        }
        initialize();

    }

    private void addTaskTile(Task.TaskType type, String name) {
        Platform.runLater(() -> {
            try {
                FXMLLoader load = new FXMLLoader(getClass().getResource("/fxml/widgets/task-tile.fxml"));
                load.setController(new Task(type, name, new TaskAction() {
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
                        ExecutorService executor = Executors.newFixedThreadPool(2);
                        executor.execute(new RestRequest(
                                AuthState.getLocalId() + "/" + projectName + "/" + taskType + "/" + taskIndex,
                                RestRequest.HttpVerb.DELETE,
                                (res, err) -> {
                                    setEnabled(true);
                                    showError(err);
                                        Platform.runLater(() -> {
                                            if (type == Task.TaskType.done) {
                                                done.remove(name);
                                                doneBox.getChildren().remove(taskIndex+1);
                                            } else if (type == Task.TaskType.planned) {
                                                planned.remove(name);
                                                plannedBox.getChildren().remove(taskIndex+1);
                                            } else {
                                                progress.remove(name);
                                                progressBox.getChildren().remove(taskIndex+1);
                                            }
                                        });

                                }
                        ));
                    }
                }));
                if (type == Task.TaskType.done) {
                    doneBox.getChildren().add(load.load());
                } else if (type == Task.TaskType.planned) {
                    plannedBox.getChildren().add(load.load());
                } else {
                    progressBox.getChildren().add(load.load());
                }
                System.out.println("added");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
