package application;

import java.io.IOException;

import audio.AudioPlayer;
import gameComponents.GameBoardView;
import gameComponents.StartScreenView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TheController extends Application {
	private Stage window;
	private Scene scene;
	private static int BOARD_WIDTH;
	private static int BOARD_HEIGHT;

	TheController(int width, int height) {
		BOARD_WIDTH = width;
		BOARD_HEIGHT = height;
	}
	public void hitPlay(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		window=stage;
		System.out.println("HI");
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		window.setResizable(false);
		window.setTitle("Breakout");
		window.setWidth(BOARD_WIDTH);
		window.setHeight(BOARD_HEIGHT);
		goToStartScreen();
	}

	public void goToLevelScreen() {
		LevelsScreenModel theModel = new LevelsScreenModel(4, 4, 20);
		LevelsScreenView theView = new LevelsScreenView(theModel);
		theView.drawArrayOfButtons();
		try {
			theView.start(window);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void goToPlayScreen() {
		Levels theModel = new Levels();
		Runnable onPaddleLeft = new MovePaddleLeft(theModel);
		Runnable onPaddleRight = new MovePaddleRight(theModel);
		GameBoardView theView = new GameBoardView(onPaddleLeft, onPaddleRight);
		try {
			theView.start(window);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), ae -> updateScreen(theView, theModel)));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
		window.setOnCloseRequest(e -> {
			Runtime.getRuntime().exit(0);
		});
		window.show();
	}

	public void goToStartScreen() throws Exception {
		StartScreenView theView = new StartScreenView();
		theView.getPlay().setOnAction(e->{goToPlayScreen();});
		theView.start(window);
		window.show();

		// Play Background Music
		AudioPlayer.playSoundEffectIndefinitely(AudioPlayer.gameMusicFile);
		// Play Background Music end
	}

	// Method for redrawing bat and balls
	public void updateScreen(GameBoardView theView, Levels theModel) {
		// Temporary solution
		if (window.getScene() == theView.getScene()) {
			theView.drawRectangle(theModel.level3.getBat());
			theView.drawRectangle(theModel.level3.getBall());
			for (gameComponents.Brick brick : theModel.level3.getBricks()) {
				theView.drawRectangle(brick);
			}
			theModel.level3.updateAll();
		} else {
			try {
				theView.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
			theModel = new Levels();
		}

	}
	// Method for redrawing bat and balls end

	// Connects Data Paddle Movement with view paddle movement
	class MovePaddleLeft implements Runnable {
		Levels theModel;

		MovePaddleLeft(Levels theModel) {
			this.theModel = theModel;
		}

		@Override
		public void run() {
			theModel.level3.movePaddleLeft();
		}
	}

	class MovePaddleRight implements Runnable {

		Levels theModel;

		MovePaddleRight(Levels theModel) {
			this.theModel = theModel;
		}

		@Override
		public void run() {
			theModel.level3.movePaddleRight();
		}
	}
	// Paddle movement end

	public static int getBoardWidth() {
		return BOARD_WIDTH;
	}

	public static int getBoardHeight() {
		return BOARD_HEIGHT;
	}
}
