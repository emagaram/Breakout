package application;

import javafx.stage.Stage;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.util.Optional;

import gameComponenets.Brick;
import gameComponenets.RectangleType;
import javafx.application.Application;
import javafx.event.EventHandler;

public class GameView extends Application {
	private Stage window;
	private Scene scene;
	private Runnable onTypePaddleMoveLeft;
	private Runnable onTypePaddleMoveRight;
	private ImagePattern brickPattern;
	private ImagePattern batPattern;
	private ImagePattern ballPattern;
	private ImagePattern chiseledBrick1Pattern;

	private Image brickImage;
	private Image chiseledBrick1Image;
	private Image batImage;
	private Image ballImage;
	private Image bgImage;
	private int width;
	private int height;
	private Pane layout;
	int totalRectangles = 0;

	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		window.setResizable(false);
		brickImage = new Image("images/Square.png");
		batImage = new Image("images/Paddle.png");
		ballImage = new Image("images/Ball.png");
		bgImage = new Image("images/Background.png");
		chiseledBrick1Image = new Image("images/ChiseledSquare.png");

		brickPattern = new ImagePattern(brickImage);
		chiseledBrick1Pattern = new ImagePattern(chiseledBrick1Image);
		batPattern = new ImagePattern(batImage);
		ballPattern = new ImagePattern(ballImage);

		layout = new Pane();

		scene = new Scene(layout, width, height);

		// One way to add bg
		// scene.getStylesheets().addAll(this.getClass().getResource("cssfilename.css").toExternalForm());
		// One way end

		// Another way to add bg
		BackgroundImage myBI = new BackgroundImage(bgImage, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
		// then you set to your layout
		layout.setBackground(new Background(myBI));
		// Another way end

		// Detecting Keymovement
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
		// Detecting Keymovement end
		window.setScene(scene);
		window.show();
	}

	public void drawRectangle(gameComponenets.Rectangle r) {
		// find a graphical rectangle in children where r.Id = gr.Id
		// if it isn't there, create one and add it to children
		// then...
		//
		// gr.setwidth, etc. according to r
		Optional<Node> graphicalRectNode = layout.getChildren().stream().filter(n -> n instanceof Rectangle)
				.filter(n -> n.getId().equals(Integer.toString(r.getId()))).findFirst();
		Rectangle graphicalRect = null;
		if (graphicalRectNode.isPresent()) {
			graphicalRect = (Rectangle) (graphicalRectNode.get());
		} else {
			totalRectangles++;
			graphicalRect = new Rectangle();
			layout.getChildren().add(graphicalRect);
		}

		if (r.getType() == RectangleType.Brick) {
			graphicalRect.setFill(brickPattern);
		} else if (r.getType() == RectangleType.ChiseledBrick1) {
			graphicalRect.setFill(chiseledBrick1Pattern);
		} else if (r.getType() == RectangleType.Bat) {
			graphicalRect.setFill(batPattern);
		} else if (r.getType() == RectangleType.Ball) {
			graphicalRect.setFill(ballPattern);
		}
		graphicalRectNode = null;

		if (r instanceof Brick && ((Brick) r).isAlive() == false) {
			graphicalRect.setOpacity(0);
		}
		graphicalRect.setId(Integer.toString(r.getId()));
		graphicalRect.setWidth(r.getWidth());
		graphicalRect.setHeight(r.getHeight());
		graphicalRect.setTranslateX(r.getTopLeftCoordinate().getX());
		graphicalRect.setTranslateY(r.getTopLeftCoordinate().getY());
	}

	public GameView(Runnable onTypePaddleMoveLeft, Runnable onTypePaddleMoveRight, int width, int height) {
		this.onTypePaddleMoveLeft = onTypePaddleMoveLeft;
		this.onTypePaddleMoveRight = onTypePaddleMoveRight;
		this.width = width;
		this.height = height;
	}
}