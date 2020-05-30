package dai.hung.pompipiTaskView.UIcontrollers.widgets;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * <h1>Tile</h1>
 * A FXML Controller for each project tile.
 * @author Ravel Tanjaya
 * @version 1.1.0
 */
public class Tile {
    @FXML
    private Label titleText;
    private final String UUID;
    private final String title;
    private final TileAction tileAction;

    /**
     * Create a new tile instance
     * @param title The title on the tile label.
     * @param UUID The tile's UUID, used for identifying the tile.
     * @param tileAction the callback for this tile.
     */
    public Tile(String title, String UUID,TileAction tileAction) {
        this.title = title;
        this.tileAction = tileAction;
        this.UUID = UUID;
    }

    /**
     * Initialize life cycle of Javafx.
     */
    @FXML
    private void initialize(){
        titleText.setText(this.title);
    }

    /**
     * Set a callback on press of the action
     */
    @FXML
    private void edit(){
        tileAction.onClicked(this.title);
    }
}
