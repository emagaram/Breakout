package application;

import javafx.application.Application;
import javafx.stage.Stage;

public class GameRunner extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception {
		new TheController(500,700).start(primaryStage);
	}
	public static void main(String[] args){
		launch(args);
	}
}
