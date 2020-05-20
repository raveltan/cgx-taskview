package dai.hung.pompipiTaskView.UIcontrollers.kanban;

import com.jfoenix.controls.JFXSpinner;
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

    public KanBanController(String projectName,String projectUUID) {
        this.projectName = projectName;
        this.projectUUID = projectUUID;
    }
    private void setEnabled(boolean enabled){
        container.setDisable(!enabled);
        container.setOpacity(enabled ? 1 :.5);
    }

    @FXML
    private void initialize(){
        this.projectNameLabel.setText(projectName);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        setEnabled(false);
        executor.execute(new RestRequest(
                AuthState.getLocalId()+"/"+this.projectName,
                RestRequest.HttpVerb.GET,
                (res,err)->{
                    showError(err);
                    if(res!=null) {
                        res.forEach((key, value) -> {
                            if (key.equals("done")) {
                                done = (ArrayList) value;
                            } else if (key.equals("progress")) {
                                progress = (ArrayList) value;
                            } else {
                                planned = (ArrayList) value;
                            }
                        });
                        setEnabled(true);
                        System.out.println(res);
                    }else{
                        ExecutorService executor2 = Executors.newFixedThreadPool(1);
                        Map<String,Map<String,String>> data = new HashMap<>();
                        data.put("done",new HashMap<String,String>(){
                            {put("0","");}
                        });
                        data.put("progress",new HashMap<String,String>(){{
                            put("0","");}
                        });
                        data.put("planned",new HashMap<String,String>(){{
                            put("0","");}
                        });
                        executor2.execute(new RestRequest(
                                AuthState.getLocalId() + "/" + this.projectName,
                                data,
                                RestRequest.HttpVerb.PATCH,
                                new ResultInterface() {
                                    @Override
                                    public void onFinish(Map result, String error) {
                                        System.out.println("added");
                                        System.out.println(result);
                                        System.out.println(error);
                                        setEnabled(true);
                                        showError(error);
                                    }
                                }
                        ));
                    }
                }
        ));
    }

    private void showError(String error) {
        if(error!=null){
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

    private void addTask(String name){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Task");
        dialog.setHeaderText(null);
        dialog.setContentText("Input Task Name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent((task)->{
            setEnabled(false);
            ExecutorService service = Executors.newFixedThreadPool(2);
            service.execute(new RestRequest(
                    AuthState.getLocalId()+"/"+projectName+"/"+name,
                    new HashMap(){{put(Integer.toString(planned.size()),task);}},
                    RestRequest.HttpVerb.PATCH,
                    (res,err)->{
                        showError(err);
                        if(res!= null){
                            switch (name){
                                case "planned":
                                    planned.add(task);
                                    break;
                                case "done":
                                    done.add(task);
                                    break;
                                case "progress":
                                    progress.add(task);
                                    break;
                            }
                            System.out.println(task);
                            setEnabled(true);
                        }
                    }
            ));
        });
    }

    @FXML
    private void deleteProject(ActionEvent actionEvent) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        setEnabled(false);
        executorService.execute(new RestRequest(
                AuthState.getLocalId()+"/"+projectName,
                RestRequest.HttpVerb.DELETE,
                (res,err)->{
                    if(err==null){
                        ExecutorService service = Executors.newFixedThreadPool(2);
                        service.execute(new RestRequest(
                                AuthState.getLocalId()+"/projects/"+projectUUID,
                                RestRequest.HttpVerb.DELETE,
                                (res2,err2)->{
                                    setEnabled(true);
                                    if(err2!=null){
                                        showError("Unable to delete project!");
                                    }else{
                                        goBack(null);
                                    }
                                }
                        ));
                    }else{
                        showError("Unable to delete! Check your connection");
                    }
                }
        ));
    }
}
