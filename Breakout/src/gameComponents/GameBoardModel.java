package gameComponents;

import java.util.LinkedList;
import java.util.Optional;

import application.TheController;

public class GameBoardModel {
	private Ball yAdjustment;
	private Ball xAdjustment;
	private Ball nextBall;
	private Brick currentBrick;
	private LineSegment ballPathTopLeft;
	private LineSegment ballPathTopRight;
	private LineSegment ballPathBottomLeft;
	private LineSegment ballPathBottomRight;
	private LineSegment ballPathCenter;

	private boolean hitBat = false;
	private boolean canUseBat = true;
	private boolean hitBrick= false;
	private boolean shouldFlipX = false;
	private boolean shouldFlipY = false;

	private int xScore;
	private int yScore;
	private int waitTimeAfterBrickCollision = 3;
	private int brickCollisionCounter = waitTimeAfterBrickCollision;

	private int levelNum;
	private final double LEFT_ANGLE_LIMIT = -35;
	private final double RIGHT_ANGLE_LIMIT = -150;
	private final int BAT_WIDTH;
	private final int BAT_HEIGHT;
	private final int BAT_SPEED;
	private double BALL_SPEED;
	private int brickHeight; // Similar to brickRowHeight but gap is included
	private int columnWidth;
	private int brickWidth;
	private Rectangle windowRectangle;
	private Rectangle bat;
	private Ball ball;
	private LinkedList<Brick> bricks = new LinkedList<Brick>();
	private LinkedList<Brick> destroyedBricks = new LinkedList<Brick>();
	private ICollisionDetector detector;

	public GameBoardModel(int levelNum, int width, int height, int brickRowHeight, int brickGapH, int brickGapV,
			int gapAboveBricks, int brickColumns, int brickRows, int batWidth, int batHeight, int batSpeed,
			double ballSpeed, ICollisionDetector detector) {
		this.levelNum = levelNum;
		this.detector = detector;
		this.windowRectangle = new Rectangle(new Coordinate(0, 0), TheController.getBoardWidth(),
				TheController.getBoardHeight(), RectangleType.Window);
		if (brickColumns != 0)
			this.columnWidth = (TheController.getBoardWidth() - 10) / brickColumns;
		this.brickWidth = columnWidth - brickGapH;
		this.brickHeight = brickRowHeight - brickGapV;
		this.BAT_WIDTH = batWidth;
		this.BAT_HEIGHT = batHeight;
		this.BAT_SPEED = batSpeed;
		this.BALL_SPEED = ballSpeed;
		// Brick creation algorithm
		for (int column = 0; column < brickColumns; column++) {
			for (int row = 0; row < brickRows; row++) {
				// The 4+ is so the bricks do not appear right on the edges
				Brick r = new Brick(
						new Coordinate(4 + (column * columnWidth) + (brickGapH / 2),
								(row * brickRowHeight) + gapAboveBricks),
						brickWidth, brickHeight, RectangleType.Brick, 0, 2);
				bricks.add(r);
			}
		}
		// Brick creation algorithm end
		Coordinate batUL = new Coordinate((TheController.getBoardWidth() - BAT_WIDTH) / 2,
				(TheController.getBoardHeight() - BAT_HEIGHT - 30));
		bat = new Rectangle(batUL, BAT_WIDTH, BAT_HEIGHT, RectangleType.Bat);
		ball = new Ball(new Coordinate(TheController.getBoardWidth() / 2, TheController.getBoardHeight() / 2), 10, 10,
				BALL_SPEED, Math.toRadians(75), RectangleType.Ball);
	}

	// This method is for manually adding bricks so that they do not have to be
	// in a dumb row
	public void addBrick(int width, int height, int posX, int posY, RectangleType type, int hitResistance) {
		Brick r = new Brick(new Coordinate(posX, posY), width, height, type, 0, hitResistance);
		bricks.add(r);
	}

	public void movePaddleLeft() {
		if ((bat.getTopLeftCoordinate().getX() - BAT_SPEED) >= 0) {
			bat = bat.createMove(-BAT_SPEED, 0, bat.getType());
		}
	}

	public void movePaddleRight() {
		if (bat.getBottomRightCoordinate().getX() + BAT_SPEED <= TheController.getBoardWidth()) {
			bat = bat.createMove(BAT_SPEED, 0, bat.getType());
		}
	}

	public void updateBricks() {
		if(currentBrick!=null&&hitBrick==true){
		Brick brick2 = currentBrick.hitBrick();
		if (brick2.getHitCount() == currentBrick.getHitResistance()) {
			destroyedBricks.add(currentBrick);
		} else {
			brick2 = brick2.chisel();
			bricks.add(brick2);
			bricks.remove(currentBrick);
		}
		hitBrick=false;
		}
		for (Brick brick : destroyedBricks) {
			bricks.remove(brick);
			bricks.add(brick.kill());
			destroyedBricks.remove(brick);
		}
	}

	// Updating Ball and Destroying bricks

	public void calculateBall() {
		nextBall = ball.getMove();
		ballPathCenter = new LineSegment(ball.getCenterCoordinate(), nextBall.getCenterCoordinate());
		ballPathTopLeft = new LineSegment(ball.getTopLeftCoordinate(), nextBall.getTopLeftCoordinate());
		ballPathBottomRight = new LineSegment(ball.getBottomRightCoordinate(), nextBall.getBottomRightCoordinate());
		ballPathTopRight = new LineSegment(ball.getTopRightCoordinate(), nextBall.getTopRightCoordinate());
		ballPathBottomLeft = new LineSegment(ball.getBottomLeftCoordinate(), nextBall.getBottomLeftCoordinate());

		hitBat = false;
		shouldFlipX = detector.intersects(ballPathTopLeft, windowRectangle.getLeftLineSegment())
				|| detector.intersects(ballPathTopRight, windowRectangle.getRightLineSegment());
		shouldFlipY = detector.intersects(ballPathTopRight, windowRectangle.getTopLineSegment())
		// || detector.intersects(ballPath,
		// windowRectangle.getBottomLineSegment())
		;
		Optional<Double> r = bricks.stream().map(i -> i.getBottomRightCoordinate().getY()).max(Double::compare);
		// Variables that will let the ball know what new object to be

		// If Ball made a collision with the bat, then the bat will not be able
		// to cause collision untill ball is above bat's topLeftY coordinate

		// Bat collision detection
		if (shouldFlipX == false && shouldFlipY == false
				&&
				(detector.intersects(ballPathBottomRight, bat.getTopLineSegment())||
				detector.intersects(ballPathBottomLeft, bat.getTopLineSegment()))
				&& canUseBat == true) {
			shouldFlipY = true;
			hitBat = true;
			canUseBat = false;
		}
		else if (shouldFlipX == false && shouldFlipY == false && detector.basicIntersects(ball, bat) && canUseBat == true) {
			System.out.println("XANDY");
			shouldFlipX = true;
			shouldFlipY = true;
			hitBat = true;
			canUseBat = false;
		}
		// Bat collision detection end

		// Brick collision detection
		else if (shouldFlipX == false && shouldFlipY == false
				&& ball.getTopLeftCoordinate().getY() <= r.get() + waitTimeAfterBrickCollision * ball.getSpeed()) {
			if (brickCollisionCounter >= waitTimeAfterBrickCollision) {
				for (Brick brick : bricks) {
					if (brick.isAlive() && detector.basicIntersects(nextBall, brick)) {
						xScore = xScore(brick, ball, nextBall);
						yScore = yScore(brick, ball, nextBall);
						if (xScore > yScore) {
							shouldFlipX = true;

						} else if (yScore > xScore) {
							shouldFlipY = true;

						} else if (xScore == yScore) {
							System.out.println("XY");
							shouldFlipX = true;
							shouldFlipY = true;
						}
						brickCollisionCounter = 0;
						currentBrick=brick;
						hitBrick=true;
					}
				}

			}
		}
	}
	// Brick collision detection end

	// Move ball to new position
	public void moveBall() {
		if (shouldFlipX == false && shouldFlipY == false) {
			ball = nextBall;
			brickCollisionCounter++;
			if (brickCollisionCounter > waitTimeAfterBrickCollision) {
				brickCollisionCounter = waitTimeAfterBrickCollision;
			}

		} else {
			if (shouldFlipY == true && hitBat == true) {
				// Angle limit is between -160 and +160
				// For every 1% offset, the ball should move 1 degree
				ball = ball.flipYDirection().changeAngleDegrees(ball.calculatePercentageOffsetWith(bat));

				if (ball.getAngleInDegrees() > LEFT_ANGLE_LIMIT) {
					ball = ball.setAngleInDegrees(LEFT_ANGLE_LIMIT);
				} else if (ball.getAngleInDegrees() < RIGHT_ANGLE_LIMIT) {
					ball = ball.setAngleInDegrees(RIGHT_ANGLE_LIMIT);
				}
			} else {
				if(xScore>yScore&&hitBrick==true){
					ball=xAdjustment;
				}
				else if(yScore>xScore&&hitBrick==true){
					ball=yAdjustment;
				}
				if (shouldFlipX == true) {
					ball = ball.flipXDirection();
				}
				if (shouldFlipY == true) {
					ball = ball.flipYDirection();
				}
			}
			if (ball.getBottomLeftCoordinate().getY() < bat.getTopLeftCoordinate().getY()) {
				canUseBat = true;
			}
		}
	}

	// Update Ball End

	public void updateAll() {
		calculateBall();
		moveBall();
		updateBricks();
	}

	public Rectangle getBat() {
		return bat;
	}

	public Rectangle getBall() {
		return ball;
	}

	public LinkedList<Brick> getBricks() {
		return new LinkedList<Brick>(this.bricks);
	}

	public int getLevelNum() {
		return levelNum;
	}

	public int yScore(Brick brick, Ball ball, Ball nextBall) {
		boolean adjustedBall = false;
		yAdjustment = ball;
		int count = 0;
		if (detector.intersects(ballPathTopLeft, brick.getTopLineSegment())) {
			if (ball.getBottomLeftCoordinate().getY() >= brick.getTopLeftCoordinate().getY() && adjustedBall == false) {
				yAdjustment = ball.setPosition(ball.getTopLeftCoordinate().getX(),
						(brick.getTopLeftCoordinate().getY() - ball.getHeight() - 0.1));
				adjustedBall = true;

			}
			count++;
		}
		if (detector.intersects(ballPathBottomRight, brick.getTopLineSegment())) {
			if (ball.getBottomLeftCoordinate().getY() >= brick.getTopLeftCoordinate().getY() && adjustedBall == false) {
				yAdjustment = ball.setPosition(ball.getTopLeftCoordinate().getX(),
						(brick.getTopLeftCoordinate().getY() - ball.getHeight() - 0.1));
				adjustedBall = true;

			}
			count++;
		}
		if (detector.intersects(ballPathTopRight, brick.getTopLineSegment())) {
			if (ball.getBottomLeftCoordinate().getY() >= brick.getTopLeftCoordinate().getY() && adjustedBall == false) {
				yAdjustment = ball.setPosition(ball.getTopLeftCoordinate().getX(),
						(brick.getTopLeftCoordinate().getY() - ball.getHeight() - 0.1));
				adjustedBall = true;
			}
			count++;
		}
		if (detector.intersects(ballPathBottomLeft, brick.getTopLineSegment())) {
			if (ball.getBottomLeftCoordinate().getY() >= brick.getTopLeftCoordinate().getY() && adjustedBall == false) {
				yAdjustment = ball.setPosition(ball.getTopLeftCoordinate().getX(),
						(brick.getTopLeftCoordinate().getY() - ball.getHeight() - 0.1));
				adjustedBall = true;

			}
			count++;
		}
		if (detector.intersects(ballPathCenter, brick.getTopLineSegment())) {
			if (ball.getBottomLeftCoordinate().getY() >= brick.getTopLeftCoordinate().getY() && adjustedBall == false) {
				yAdjustment = ball.setPosition(ball.getTopLeftCoordinate().getX(),
						(brick.getTopLeftCoordinate().getY() - ball.getHeight() - 0.1));
				adjustedBall = true;

			}
			count++;
		}
		if (detector.intersects(ballPathTopLeft, brick.getBottomLineSegment())) {
			if (ball.getTopLeftCoordinate().getY() <= brick.getBottomLeftCoordinate().getY() && adjustedBall == false) {
				yAdjustment = ball.setPosition(ball.getTopLeftCoordinate().getX(),
						(brick.getBottomLeftCoordinate().getY() + 0.1));
				adjustedBall = true;

			}
			count++;
		}
		if (detector.intersects(ballPathBottomRight, brick.getBottomLineSegment())) {
			if (ball.getTopLeftCoordinate().getY() <= brick.getBottomLeftCoordinate().getY() && adjustedBall == false) {
				yAdjustment = ball.setPosition(ball.getTopLeftCoordinate().getX(),
						(brick.getBottomLeftCoordinate().getY() + 0.1));
				adjustedBall = true;

			}
			count++;
		}
		if (detector.intersects(ballPathTopRight, brick.getBottomLineSegment())) {
			if (ball.getTopLeftCoordinate().getY() <= brick.getBottomLeftCoordinate().getY() && adjustedBall == false) {
				yAdjustment = ball.setPosition(ball.getTopLeftCoordinate().getX(),
						(brick.getBottomLeftCoordinate().getY() + 0.1));
				adjustedBall = true;

			}
			count++;
		}
		if (detector.intersects(ballPathBottomLeft, brick.getBottomLineSegment())) {
			if (ball.getTopLeftCoordinate().getY() <= brick.getBottomLeftCoordinate().getY() && adjustedBall == false) {
				yAdjustment = ball.setPosition(ball.getTopLeftCoordinate().getX(),
						(brick.getBottomLeftCoordinate().getY() + 0.1));
				adjustedBall = true;

			}
			count++;
		}
		if (detector.intersects(ballPathCenter, brick.getBottomLineSegment())) {
			if (ball.getTopLeftCoordinate().getY() <= brick.getBottomLeftCoordinate().getY() && adjustedBall == false) {
				yAdjustment = ball.setPosition(ball.getTopLeftCoordinate().getX(),
						(brick.getBottomLeftCoordinate().getY() + 0.1));
				adjustedBall = true;

			}
			count++;
		}
		return count;
	}

	public int xScore(Brick brick, Ball ball, Ball nextBall) {
		boolean adjustedBall = false;
		int count = 0;
		xAdjustment = ball;
		if (detector.intersects(ballPathTopLeft, brick.getRightLineSegment())) {
			if (ball.getBottomLeftCoordinate().getX() <= brick.getBottomLeftCoordinate().getX()
					&& adjustedBall == false) {
				xAdjustment = ball.setPosition(brick.getBottomLeftCoordinate().getX() - 0.1,
						(ball.getTopLeftCoordinate().getY()));
				adjustedBall = true;

			}
			count++;
		}
		if (detector.intersects(ballPathBottomRight, brick.getRightLineSegment())) {
			if (ball.getBottomLeftCoordinate().getX() <= brick.getBottomLeftCoordinate().getX()
					&& adjustedBall == false) {
				xAdjustment = ball.setPosition(brick.getBottomLeftCoordinate().getX() - 0.1,
						(ball.getTopLeftCoordinate().getY()));
				adjustedBall = true;

			}
			count++;
		}
		if (detector.intersects(ballPathTopRight, brick.getRightLineSegment())) {
			if (ball.getBottomLeftCoordinate().getX() <= brick.getBottomLeftCoordinate().getX()
					&& adjustedBall == false) {
				xAdjustment = ball.setPosition(brick.getBottomLeftCoordinate().getX() - 0.1,
						(ball.getTopLeftCoordinate().getY()));
				adjustedBall = true;

			}
			count++;
		}
		if (detector.intersects(ballPathBottomLeft, brick.getRightLineSegment())) {
			if (ball.getBottomLeftCoordinate().getX() <= brick.getBottomLeftCoordinate().getX()
					&& adjustedBall == false) {
				xAdjustment = ball.setPosition(brick.getBottomLeftCoordinate().getX() - 0.1,
						(ball.getTopLeftCoordinate().getY()));
				adjustedBall = true;

			}
			count++;
		}
		if (detector.intersects(ballPathCenter, brick.getRightLineSegment())) {
			if (ball.getBottomLeftCoordinate().getX() <= brick.getBottomLeftCoordinate().getX()
					&& adjustedBall == false) {
				xAdjustment = ball.setPosition(brick.getBottomLeftCoordinate().getX() - 0.1,
						(ball.getTopLeftCoordinate().getY()));
				adjustedBall = true;

			}
			count++;
		}
		if (detector.intersects(ballPathTopLeft, brick.getLeftLineSegment())) {
			if (ball.getBottomRightCoordinate().getX() >= brick.getBottomRightCoordinate().getX()
					&& adjustedBall == false) {
				xAdjustment = ball.setPosition(brick.getBottomRightCoordinate().getX() - ball.getWidth() + 0.1,
						(ball.getTopLeftCoordinate().getY()));
				adjustedBall = true;

			}
			count++;
		}
		if (detector.intersects(ballPathBottomRight, brick.getLeftLineSegment())) {
			if (ball.getBottomRightCoordinate().getX() >= brick.getBottomRightCoordinate().getX()
					&& adjustedBall == false) {
				xAdjustment = ball.setPosition(brick.getBottomRightCoordinate().getX() - ball.getWidth() + 0.1,
						(ball.getTopLeftCoordinate().getY()));
				adjustedBall = true;

			}
			count++;
		}
		if (detector.intersects(ballPathTopRight, brick.getLeftLineSegment())) {
			if (ball.getBottomRightCoordinate().getX() >= brick.getBottomRightCoordinate().getX()
					&& adjustedBall == false) {
				xAdjustment = ball.setPosition(brick.getBottomRightCoordinate().getX() - ball.getWidth() + 0.1,
						(ball.getTopLeftCoordinate().getY()));
				adjustedBall = true;

			}
			count++;
		}
		if (detector.intersects(ballPathBottomLeft, brick.getLeftLineSegment())) {
			if (ball.getBottomRightCoordinate().getX() >= brick.getBottomRightCoordinate().getX()
					&& adjustedBall == false) {
				xAdjustment = ball.setPosition(brick.getBottomRightCoordinate().getX() - ball.getWidth() + 0.1,
						(ball.getTopLeftCoordinate().getY()));
				adjustedBall = true;

			}
			count++;
		}
		if (detector.intersects(ballPathCenter, brick.getLeftLineSegment())) {
			if (ball.getBottomRightCoordinate().getX() >= brick.getBottomRightCoordinate().getX()
					&& adjustedBall == false) {
				xAdjustment = ball.setPosition(brick.getBottomRightCoordinate().getX() - ball.getWidth() + 0.1,
						(ball.getTopLeftCoordinate().getY()));
				adjustedBall = true;

			}
			count++;
		}
		return count;
	}

}
