package gameComponents;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Random;

import application.TheController;

public class GameBoardModel {
	private Random random;
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
	private LinkedList<Packet> packets = new LinkedList<Packet>();

	private ICollisionDetector detector;

	public GameBoardModel(int levelNum, int width, int height, int brickRowHeight, int brickGapH, int brickGapV,
			int gapAboveBricks, int brickColumns, int brickRows, int batWidth, int batHeight, int batSpeed,
			double ballSpeed, ICollisionDetector detector) {
		this.random = new Random();
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
		balls.add(new Ball(new Coordinate(300, TheController.getBoardHeight() / 2 + 100), 10, 10, BALL_SPEED,
				Math.toRadians(225), RectangleType.Ball));
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

	// Updating Ball and Destroying bricks
	private static Ball calculateBallHitBricks(Ball ball, LinkedList<Brick> bricks, ICollisionDetector detector) {
		Ball nextBall = ball.getMove();
		Optional<Double> r = bricks.stream().map(i -> i.getBottomRightCoordinate().getY()).max(Double::compare);

		@SuppressWarnings("unchecked")
		LinkedList<Brick> copyBricks = (LinkedList<Brick>) bricks.clone();

		if (nextBall.getTopLeftCoordinate().getY() <= r.get()) {
			for (Brick brick : copyBricks) {
				if (brick.isAlive() && detector.basicIntersects(nextBall, brick)) {
					ball = ball.setBrickItHit(brick).setSpeed(ball.getSpeed() + 0.03);
					int xScore = xScore(brick, ball, detector);
					int yScore = yScore(brick, ball, detector);
					if (xScore > yScore) {
						return ball.flipXDirection();
					} else if (yScore > xScore) {
						return ball.flipYDirection();

					} else if (xScore == yScore) {
						System.out.println("XY");
						return ball.flipYDirection();
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
		LineSegment ballPathBottomRight = new LineSegment(ball.getBottomRightCoordinate(),
				nextBall.getBottomRightCoordinate());
		LineSegment ballPathTopLeft = new LineSegment(ball.getTopLeftCoordinate(), nextBall.getTopLeftCoordinate());
		LineSegment ballPathTopRight = new LineSegment(ball.getTopRightCoordinate(), nextBall.getTopRightCoordinate());
		LineSegment ballPathBottomLeft = new LineSegment(ball.getBottomLeftCoordinate(),
				nextBall.getBottomLeftCoordinate());

		boolean canUseBat = ball.getCanUseBat();

		if (ball.getBottomLeftCoordinate().getY() < bat.getTopLeftCoordinate().getY()) {
			canUseBat = true;
			if (ball.getCanUseBat() == false) {
				return ball.setCanUseBat(true).getMove();
			}
		}
		if (ball.getCanUseBat() == false) {
			return ball.getMove();
		}

		if (canUseBat) {
			if (detector.basicIntersects(ball, bat)
					&& !(detector.intersects(ballPathBottomLeft, bat.getTopLineSegment())
							|| detector.intersects(ballPathBottomRight, bat.getTopLineSegment())
							|| detector.intersects(ballPathTopLeft, bat.getBottomLineSegment())
							|| detector.intersects(ballPathTopRight, bat.getBottomLineSegment()))) {
				Ball changedBall = ball.flipXDirection().flipYDirection()
						.changeAngleDegrees(ball.calculatePercentageOffsetWith(bat)).getMove().setCanUseBat(false);
				if (changedBall.getAngleInDegrees() > LEFT_ANGLE_LIMIT) {
					changedBall = changedBall.setAngleInDegrees(LEFT_ANGLE_LIMIT);
				} else if (changedBall.getAngleInDegrees() < RIGHT_ANGLE_LIMIT) {
					changedBall = changedBall.setAngleInDegrees(RIGHT_ANGLE_LIMIT);
				}

				return changedBall.setCanUseBat(false);
			} else if ((detector.intersects(ballPathBottomLeft, bat.getTopLineSegment())
					|| detector.intersects(ballPathBottomRight, bat.getTopLineSegment()))) {
				Ball changedBall = ball.flipYDirection().changeAngleDegrees(ball.calculatePercentageOffsetWith(bat))
						.setCanUseBat(false);
				if (changedBall.getAngleInDegrees() > LEFT_ANGLE_LIMIT) {
					changedBall = changedBall.setAngleInDegrees(LEFT_ANGLE_LIMIT);
				} else if (changedBall.getAngleInDegrees() < RIGHT_ANGLE_LIMIT) {
					changedBall = changedBall.setAngleInDegrees(RIGHT_ANGLE_LIMIT);
				}

				if (changedBall.getBottomLeftCoordinate().getY() >= bat.getTopLeftCoordinate().getY()) {
					changedBall = changedBall.setPosition(changedBall.getTopLeftCoordinate().getX(),
							(bat.getTopLeftCoordinate().getY() - changedBall.getHeight()));
				}
				return changedBall;
			}
		}
		return ball;
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

			// BRICK COLLISION DETECTION
			else if (calculateBallHitBricks(ball, bricks, detector) != ball) {
				balls.add(calculateBallHitBricks(ball, bricks, detector));
				balls.remove(ball);
			} else {
				balls.add(ball.getMove());
				balls.remove(ball);
			}
		}
	}

	public void movePackets() {
		@SuppressWarnings("unchecked")
		LinkedList<Packet> copyPackets = (LinkedList<Packet>) packets.clone();
		for (Packet packet : copyPackets) {
			if (detector.basicIntersects(packet, bat)) { 
				packets.add(packet.setConsumed(true));
				packets.remove(packet);

				if (packet.getType() == RectangleType.BallPacket) {
					balls.add(new Ball(new Coordinate(TheController.getBoardWidth()/2, TheController.getBoardHeight() / 2), 10, 10, BALL_SPEED,
							Math.toRadians(25), RectangleType.Ball));
					System.out.println("BALLS");
				} else if (packet.getType() == RectangleType.GunPacket) {
					System.out.println("GUNZZ");
				} else if (packet.getType() == RectangleType.UnstoppablePacket) {
					System.out.println("UNSTAWPPABLE");
				}

			} else {
				packets.remove(packet);
				packets.add(packet.getMove());
			}
		}
	}

	public void cleanBricks() {
		@SuppressWarnings("unchecked")
		LinkedList<Brick> copyBricks = (LinkedList<Brick>) bricks.clone();
		for (Brick brick : copyBricks) {
			if (!brick.isAlive()) {
				bricks.remove(brick);
			}
		}
	}

	public void cleanBalls() {
		@SuppressWarnings("unchecked")
		LinkedList<Ball> copyBalls = (LinkedList<Ball>) balls.clone();
		for (Ball ball : copyBalls) {
			if (ball.getBottomLeftCoordinate().getY() > TheController.getBoardHeight()) {
				balls.remove(ball);
			}
		}
	}

	public void cleanPackets() {
		@SuppressWarnings("unchecked")
		LinkedList<Packet> copyPackets = (LinkedList<Packet>) packets.clone();
		for (Packet packet : copyPackets) {
			if (packet.isConsumed() || packet.getTopLeftCoordinate().getY() > TheController.getBoardHeight()) {
				packets.remove(packet);
			}
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

					int odds = 2;
					if (random.nextInt(odds) == (odds - 1)) {
						int randomNum = random.nextInt(3);
						if (randomNum == 0) {
							packets.add(new Packet(ball.getBrickItHit().getCenterCoordinate(), 10, 10,
									RectangleType.BallPacket, 0.6, false));
						} else if (randomNum == 1) {
							packets.add(new Packet(ball.getBrickItHit().getCenterCoordinate(), 10, 10,
									RectangleType.UnstoppablePacket, 0.6, false));
						} else if (randomNum == 2) {
							packets.add(new Packet(ball.getBrickItHit().getCenterCoordinate(), 10, 10,
									RectangleType.GunPacket, 0.6, false));
						}
					}
				} else {
					bricks.add(ball.getBrickItHit().hitBrick());
					bricks.remove(ball.getBrickItHit());
				}
				balls.add(ball.setBrickItHit(null));
				balls.remove(ball);
			}

		}
	}

	public void updateAll() {
		cleanBalls();
		cleanPackets();
		cleanBricks();

		moveBalls();
		movePackets();

		updateBricks();
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

	public static int yScore(Brick brick, Ball ball, ICollisionDetector detector) {
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
			count++;
		}
		if (detector.intersects(ballPathBottomRight, brick.getTopLineSegment())) {
			count++;
		}
		if (detector.intersects(ballPathTopRight, brick.getTopLineSegment())) {
			count++;
		}
		if (detector.intersects(ballPathBottomLeft, brick.getTopLineSegment())) {
			count++;
		}
		if (detector.intersects(ballPathCenter, brick.getTopLineSegment())) {
			count++;
		}
		if (detector.intersects(ballPathTopLeft, brick.getBottomLineSegment())) {
			count++;
		}
		if (detector.intersects(ballPathBottomRight, brick.getBottomLineSegment())) {
			count++;
		}
		if (detector.intersects(ballPathTopRight, brick.getBottomLineSegment())) {
			count++;
		}
		if (detector.intersects(ballPathBottomLeft, brick.getBottomLineSegment())) {
			count++;
		}
		if (detector.intersects(ballPathCenter, brick.getBottomLineSegment())) {
			count++;
		}
		return count;
	}

	public static int xScore(Brick brick, Ball ball, ICollisionDetector detector) {
		int count = 0;
		Ball nextBall = ball.getMove();
		LineSegment ballPathCenter = new LineSegment(ball.getCenterCoordinate(), nextBall.getCenterCoordinate());
		LineSegment ballPathTopLeft = new LineSegment(ball.getTopLeftCoordinate(), nextBall.getTopLeftCoordinate());
		LineSegment ballPathBottomRight = new LineSegment(ball.getBottomRightCoordinate(),
				nextBall.getBottomRightCoordinate());
		LineSegment ballPathTopRight = new LineSegment(ball.getTopRightCoordinate(), nextBall.getTopRightCoordinate());
		LineSegment ballPathBottomLeft = new LineSegment(ball.getBottomLeftCoordinate(),
				nextBall.getBottomLeftCoordinate());
		if (detector.intersects(ballPathTopLeft, brick.getRightLineSegment())) {
			count++;
		}
		if (detector.intersects(ballPathBottomRight, brick.getRightLineSegment())) {
			count++;
		}
		if (detector.intersects(ballPathTopRight, brick.getRightLineSegment())) {
			count++;
		}
		if (detector.intersects(ballPathBottomLeft, brick.getRightLineSegment())) {
			count++;
		}
		if (detector.intersects(ballPathCenter, brick.getRightLineSegment())) {
			count++;
		}
		if (detector.intersects(ballPathTopLeft, brick.getLeftLineSegment())) {
			count++;
		}
		if (detector.intersects(ballPathBottomRight, brick.getLeftLineSegment())) {
			count++;
		}
		if (detector.intersects(ballPathTopRight, brick.getLeftLineSegment())) {
			count++;
		}
		if (detector.intersects(ballPathBottomLeft, brick.getLeftLineSegment())) {
			count++;
		}
		if (detector.intersects(ballPathCenter, brick.getLeftLineSegment())) {
			count++;
		}
		return count;
	}

	public Rectangle getBat() {
		return bat;
	}

	public LinkedList<Ball> getBalls() {
		return balls;
	}

	public LinkedList<Brick> getBricks() {
		return this.bricks;
	}

	public LinkedList<Packet> getPackets() {
		return packets;
	}

	public int getLevelNum() {
		return levelNum;
	}

}

class BallCollisionResult {
	Ball adjustedBall;
}

class BallHitBrickCollisionResult {
	Ball adjustedBall;
	Brick adjustedBrick;
}
