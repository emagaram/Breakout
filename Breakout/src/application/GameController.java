package application;

import gameComponenets.HighscoreManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameController extends Application {
	private Levels levels;
	private GameView view;
	private Timeline timeline;
	private Stage window;
	private HighscoreManager hm;

	@Override
	public void start(Stage primaryStage) throws Exception {
		levels = new Levels();
		window = primaryStage;
		// hm = new HighscoreManager("Highscore.txt");
		Runnable onPaddleLeft = new MovePaddleLeft();
		Runnable onPaddleRight = new MovePaddleRight();
		view = new GameView(onPaddleLeft, onPaddleRight, levels.level3.getWidth(), levels.level3.getHeight());
		view.start(primaryStage);
		// Draws Bricks
		for (gameComponenets.Rectangle b : levels.level3.getBricks()) {
			view.drawRectangle(b);
		}

		// Draws Bricks end

		// Terminates the entire program on close
		window.setOnCloseRequest(e -> {
			Runtime.getRuntime().exit(0);
		});
		// Terminates the entire program on close end

		// Animation Timer, it moves the ball every 4ms
		timeline = new Timeline(new KeyFrame(Duration.millis(0.5), ae -> updateScreen()));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
		// Animation Timer End

	}

	// Connects Data Paddle Movement with view paddle movement
	class MovePaddleLeft implements Runnable {
		@Override
		public void run() {
			levels.level3.movePaddleLeft();
		}
	}

	class MovePaddleRight implements Runnable {
		@Override
		public void run() {
			levels.level3.movePaddleRight();
		}
	}
	// Paddle movement end

	// Method for redrawing bat and balls
	public void updateScreen() {
		view.drawRectangle(levels.level3.getBat());
		view.drawRectangle(levels.level3.getBall());
		for (gameComponenets.Brick b : levels.level3.getBricks()) {
			view.drawRectangle(b);
		}
		levels.level3.updateAll();
	}
	// Method for redrawing bat and balls end

	// Method for returning the scene so that the StartScreenController can

	// change scenes to this one
	public Scene getScene() {
		return window.getScene();
	}

	public static void main(String[] args) {
		launch(args);
	}
	// getScene() end
}
