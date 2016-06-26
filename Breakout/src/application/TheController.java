package application;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.stage.Stage;

public class TheController extends Application{
	BoardModel bm;
	TheView view;
	ScheduledThreadPoolExecutor executor;
	@Override
	public void start(Stage primaryStage) throws Exception {
		bm = new BoardModel(500,500,20,2);
		executor = new ScheduledThreadPoolExecutor(5);
		Runnable onPaddleLeft = new MovePaddleLeft();
        Runnable onPaddleRight = new MovePaddleRight();
        view = new TheView(onPaddleLeft,onPaddleRight, bm.getWidth(), bm.getHeight());
		view.start(primaryStage);
		executor.scheduleAtFixedRate(new Update(), 20L, 20, TimeUnit.MILLISECONDS);
		primaryStage.setOnCloseRequest(e -> {
			Runtime.getRuntime().exit(0);
		});
	}
	class MovePaddleLeft implements Runnable{
		@Override
		public void run() {
			bm.movePaddleLeft();
			view.drawRectangle(bm.getBat());
			bm.update();
			view.drawRectangle(bm.getBall());
		}
	}

	class MovePaddleRight implements Runnable{
		@Override
		public void run() {
			bm.movePaddleRight();
			for(application.Rectangle b: bm.getBricks()){
				view.drawRectangle(b);
			}
			view.drawRectangle(bm.getBat());
			bm.update();
			view.drawRectangle(bm.getBall());
		}
	}
	class Update implements Runnable{

		@Override
		public void run() {
			view.drawRectangle(bm.getBall());
			bm.update();
		}

	}

	public static void main(String[] args){
		launch(args);
	}

}
