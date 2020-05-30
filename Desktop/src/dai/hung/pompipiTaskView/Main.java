package dai.hung.pompipiTaskView;
import dai.hung.pompipiTaskView.models.auth.TokenWriter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * <h1>Main</h1>
 * The main class is the entry point to the whole program.
 * This class loads the first scene, which is determined by the availability of token file.
 * If token file is not available login scene will be loaded,
 * if available view project will be loaded.
 *
 * @author Ravel Tanjaya
 * @version 1.1.0
 */
public class Main extends Application {
    /**
     * Create the main windows of the JavaFX application.
     * @param primaryStage the window for the scene to be displayed.
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        boolean isLogin;
        try{
            isLogin = TokenWriter.readStateFromFile(); //ReadToken from file
        }catch (Exception ex){
            isLogin=false;
        }
        Parent root = FXMLLoader.load(getClass().getResource(
                isLogin ? "/fxml/screens/view-project.fxml" : "/fxml/screens/login.fxml"
        )); //Assign the root widget according to the result of isLogin.

        // Stage initialization.
        primaryStage.setTitle("CGX TaskView");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 800, 600));

        // Set a callback to terminate the application if the stage is closed.
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });

        //Show the app
        primaryStage.show();
    }

    /**
     * The main method of the application, application entry point
     * @param args program argument to be run
     */
    public static void main(String[] args) {
        launch(args);
    }
}
