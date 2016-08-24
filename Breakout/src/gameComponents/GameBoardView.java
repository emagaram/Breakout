package gameComponents;

import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.util.LinkedList;
import java.util.Optional;

import application.TheController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;

public class GameBoardView extends Application {
    double orgSceneX, orgSceneY;
    double orgTranslateX;
    double newTranslateX;

	private Stage window;
	private Scene scene;

	private Runnable onTypePaddleMoveLeft;
	private Runnable onTypePaddleMoveRight;

	private ImagePattern threeHit3Pattern;
	private ImagePattern threeHit2Pattern;
	private ImagePattern threeHit1Pattern;

	private ImagePattern twohit2Pattern;
	private ImagePattern twoHit1Pattern;

	private ImagePattern explosivePattern;

	private ImagePattern batPattern;

	private ImagePattern ballPattern;

	private Image threeHit3Image;
	private Image threeHit2Image;
	private Image threeHit1Image;

	private Image twoHit2Image;
	private Image twoHit1Image;

	private Image explosiveImage;

	private Image batImage;
	private Image ballImage;
	private Image bgImage;
	private Pane layout;

	private LinkedList<ImageView> imageViews = new LinkedList<ImageView>();
	//private LinkedList<SpriteAnimation> animations = new LinkedList<SpriteAnimation>();

	private static final int EXPLOSION_COLUMNS = 5;
	private static final int EXPLOSION_COUNT = 25;
	private static final int EXPLOSION_OFFSET_X = 0;
	private static final int EXPLOSION_OFFSET_Y = 0;
	private static final int EXPLOSION_WIDTH = 64;
	private static final int EXPLOSION_HEIGHT = 64;

	GameBoardModel gbm;

	public GameBoardView(Runnable onTypePaddleMoveLeft, Runnable onTypePaddleMoveRight, GameBoardModel gbm) {
		this.onTypePaddleMoveLeft = onTypePaddleMoveLeft;
		this.onTypePaddleMoveRight = onTypePaddleMoveRight;
		this.gbm = gbm;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		threeHit3Image = new Image("images/threeHit3.png");
		threeHit2Image = new Image("images/threeHit2.png");
		threeHit1Image = new Image("images/threeHit1.png");

		twoHit2Image = new Image("images/twoHit2.png");
		twoHit1Image = new Image("images/twoHit1.png");

		explosiveImage = new Image("images/explosive.png");

		batImage = new Image("images/bat.png");

		ballImage = new Image("images/Ball.png");

		bgImage = new Image("images/Background.png");

		threeHit3Pattern = new ImagePattern(threeHit3Image);
		threeHit2Pattern = new ImagePattern(threeHit2Image);
		threeHit1Pattern = new ImagePattern(threeHit1Image);

		twohit2Pattern = new ImagePattern(twoHit2Image);
		twoHit1Pattern = new ImagePattern(twoHit1Image);

		explosivePattern = new ImagePattern(explosiveImage);

		batPattern = new ImagePattern(batImage);
		ballPattern = new ImagePattern(ballImage);

		layout = new Pane();

		scene = new Scene(layout, TheController.getBoardWidth(), TheController.getBoardHeight());
		BackgroundImage myBI = new BackgroundImage(bgImage, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
		// then you set to your layout
		layout.setBackground(new Background(myBI));

		// Detecting Key movement
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
		//layout.getChildren().add(imageView);
		window.setScene(scene);
		window.show();
	}

	public void drawRectangle(gameComponents.Rectangle r) {
		// find a graphical rectangle in children where r.Id = gr.Id
		// if it isn't there, create one and add it to children
		// then...
		//
		// gr.setwidth, etc. according to r
		int ID = r.getId();
		Optional<Node> graphicalRectNode = layout.getChildren().stream().filter(n -> n instanceof Rectangle)
				.filter(n -> n.getId().equals(Integer.toString(ID))).findFirst();
		Rectangle graphicalRect = null;
		if (graphicalRectNode.isPresent()) {
			graphicalRect = (Rectangle) (graphicalRectNode.get());
		} else {
			graphicalRect = new Rectangle();
			layout.getChildren().add(graphicalRect);
		}

		if (r.getType() == RectangleType.Threehit3) {
			graphicalRect.setFill(threeHit3Pattern);
		} else if (r.getType() == RectangleType.Threehit2) {
			graphicalRect.setFill(threeHit2Pattern);
		} else if (r.getType() == RectangleType.Threehit1) {
			graphicalRect.setFill(threeHit1Pattern);
		} else if (r.getType() == RectangleType.Twohit2) {
			graphicalRect.setFill(twohit2Pattern);
		} else if (r.getType() == RectangleType.Twohit1) {
			graphicalRect.setFill(twoHit1Pattern);
		} else if (r.getType() == RectangleType.ExplosiveBrick) {
			graphicalRect.setFill(explosivePattern);
		} else if (r.getType() == RectangleType.Bat) {
			graphicalRect.setFill(batPattern);
			graphicalRect.setOnMousePressed(rectangleOnMousePressedEventHandler);
			graphicalRect.setOnMouseDragged(rectangleOnMouseDraggedEventHandler);
			graphicalRect.setCursor(Cursor.HAND);
		} else if (r.getType() == RectangleType.Ball) {
			graphicalRect.setFill(ballPattern);
		}
		graphicalRectNode = null;

		if (r instanceof Brick && ((Brick) r).isAlive() == false) {
			graphicalRect.setOpacity(0);

			ImageView imageView = new ImageView(new Image("images/TestAnimationSheet.png"));
			imageView.setTranslateX(r.getTopLeftCoordinate().getX());
			imageView.setTranslateY(r.getTopLeftCoordinate().getY());
			imageView.setViewport(new Rectangle2D(EXPLOSION_OFFSET_X, EXPLOSION_OFFSET_Y, EXPLOSION_WIDTH, EXPLOSION_HEIGHT));
			SpriteAnimation animation = new SpriteAnimation(imageView, Duration.millis(1000), EXPLOSION_COUNT, EXPLOSION_COLUMNS, EXPLOSION_OFFSET_X,
					EXPLOSION_OFFSET_Y, EXPLOSION_WIDTH, EXPLOSION_HEIGHT);
			animation.setCycleCount(1);
			animation.play();
			imageViews.add(imageView);
			animation.setOnFinished(e -> {
				System.out.println("done");
				@SuppressWarnings("unchecked")
				LinkedList<ImageView> imageViewsCopy = (LinkedList<ImageView>) imageViews.clone();
				for (ImageView iv : imageViewsCopy) {
					if (animation.getImageView().equals(iv)) {
						imageViews.remove(iv);
						layout.getChildren().remove(iv);
						break;
					}
				}
			});
			layout.getChildren().add(imageView);

		} else if (r instanceof Packet && ((Packet) r).isConsumed()) {
			graphicalRect.setOpacity(0);
		}
		else if(r instanceof Bat && newTranslateX!=0){
			gbm.movePaddle(newTranslateX);
			newTranslateX=0;
		}
		graphicalRect.setId(Integer.toString(r.getId()));
		graphicalRect.setWidth(r.getWidth());
		graphicalRect.setHeight(r.getHeight());
		graphicalRect.setTranslateX(r.getTopLeftCoordinate().getX());
		graphicalRect.setTranslateY(r.getTopLeftCoordinate().getY());
	}

    EventHandler<MouseEvent> rectangleOnMousePressedEventHandler =
            new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                orgSceneX = t.getSceneX();
                orgSceneY = t.getSceneY();
                orgTranslateX = ((Rectangle)(t.getSource())).getTranslateX();
            }
        };

        EventHandler<MouseEvent> rectangleOnMouseDraggedEventHandler =
            new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                double offsetX = t.getSceneX() - orgSceneX;
                newTranslateX = orgTranslateX + offsetX;
                ((Rectangle)(t.getSource())).setTranslateX(newTranslateX);
            }
        };

	public Scene getScene() {
		return scene;
	}
}