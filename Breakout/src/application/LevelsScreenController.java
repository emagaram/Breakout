package application;

import javafx.application.Application;
import javafx.stage.Stage;

public class LevelsScreenController extends Application {
	private Stage window;
	private LevelsScreenView theView;
	private LevelsScreenModel theModel;

	@Override
	public void start(Stage primaryStage) throws Exception {
		theModel = new LevelsScreenModel(3, 3, 50);
		theView = new LevelsScreenView(theModel);
		theView.drawArrayOfButtons();
		window = primaryStage;
		theView.start(window);
	}

	public static void main(String[] args) {
		launch(args);
	}
}