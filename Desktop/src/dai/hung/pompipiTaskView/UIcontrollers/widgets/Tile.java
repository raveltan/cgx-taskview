package dai.hung.pompipiTaskView.UIcontrollers.widgets;

import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class Tile {
    @FXML
    private Label titleText;
    private final String UUID;
    private final String title;
    private final TileAction tileAction;

    public Tile(String title, String UUID,TileAction tileAction) {
        this.title = title;
        this.tileAction = tileAction;
        this.UUID = UUID;
    }

    @FXML
    private void initialize(){
        titleText.setText(this.title);
    }
    @FXML
    private void edit(){
        tileAction.onClicked(this.title);
    }

}
