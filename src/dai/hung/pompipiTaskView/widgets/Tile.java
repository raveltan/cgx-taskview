package dai.hung.pompipiTaskView.widgets;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class Tile {
    public Text descText;
    public Text timeText;
    public Label titleText;
    private final String desc;
    private final String date;
    private final String title;

    public Tile(String desc, String date, String title) {
        this.desc = desc;
        this.date = date;
        this.title = title;
    }

    @FXML
    public void initialize(){
        descText.setText(this.desc);
        titleText.setText(this.title);
        timeText.setText(this.date);
    }
}
