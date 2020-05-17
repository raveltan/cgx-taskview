package dai.hung.pompipiTaskView.UIcontrollers.widgets;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class Tile {
    public Label titleText;
    private final String title;

    public Tile(String title) {
        this.title = title;
    }

    @FXML
    public void initialize(){
        titleText.setText(this.title);
    }
}
