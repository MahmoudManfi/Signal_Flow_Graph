import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("GUI.fxml"));
        VGraph vGraph = VGraph.getInstance();
        vGraph.draw(root);
        Scene scene = new Scene(root, 1000, 720);
        scene.getStylesheets().add("style.css");
        primaryStage.setTitle("Signal_Flow_Graph");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
        vGraph.init();
    }

    public static void main(String[] args) {  launch(args);}
}