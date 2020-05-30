package dai.hung.pompipiTaskView.UIcontrollers.widgets;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * <h1>Task</h1>
 * A FXML controller class to manage each task widget.
 * @author Ravel Tanjaya
 * @version 1.1.0
 */

public class Task {

    /**
     * Enum to represent the task progress
     */
    public enum TaskType{
        planned,
        done,
        progress
    }

    private final TaskType type;
    private final String name;
    private final TaskAction taskAction;
    @FXML
    private Label nameLabel;

    /**
     * Constructor of Task class
     * @param type The task of the task
     * @param name The name of the task, will be put on the label
     * @param taskAction A callback to be run on delete
     */
    public Task(TaskType type, String name, TaskAction taskAction) {
        this.type = type;
        this.name = name;
        this.taskAction = taskAction;
    }

    /**
     * The initialize lifecycle of javafx application
     * automatically set the label nameLabel to the data gathered from constructor
     */
    @FXML
    private void initialize(){
        this.nameLabel.setText(name);
    }

    /**
     * Call the callback of onDelete from taskAction interface when delete is pressed
     * @param actionEvent The event data passed when the button is pressed (unused)
     */
    @FXML
    private void deleteTask(ActionEvent actionEvent) {
        taskAction.onDelete();
    }

}
