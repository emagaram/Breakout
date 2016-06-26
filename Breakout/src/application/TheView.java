package application;

import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.Optional;

import javafx.application.Application;
import javafx.event.EventHandler;

public class TheView extends Application {
	private Stage window;
	private Scene scene;
	private Runnable onTypePaddleMoveLeft;
	private Runnable onTypePaddleMoveRight;
	private int width;
	private int height;
	private Pane layout;

	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		window.setResizable(false);
		layout = new Pane();
		scene = new Scene(layout, width, height);
		scene.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent k) {
				if (k.getCode() == KeyCode.A) {
					onTypePaddleMoveLeft.run();
				}
				else if (k.getCode() == KeyCode.D) {
					onTypePaddleMoveRight.run();
				}
			}
		});
		window.setScene(scene);
		window.show();
	}

	public void drawRectangle(application.Rectangle r) {
		// find a graphical rectangle in children where r.Id = gr.Id
		// if it isn't there, create one and add it to children
		// then...
		//
		//    gr.setwidth, etc. according to r
		Optional<Node> graphicalRectNode = layout
			.getChildren()
			.stream()
			.filter(n->n instanceof Rectangle)
			.filter(n->n.getId().equals(Integer.toString(r.getId())))
			.findFirst();
		Rectangle graphicalRect = null;
		if (graphicalRectNode.isPresent()) {
			graphicalRect = (Rectangle)(graphicalRectNode.get());
		}
		else {
			graphicalRect=new Rectangle();
			graphicalRect.setId(Integer.toString(r.getId()));
			layout.getChildren().add(graphicalRect);
		}
		graphicalRect.setWidth(r.getWidth());
		graphicalRect.setHeight(r.getHeight());
		graphicalRect.setTranslateX(r.getTopLeft().getX());
		graphicalRect.setTranslateY(r.getTopLeft().getY());
	}

	public TheView(Runnable onTypePaddleMoveLeft, Runnable onTypePaddleMoveRight,int width,int height) {
		this.onTypePaddleMoveLeft = onTypePaddleMoveLeft;
		this.onTypePaddleMoveRight = onTypePaddleMoveRight;
		this.width = width;
		this.height = height;
	}
}
