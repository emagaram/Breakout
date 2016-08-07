package application;
import gameComponenets.HighscoreManager;
import gameComponenets.JavaCollisionDetector;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameController extends Application{
	GameBoardModel gbm;
	GameView view;
	Timeline timeline;
	Stage window;
	HighscoreManager hm;
	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		hm = new HighscoreManager("Highscore.txt");
		JavaCollisionDetector jcd = new JavaCollisionDetector();
		gbm = new GameBoardModel(
				500,// Screen Width
				500,// Screen Height
				50,// Brick Height
				30,// Horizontal Brick Gap
				30,// Vertical Brick Gap
				100,// Gap above bricks
				10,// # of Columns
				3,// # of Rows
				100,// Bat Width
				20,// Bat Height
				8,// Bat Speed
				1, //Ball Speed
				jcd);
		Runnable onPaddleLeft = new MovePaddleLeft();
        Runnable onPaddleRight = new MovePaddleRight();
        view = new GameView(onPaddleLeft,onPaddleRight, gbm.getWidth(), gbm.getHeight());
		view.start(primaryStage);
		//Draws Bricks
		for(gameComponenets.Rectangle b: gbm.getBricks()){
			view.drawRectangle(b);
		}

		//Draws Bricks end

		//Terminates the entire program on close
		window.setOnCloseRequest(e -> {
			Runtime.getRuntime().exit(0);
		});
		//Terminates the entire program on close end


		//Animation Timer, it moves the ball every 4ms
		timeline = new Timeline(new KeyFrame(
		        Duration.millis(4),
		        ae -> updateScreen()));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
		//Animation Timer End

		}

	//Connects Data Paddle Movement with view paddle movement
	class MovePaddleLeft implements Runnable{
		@Override
		public void run() {
			gbm.movePaddleLeft();
		}
	}

	class MovePaddleRight implements Runnable{
		@Override
		public void run() {
			gbm.movePaddleRight();
		}
	}
	//Paddle movement end

	//Method for redrawing bat and balls
		public void updateScreen() {
			view.drawRectangle(gbm.getBat());
			view.drawRectangle(gbm.getBall());
			for(gameComponenets.Brick b: gbm.getBricks()){
				view.drawRectangle(b);
			}
			gbm.updateAll();
		}
	//Method for redrawing bat and balls end

	//Method for returning the scene so that the StartScreenController can change scenes to this one
		public Scene getScene() {
			return window.getScene();
		}
	//getScene() end
}
