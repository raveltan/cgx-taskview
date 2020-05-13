package dai.hung.pompipiTaskView;

import dai.hung.pompipiTaskView.models.auth.TokenWriter;
import dai.hung.pompipiTaskView.state.AuthState;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        boolean isLogedin;
        try{
            isLogedin = TokenWriter.readStateFromFile();
        }catch (Exception ex){
            isLogedin=false;
        }
        Parent root = FXMLLoader.load(getClass().getResource(
                isLogedin ? "/fxml/screens/list.fxml" : "/fxml/screens/login.fxml"
        ));
        primaryStage.setTitle("Pompipi TaskView");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
