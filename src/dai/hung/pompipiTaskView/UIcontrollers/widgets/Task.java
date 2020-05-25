package dai.hung.pompipiTaskView.UIcontrollers.widgets;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class Task {
    public enum TaskType{
        planned,
        done,
        progress
    }
    private final TaskType type;
    private final String name;
    private final TaskAction taskAction;
    public Label nameLabel;

    public Task(TaskType type, String name, TaskAction taskAction) {
        this.type = type;
        this.name = name;
        this.taskAction = taskAction;
    }
    @FXML
    private void initialize(){
        this.nameLabel.setText(name);
    }

    public void deleteTask(ActionEvent actionEvent) {
        taskAction.onDelete();
    }

}
