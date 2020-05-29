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

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        boolean isLogin;
        try{
            isLogin = TokenWriter.readStateFromFile();
        }catch (Exception ex){
            isLogin=false;
        }
        Parent root = FXMLLoader.load(getClass().getResource(
                isLogin ? "/fxml/screens/view-project.fxml" : "/fxml/screens/login.fxml"
        ));
        primaryStage.setTitle("CGX TaskView");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
