package application;

import java.util.LinkedList;
import java.util.Optional;

import gameComponenets.Ball;
import gameComponenets.Brick;
import gameComponenets.Coordinate;
import gameComponenets.ICollisionDetector;
import gameComponenets.LineSegment;
import gameComponenets.Rectangle;
import gameComponenets.RectangleType;

public class GameBoardModel {
	private boolean canUseBat = true;
	private final double LEFT_ANGLE_LIMIT = -35;
	private final double RIGHT_ANGLE_LIMIT = -150;
	public static int WIDTH;
	public static int HEIGHT;
	private final int BAT_WIDTH;
	private final int BAT_HEIGHT;
	private final int BAT_SPEED;
	private double BALL_SPEED;
	private int gapAboveBricks;
	private int brickRowHeight; // The height of a row of bricks, gap is not
								// included
	private int brickHeight; // Similar to brickRowHeight but gap is included
	private int brickGapH; // The gap that will separate the bricks horizontally
	private int brickGapV; // The gap that will separate the bricks vertically
	private int brickColumns; // The number of columns of bricks
	private int brickRows; // The number of rows of bricks
	private int columnWidth;
	private int brickWidth;
	private Rectangle windowRectangle;
	private Rectangle bat;
	private Ball ball;
	private LinkedList<Brick> bricks = new LinkedList<Brick>();
	private LinkedList<Brick> destroyedBricks = new LinkedList<Brick>();
	private ICollisionDetector detector;

	public GameBoardModel(int width, int height, int brickRowHeight, int brickGapH, int brickGapV, int gapAboveBricks,
			int brickColumns, int brickRows, int batWidth,int batHeight,int batSpeed, double ballSpeed,ICollisionDetector detector) {
		WIDTH = width;
		HEIGHT = height;
		this.detector = detector;
		this.windowRectangle = new Rectangle(new Coordinate(0, 0), WIDTH, HEIGHT, RectangleType.Window);
		this.brickColumns = brickColumns;
		this.brickRows = brickRows;
		this.brickRowHeight = brickRowHeight;
		this.brickGapH = brickGapH;
		this.brickGapV = brickGapV;
		this.gapAboveBricks = gapAboveBricks;
		this.columnWidth = WIDTH / brickColumns;
		this.brickWidth = columnWidth - brickGapH;
		this.brickHeight = brickRowHeight - brickGapV;
		this.BAT_WIDTH=batWidth;
		this.BAT_HEIGHT=batHeight;
		this.BAT_SPEED=batSpeed;
		this.BALL_SPEED=ballSpeed;
		// Brick creation algorithm
		for (int column = 0; column < brickColumns; column++) {
			for (int row = 0; row < brickRows; row++) {
				Brick r = new Brick(new Coordinate((column * columnWidth) + (brickGapH / 2),
						(row * brickRowHeight) + gapAboveBricks), brickWidth, brickHeight, RectangleType.Brick,0,2);
				bricks.add(r);
			}
		}
		// Brick creation algorithm end
		Coordinate batUL = new Coordinate((WIDTH - BAT_WIDTH) / 2, (HEIGHT - BAT_HEIGHT));
		bat = new Rectangle(batUL, BAT_WIDTH, BAT_HEIGHT, RectangleType.Bat);
		ball = new Ball(new Coordinate(WIDTH / 2, HEIGHT / 2), 10, 10, BALL_SPEED, Math.toRadians(75),
				RectangleType.Ball);
	}

	//This method is for manually adding bricks so that they do not have to be in a dumb row
	public void addBrick(int width, int height, int posX, int posY, RectangleType type, int hitResistance){
		Brick r = new Brick(new Coordinate(posX,posY), width, height, type,0,hitResistance);
		bricks.add(r);
	}


	public void movePaddleLeft() {
		if ((bat.getTopLeftCoordinate().getX() - BAT_SPEED) >= 0) {
			bat = bat.createMove(-BAT_SPEED, 0, bat.getType());
		}
	}

	public void movePaddleRight() {
		if (bat.getBottomRightCoordinate().getX() + BAT_SPEED <= WIDTH) {
			bat = bat.createMove(BAT_SPEED, 0, bat.getType());
		}
	}

	public void updateBricks() {
		for (Brick b : destroyedBricks) {
			b.setOpacity(0);
			b.setAlive(false);
		}
	}

	//Updating Ball and Destroying bricks

	public void updateBallAndDestroyBricks() {
		Ball nextBall = ball.getMove();
		LineSegment ballPathCenter = new LineSegment(ball.getCenterCoordinate(), ball.getMove().getCenterCoordinate());
		LineSegment ballPathTopLeft = new LineSegment(ball.getTopLeftCoordinate(),
				ball.getMove().getTopLeftCoordinate());
		LineSegment ballPathBottomRight = new LineSegment(ball.getBottomRightCoordinate(),
				ball.getMove().getBottomRightCoordinate());
		LineSegment ballPathTopRight = new LineSegment(ball.getTopRightCoordinate(),
				ball.getMove().getBottomRightCoordinate());
		LineSegment ballPathBottomLeft = new LineSegment(ball.getBottomLeftCoordinate(),
				ball.getMove().getBottomLeftCoordinate());

		boolean hitBat = false;
		boolean shouldFlipX = detector.intersects(ballPathCenter, windowRectangle.getLeftLineSegment())
				|| detector.intersects(ballPathCenter, windowRectangle.getRightLineSegment());
		boolean shouldFlipY = detector.intersects(ballPathCenter, windowRectangle.getTopLineSegment())
		// || detector.intersects(ballPath,
		// windowRectangle.getBottomLineSegment())
		;
		Optional<Double> r = bricks.stream().map(i -> i.getBottomRightCoordinate().getY()).max(Double::compare);
		// Variables that will let the ball know what new object to be

		// If Ball made a collision with the bat, then the bat will not be able
		// to cause collision untill ball is above bat's topLeftY coordinate

		// Bat collision detection
		if (shouldFlipX == false && shouldFlipY == false && detector.basicIntersects(ball, bat) && canUseBat == true
				&& ball.getTopLeftCoordinate().getY() >= bat.getTopLeftCoordinate().getY()) {
			shouldFlipX = true;
			shouldFlipY = true;
			hitBat = true;
			canUseBat = false;
		} else if (shouldFlipX == false && shouldFlipY == false
				&& detector.intersects(ballPathBottomRight, bat.getTopLineSegment()) && canUseBat == true) {
			shouldFlipY = true;
			hitBat = true;
			canUseBat = false;
		}
		// Bat collision detection end


		// Brick collision detection
		else if (shouldFlipX == false && shouldFlipY == false && ball.getTopLeftCoordinate().getY()<=r.get()) {
			for (Brick brick : bricks) {
				if (brick.isAlive()) {
					if (detector.intersects(ballPathTopLeft, brick.getBottomLineSegment())
							|| detector.intersects(ballPathBottomRight, brick.getBottomLineSegment())
							|| detector.intersects(ballPathTopRight, brick.getBottomLineSegment())
							|| detector.intersects(ballPathBottomLeft, brick.getBottomLineSegment())) {
						shouldFlipY = true;
						if (ball.getTopLeftCoordinate().getY() < brick.getBottomLeftCoordinate().getY()) {
							ball = ball.setPosition(ball.getTopLeftCoordinate().getX(),
									(brick.getBottomLeftCoordinate().getY()+1));
						}
					}
					if (detector.intersects(ballPathTopLeft, brick.getTopLineSegment())
							|| detector.intersects(ballPathBottomRight, brick.getTopLineSegment())
							|| detector.intersects(ballPathTopRight, brick.getTopLineSegment())
							|| detector.intersects(ballPathBottomLeft, brick.getTopLineSegment())) {
						if (ball.getBottomLeftCoordinate().getY() > brick.getTopLeftCoordinate().getY()) {
							ball = ball.setPosition(ball.getTopLeftCoordinate().getX(),
									(brick.getTopLeftCoordinate().getY() - ball.getHeight()-1));
						}
						shouldFlipY = true;
					}
					if (detector.intersects(ballPathTopLeft, brick.getRightLineSegment())
							|| detector.intersects(ballPathBottomRight, brick.getRightLineSegment())
							|| detector.intersects(ballPathTopRight, brick.getRightLineSegment())
							|| detector.intersects(ballPathBottomLeft, brick.getRightLineSegment())) {
						if (ball.getBottomLeftCoordinate().getX() < brick.getBottomLeftCoordinate().getX()) {
							ball = ball.setPosition(brick.getBottomLeftCoordinate().getX(),
									(ball.getTopLeftCoordinate().getY()));
						}
						shouldFlipX = true;
					}

					if (detector.intersects(ballPathTopLeft, brick.getLeftLineSegment())
							|| detector.intersects(ballPathBottomRight, brick.getLeftLineSegment())
							|| detector.intersects(ballPathTopRight, brick.getLeftLineSegment())
							|| detector.intersects(ballPathBottomLeft, brick.getLeftLineSegment())) {
						if (ball.getBottomRightCoordinate().getX() > brick.getBottomRightCoordinate().getX()) {
							ball = ball.setPosition(brick.getBottomRightCoordinate().getX()-ball.getWidth(),
									(ball.getTopLeftCoordinate().getY()));
						}
						shouldFlipX = true;
					}
					if (shouldFlipY == true || shouldFlipX == true) {
						Brick brick2 = brick.hitBrick();

						if(brick2.getHitCount()==brick.getHitResistance()){
							destroyedBricks.add(brick);
						}
						else{
							brick2=brick2.chisel();
							bricks.add(brick2);
							bricks.remove(brick);
						}
						break;
					}
				}
			}
		}
		// Brick collision detection end

		// Calculating ball's new position
		if (shouldFlipX == false && shouldFlipY == false) {
			ball = nextBall;
		} else if (shouldFlipX == true && shouldFlipY == true) {
			ball = ball.flipXDirection().flipYDirection();
		} else if (shouldFlipX == true && shouldFlipY == false) {
			ball = ball.flipXDirection();
		} else if (shouldFlipX == false && shouldFlipY == true && hitBat == true) {
			// Angle limit is between -160 and +160
			// For every 1% offset, the ball should move 1 degree
			ball = ball.flipYDirection().changeAngleDegrees(ball.calculatePercentageOffsetWith(bat));

			if (ball.getAngleInDegrees() > LEFT_ANGLE_LIMIT) {
				ball = ball.setAngleInDegrees(LEFT_ANGLE_LIMIT);
			} else if (ball.getAngleInDegrees() < RIGHT_ANGLE_LIMIT) {
				ball = ball.setAngleInDegrees(RIGHT_ANGLE_LIMIT);
			}

		} else if (shouldFlipX == false && shouldFlipY == true) {
			ball = ball.flipYDirection();
		}
		if (ball.getBottomLeftCoordinate().getY() < bat.getTopLeftCoordinate().getY()) {
			canUseBat = true;
		}
	}
	//Update Ball End

	public void updateAll() {
		//double oldBallX = ball.getCenterCoordinate().getX();
		updateBallAndDestroyBricks();
		updateBricks();
		//bat = bat.createMove(ball.getCenterCoordinate().getX() - oldBallX, 0, bat.getType());
		// bat.getType());
	}

	public Rectangle getBat() {
		return bat;
	}

	public Rectangle getBall() {
		return ball;
	}

	public int getWidth() {
		return WIDTH;
	}

	public int getHeight() {
		return HEIGHT;
	}

	public LinkedList<Brick> getBricks() {
		return new LinkedList<Brick>(this.bricks);
	}
}
