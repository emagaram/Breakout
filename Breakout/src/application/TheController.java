package application;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Duration;

public class TheController extends Application{
	BoardModel bm;
	TheView view;
	Timeline timeline;
	Stage window;
	HighscoreManager hm;
	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		hm = new HighscoreManager("Highscore.txt");
		JavaCollisionDetector jcd = new JavaCollisionDetector();
		bm = new BoardModel(500,500,30,0,0,80,15,6,jcd);
		Runnable onPaddleLeft = new MovePaddleLeft();
        Runnable onPaddleRight = new MovePaddleRight();
        view = new TheView(onPaddleLeft,onPaddleRight, bm.getWidth(), bm.getHeight());
		view.start(primaryStage);
		for(application.Rectangle b: bm.getBricks()){
			view.drawRectangle(b);
		}

		window.setOnCloseRequest(e -> {
			Runtime.getRuntime().exit(0);
		});

		timeline = new Timeline(new KeyFrame(
		        Duration.millis(4),
		        ae -> updateScreen()));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();

		}
	class MovePaddleLeft implements Runnable{
		@Override
		public void run() {
			bm.movePaddleLeft();
			//updateScreen();updateScreen();updateScreen();updateScreen();

		}
	}

	class MovePaddleRight implements Runnable{
		@Override
		public void run() {
			bm.movePaddleRight();
			//updateScreen();updateScreen();updateScreen();updateScreen();
		}
	}
		public void updateScreen() {
			view.drawRectangle(bm.getBat());
			view.drawRectangle(bm.getBall());
			for(application.Rectangle b: bm.getBricks()){
				view.drawRectangle(b);
			}
			bm.updateAll();
			view.score.setText(""+bm.getGameScore());
		}

	public static void main(String[] args){
		launch(args);
	}

}
