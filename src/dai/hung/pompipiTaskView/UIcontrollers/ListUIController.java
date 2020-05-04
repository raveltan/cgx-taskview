package dai.hung.pompipiTaskView.UIcontrollers;

import dai.hung.pompipiTaskView.widgets.Tile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ListUIController {
    public VBox projectList;

    @FXML
    public void initialize(){
        try {
            FXMLLoader load = new FXMLLoader(getClass().getResource("/fxml/widgets/project-tile.fxml"));
            load.setController(new Tile("a","b","c"));
            projectList.getChildren().add(load.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logout(ActionEvent actionEvent) {

    }

    public void createProject(ActionEvent actionEvent) {

    }
}
