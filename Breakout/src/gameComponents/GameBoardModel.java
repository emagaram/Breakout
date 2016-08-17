package gameComponents;

import java.util.LinkedList;
import java.util.Optional;

import application.TheController;

public class GameBoardModel {
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
	private LinkedList<Ball> balls = new LinkedList<Ball>();
	private LinkedList<Brick> bricks = new LinkedList<Brick>();
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
						brickWidth, brickHeight, RectangleType.Twohit2, 0, 1);
				bricks.add(r);
			}
		}
		// Brick creation algorithm end
		Coordinate batUL = new Coordinate((TheController.getBoardWidth() - BAT_WIDTH) / 2,
				(TheController.getBoardHeight() - BAT_HEIGHT - 30));
		bat = new Rectangle(batUL, BAT_WIDTH, BAT_HEIGHT, RectangleType.Bat);
		balls.add(new Ball(new Coordinate(300, TheController.getBoardHeight() / 2), 10, 10, BALL_SPEED,
				Math.toRadians(175), RectangleType.Ball));
		balls.add(new Ball(new Coordinate(400, TheController.getBoardHeight() / 2), 10, 10, BALL_SPEED,
				Math.toRadians(160), RectangleType.Ball));
		balls.add(new Ball(new Coordinate(100, TheController.getBoardHeight() / 2), 10, 10, BALL_SPEED,
				Math.toRadians(10), RectangleType.Ball));
		balls.add(new Ball(new Coordinate(200, TheController.getBoardHeight() / 2), 10, 10, BALL_SPEED,
				Math.toRadians(70), RectangleType.Ball));
		balls.add(new Ball(new Coordinate(450, TheController.getBoardHeight() / 2), 10, 10, BALL_SPEED,
				Math.toRadians(25), RectangleType.Ball));
		balls.add(new Ball(new Coordinate(150, TheController.getBoardHeight() / 2), 10, 10, BALL_SPEED,
				Math.toRadians(35), RectangleType.Ball));
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
		@SuppressWarnings("unchecked")
		LinkedList<Ball> copyBalls = (LinkedList<Ball>) balls.clone();
		for (Ball ball : copyBalls) {
			if (ball.getBrickItHit() != null) {
				if (ball.getBrickItHit().getHitCount() >= ball.getBrickItHit().getHitResistance()) {
					bricks.add(ball.getBrickItHit().kill());
					bricks.remove(ball.getBrickItHit());
				} else {
					bricks.add(ball.getBrickItHit().hitBrick());
					bricks.remove(ball.getBrickItHit());
				}
				balls.add(ball.setBrickItHit(null));
				balls.remove(ball);
			}

		}
	}

	// Updating Ball and Destroying bricks
	private static Ball calculateBallHitBricks(Ball ball, LinkedList<Brick> bricks, ICollisionDetector detector) {
		Ball nextBall = ball.getMove();
		Optional<Double> r = bricks.stream().map(i -> i.getBottomRightCoordinate().getY()).max(Double::compare);

		@SuppressWarnings("unchecked")
		LinkedList<Brick> copyBricks = (LinkedList<Brick>) bricks.clone();

		if (nextBall.getTopLeftCoordinate().getY() <= r.get()) {
			for (Brick brick : copyBricks) {
				if (brick.isAlive() && detector.basicIntersects(nextBall, brick)) {
					int xScore = xScore(brick, ball, detector);
					int yScore = yScore(brick, ball, detector);
					if (xScore > yScore) {
						return ball.flipXDirection().setBrickItHit(brick);

					} else if (yScore > xScore) {
						return ball.flipYDirection().setBrickItHit(brick);

					} else if (xScore == yScore) {
						System.out.println("XY");
						return ball.flipYDirection().setBrickItHit(brick);
					}
				}
			}
		}
		return ball;
	}

	private static Ball calculateBallHitWall(Ball ball, Rectangle windowRectangle, ICollisionDetector detector) {
		Ball nextBall = ball.getMove();
		LineSegment ballPathCenter = new LineSegment(ball.getCenterCoordinate(), nextBall.getCenterCoordinate());
		LineSegment ballPathTopLeft = new LineSegment(ball.getTopLeftCoordinate(), nextBall.getTopLeftCoordinate());
		LineSegment ballPathBottomRight = new LineSegment(ball.getBottomRightCoordinate(),
				nextBall.getBottomRightCoordinate());
		LineSegment ballPathTopRight = new LineSegment(ball.getTopRightCoordinate(), nextBall.getTopRightCoordinate());
		LineSegment ballPathBottomLeft = new LineSegment(ball.getBottomLeftCoordinate(),
				nextBall.getBottomLeftCoordinate());
		if (detector.intersects(ballPathTopLeft, windowRectangle.getLeftLineSegment())
				|| detector.intersects(ballPathBottomLeft, windowRectangle.getLeftLineSegment())
				|| detector.intersects(ballPathCenter, windowRectangle.getLeftLineSegment())
				|| detector.intersects(ballPathTopRight, windowRectangle.getRightLineSegment())
				|| detector.intersects(ballPathBottomRight, windowRectangle.getRightLineSegment())
				|| detector.intersects(ballPathCenter, windowRectangle.getRightLineSegment())) {
			return ball.flipXDirection();
		} else if (detector.intersects(ballPathTopLeft, windowRectangle.getTopLineSegment())
				|| detector.intersects(ballPathTopRight, windowRectangle.getTopLineSegment())
				|| detector.intersects(ballPathCenter, windowRectangle.getTopLineSegment())) {
			return ball.flipYDirection();
		} else {
			return ball;
		}
	}

	private static Ball calculateBallHitBat(Ball ball, Rectangle bat, ICollisionDetector detector,
			double LEFT_ANGLE_LIMIT, double RIGHT_ANGLE_LIMIT) {

		Ball nextBall = ball.getMove();
		LineSegment ballPathCenter = new LineSegment(ball.getCenterCoordinate(), nextBall.getCenterCoordinate());
		LineSegment ballPathBottomRight = new LineSegment(ball.getBottomRightCoordinate(),
				nextBall.getBottomRightCoordinate());
		LineSegment ballPathBottomLeft = new LineSegment(ball.getBottomLeftCoordinate(),
				nextBall.getBottomLeftCoordinate());

		if ((detector.intersects(ballPathBottomLeft, bat.getTopLineSegment())
				|| detector.intersects(ballPathBottomRight, bat.getTopLineSegment())
				|| detector.intersects(ballPathCenter, bat.getTopLineSegment()))) {
			Ball changedBall = ball.flipYDirection().changeAngleDegrees(ball.calculatePercentageOffsetWith(bat));
			if (changedBall.getAngleInDegrees() > LEFT_ANGLE_LIMIT) {
				changedBall = changedBall.setAngleInDegrees(LEFT_ANGLE_LIMIT);
			} else if (changedBall.getAngleInDegrees() < RIGHT_ANGLE_LIMIT) {
				changedBall = changedBall.setAngleInDegrees(RIGHT_ANGLE_LIMIT);
			}
			return changedBall;
		} else if (detector.intersects(ballPathBottomLeft, bat.getRightLineSegment())
				|| detector.intersects(ballPathBottomRight, bat.getRightLineSegment())
				|| detector.intersects(ballPathCenter, bat.getRightLineSegment())
				|| detector.intersects(ballPathBottomLeft, bat.getLeftLineSegment())
				|| detector.intersects(ballPathBottomRight, bat.getLeftLineSegment())
				|| detector.intersects(ballPathCenter, bat.getLeftLineSegment())) {
			Ball changedBall = ball.flipXDirection().flipYDirection()
					.changeAngleDegrees(ball.calculatePercentageOffsetWith(bat));
			if (changedBall.getAngleInDegrees() > LEFT_ANGLE_LIMIT) {
				changedBall = changedBall.setAngleInDegrees(LEFT_ANGLE_LIMIT);
			} else if (changedBall.getAngleInDegrees() < RIGHT_ANGLE_LIMIT) {
				changedBall = changedBall.setAngleInDegrees(RIGHT_ANGLE_LIMIT);
			}
			return changedBall;
		} else {
			return ball;
		}
	}

	public void moveBalls() {
		@SuppressWarnings("unchecked")
		LinkedList<Ball> ballsCopy = (LinkedList<Ball>) balls.clone();
		for (Ball ball : ballsCopy) {

			// WINDOW COLLISION DETECTION
			if (calculateBallHitWall(ball, windowRectangle, detector) != ball) {
				balls.add(calculateBallHitWall(ball, windowRectangle, detector));
				balls.remove(ball);
			}
			// BAT COLLISION DETECTION
			else if (calculateBallHitBat(ball, bat, detector, LEFT_ANGLE_LIMIT, RIGHT_ANGLE_LIMIT) != ball) {
				balls.add(calculateBallHitBat(ball, bat, detector, LEFT_ANGLE_LIMIT, RIGHT_ANGLE_LIMIT));
				balls.remove(ball);
			}

			else if (calculateBallHitBricks(ball, bricks, detector) != ball) {
				balls.add(calculateBallHitBricks(ball, bricks, detector));
				balls.remove(ball);
			} else {
				balls.add(ball.getMove());
				balls.remove(ball);
			}
			if (ball.getBottomLeftCoordinate().getY() > TheController.getBoardHeight()) {
				balls.remove(ball);
			}
		}

	}

	public void updateAll() {
		moveBalls();
		updateBricks();
	}

	public Rectangle getBat() {
		return bat;
	}

	public LinkedList<Ball> getBalls() {
		return balls;
	}

	public LinkedList<Brick> getBricks() {
		return new LinkedList<Brick>(this.bricks);
	}

	public int getLevelNum() {
		return levelNum;
	}

	public static int yScore(Brick brick, Ball ball, ICollisionDetector detector) {
		ball.setAdjustments(ball, ball.getXAdjustment());
		int count = 0;
		Ball nextBall = ball.getMove();
		LineSegment ballPathCenter = new LineSegment(ball.getCenterCoordinate(), nextBall.getCenterCoordinate());
		LineSegment ballPathTopLeft = new LineSegment(ball.getTopLeftCoordinate(), nextBall.getTopLeftCoordinate());
		LineSegment ballPathBottomRight = new LineSegment(ball.getBottomRightCoordinate(),
				nextBall.getBottomRightCoordinate());
		LineSegment ballPathTopRight = new LineSegment(ball.getTopRightCoordinate(), nextBall.getTopRightCoordinate());
		LineSegment ballPathBottomLeft = new LineSegment(ball.getBottomLeftCoordinate(),
				nextBall.getBottomLeftCoordinate());

		if (detector.intersects(ballPathTopLeft, brick.getTopLineSegment())) {
			if (ball.getBottomLeftCoordinate().getY() >= brick.getTopLeftCoordinate().getY()) {
				ball.setAdjustments(ball.setPosition(ball.getTopLeftCoordinate().getX(),
						(brick.getTopLeftCoordinate().getY() - ball.getHeight() - 0.1)), ball.getXAdjustment());
			}
			count++;
		}
		if (detector.intersects(ballPathBottomRight, brick.getTopLineSegment())) {
			if (ball.getBottomLeftCoordinate().getY() >= brick.getTopLeftCoordinate().getY()) {
				ball.setAdjustments(ball.setPosition(ball.getTopLeftCoordinate().getX(),
						(brick.getTopLeftCoordinate().getY() - ball.getHeight() - 0.1)), ball.getXAdjustment());
			}
			count++;
		}
		if (detector.intersects(ballPathTopRight, brick.getTopLineSegment())) {
			if (ball.getBottomLeftCoordinate().getY() >= brick.getTopLeftCoordinate().getY()) {
				ball.setAdjustments(ball.setPosition(ball.getTopLeftCoordinate().getX(),
						(brick.getTopLeftCoordinate().getY() - ball.getHeight() - 0.1)), ball.getXAdjustment());
			}
			count++;
		}
		if (detector.intersects(ballPathBottomLeft, brick.getTopLineSegment())) {
			if (ball.getBottomLeftCoordinate().getY() >= brick.getTopLeftCoordinate().getY()) {
				ball.setAdjustments(ball.setPosition(ball.getTopLeftCoordinate().getX(),
						(brick.getTopLeftCoordinate().getY() - ball.getHeight() - 0.1)), ball.getXAdjustment());

			}
			count++;
		}
		if (detector.intersects(ballPathCenter, brick.getTopLineSegment())) {
			if (ball.getBottomLeftCoordinate().getY() >= brick.getTopLeftCoordinate().getY()) {
				ball.setAdjustments(ball.setPosition(ball.getTopLeftCoordinate().getX(),
						(brick.getTopLeftCoordinate().getY() - ball.getHeight() - 0.1)), ball.getXAdjustment());

			}
			count++;
		}
		if (detector.intersects(ballPathTopLeft, brick.getBottomLineSegment())) {
			if (ball.getTopLeftCoordinate().getY() <= brick.getBottomLeftCoordinate().getY()) {
				ball.setAdjustments(ball.setPosition(ball.getTopLeftCoordinate().getX(),
						(brick.getBottomLeftCoordinate().getY() + 0.1)), ball.getXAdjustment());

			}
			count++;
		}
		if (detector.intersects(ballPathBottomRight, brick.getBottomLineSegment())) {
			if (ball.getTopLeftCoordinate().getY() <= brick.getBottomLeftCoordinate().getY()) {
				ball.setAdjustments(ball.setPosition(ball.getTopLeftCoordinate().getX(),
						(brick.getBottomLeftCoordinate().getY() + 0.1)), ball.getXAdjustment());

			}
			count++;
		}
		if (detector.intersects(ballPathTopRight, brick.getBottomLineSegment())) {
			if (ball.getTopLeftCoordinate().getY() <= brick.getBottomLeftCoordinate().getY()) {
				ball.setAdjustments(ball.setPosition(ball.getTopLeftCoordinate().getX(),
						(brick.getBottomLeftCoordinate().getY() + 0.1)), ball.getXAdjustment());

			}
			count++;
		}
		if (detector.intersects(ballPathBottomLeft, brick.getBottomLineSegment())) {
			if (ball.getTopLeftCoordinate().getY() <= brick.getBottomLeftCoordinate().getY()) {
				ball.setAdjustments(ball.setPosition(ball.getTopLeftCoordinate().getX(),
						(brick.getBottomLeftCoordinate().getY() + 0.1)), ball.getXAdjustment());

			}
			count++;
		}
		if (detector.intersects(ballPathCenter, brick.getBottomLineSegment())) {
			if (ball.getTopLeftCoordinate().getY() <= brick.getBottomLeftCoordinate().getY()) {
				ball.setAdjustments(ball.setPosition(ball.getTopLeftCoordinate().getX(),
						(brick.getBottomLeftCoordinate().getY() + 0.1)), ball.getXAdjustment());
			}
			count++;
		}
		return count;
	}

	public static int xScore(Brick brick, Ball ball, ICollisionDetector detector) {
		Ball nextBall = ball.getMove();
		LineSegment ballPathCenter = new LineSegment(ball.getCenterCoordinate(), nextBall.getCenterCoordinate());
		LineSegment ballPathTopLeft = new LineSegment(ball.getTopLeftCoordinate(), nextBall.getTopLeftCoordinate());
		LineSegment ballPathBottomRight = new LineSegment(ball.getBottomRightCoordinate(),
				nextBall.getBottomRightCoordinate());
		LineSegment ballPathTopRight = new LineSegment(ball.getTopRightCoordinate(), nextBall.getTopRightCoordinate());
		LineSegment ballPathBottomLeft = new LineSegment(ball.getBottomLeftCoordinate(),
				nextBall.getBottomLeftCoordinate());

		int count = 0;
		ball.setAdjustments(ball.getYAdjustment(), ball);
		if (detector.intersects(ballPathTopLeft, brick.getRightLineSegment())) {
			if (ball.getBottomLeftCoordinate().getX() <= brick.getBottomLeftCoordinate().getX()) {
				ball.setAdjustments(ball.getYAdjustment(), ball.setPosition(
						brick.getBottomLeftCoordinate().getX() - 0.1, (ball.getTopLeftCoordinate().getY())));

			}
			count++;
		}
		if (detector.intersects(ballPathBottomRight, brick.getRightLineSegment())) {
			if (ball.getBottomLeftCoordinate().getX() <= brick.getBottomLeftCoordinate().getX()) {
				ball.setAdjustments(ball.getYAdjustment(), ball.setPosition(
						brick.getBottomLeftCoordinate().getX() - 0.1, (ball.getTopLeftCoordinate().getY())));

			}
			count++;
		}
		if (detector.intersects(ballPathTopRight, brick.getRightLineSegment())) {
			if (ball.getBottomLeftCoordinate().getX() <= brick.getBottomLeftCoordinate().getX()) {
				ball.setAdjustments(ball.getYAdjustment(), ball.setPosition(
						brick.getBottomLeftCoordinate().getX() - 0.1, (ball.getTopLeftCoordinate().getY())));

			}
			count++;
		}
		if (detector.intersects(ballPathBottomLeft, brick.getRightLineSegment())) {
			if (ball.getBottomLeftCoordinate().getX() <= brick.getBottomLeftCoordinate().getX()) {
				ball.setAdjustments(ball.getYAdjustment(), ball.setPosition(
						brick.getBottomLeftCoordinate().getX() - 0.1, (ball.getTopLeftCoordinate().getY())));

			}
			count++;
		}
		if (detector.intersects(ballPathCenter, brick.getRightLineSegment())) {
			if (ball.getBottomLeftCoordinate().getX() <= brick.getBottomLeftCoordinate().getX()) {
				ball.setAdjustments(ball.getYAdjustment(), ball.setPosition(
						brick.getBottomLeftCoordinate().getX() - 0.1, (ball.getTopLeftCoordinate().getY())));
			}
			count++;
		}
		if (detector.intersects(ballPathTopLeft, brick.getLeftLineSegment())) {
			if (ball.getBottomRightCoordinate().getX() >= brick.getBottomRightCoordinate().getX()) {
				ball.setAdjustments(ball.getYAdjustment(),
						ball.setPosition(brick.getBottomRightCoordinate().getX() - ball.getWidth() + 0.1,
								(ball.getTopLeftCoordinate().getY())));
			}
			count++;
		}
		if (detector.intersects(ballPathBottomRight, brick.getLeftLineSegment())) {
			if (ball.getBottomRightCoordinate().getX() >= brick.getBottomRightCoordinate().getX()) {
				ball.setAdjustments(ball.getYAdjustment(),
						ball.setPosition(brick.getBottomRightCoordinate().getX() - ball.getWidth() + 0.1,
								(ball.getTopLeftCoordinate().getY())));
			}
			count++;
		}
		if (detector.intersects(ballPathTopRight, brick.getLeftLineSegment())) {
			if (ball.getBottomRightCoordinate().getX() >= brick.getBottomRightCoordinate().getX()) {
				ball.setAdjustments(ball.getYAdjustment(),
						ball.setPosition(brick.getBottomRightCoordinate().getX() - ball.getWidth() + 0.1,
								(ball.getTopLeftCoordinate().getY())));
			}
			count++;
		}
		if (detector.intersects(ballPathBottomLeft, brick.getLeftLineSegment())) {
			if (ball.getBottomRightCoordinate().getX() >= brick.getBottomRightCoordinate().getX()) {
				ball.setAdjustments(ball.getYAdjustment(),
						ball.setPosition(brick.getBottomRightCoordinate().getX() - ball.getWidth() + 0.1,
								(ball.getTopLeftCoordinate().getY())));
			}
			count++;
		}
		if (detector.intersects(ballPathCenter, brick.getLeftLineSegment())) {
			if (ball.getBottomRightCoordinate().getX() >= brick.getBottomRightCoordinate().getX()) {
				ball.setAdjustments(ball.getYAdjustment(),
						ball.setPosition(brick.getBottomRightCoordinate().getX() - ball.getWidth() + 0.1,
								(ball.getTopLeftCoordinate().getY())));
			}
			count++;
		}
		return count;
	}

}

class BallCollisionResult {
	Ball adjustedBall;
}

class BallHitBrickCollisionResult {
	Ball adjustedBall;
	Brick adjustedBrick;
}
