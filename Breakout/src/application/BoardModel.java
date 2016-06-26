package application;

import java.util.LinkedList;

public class BoardModel {
	private final int WIDTH;
	private final int HEIGHT;
	private final int BAT_WIDTH = 40;
	private final int BAT_HEIGHT = 10;
	private final int BAT_SPEED = 10;
	private Rectangle bat;
	private Ball ball;
	private LinkedList<Rectangle> bricks = new LinkedList<Rectangle>();

	public BoardModel(int width, int height, int brickColumns, int brickRows) {
		this.WIDTH = width;
		this.HEIGHT = height;
		int brickRowHeight = 15;
		int gap = 2;
		int columnWidth = WIDTH / brickColumns;
		int brickWidth = columnWidth - gap;
		int brickHeight = brickRowHeight - gap;
		for (int column = 0; column < brickColumns; column++) {
			for (int row = 0; row < brickRows; row++) {
				Rectangle r =new Rectangle(new Coordinate(column * columnWidth, row * brickRowHeight), brickWidth,
						brickHeight);
				bricks.add(r);
			}
		}
		Coordinate batUL = new Coordinate((WIDTH - BAT_WIDTH) / 2, (HEIGHT - BAT_HEIGHT));
		bat = new Rectangle(batUL, BAT_WIDTH, BAT_HEIGHT);
		ball = new Ball(new Coordinate(WIDTH / 2, HEIGHT / 2), 10, 10, 5, 2);
	}

	public void movePaddleLeft() {
		if ((bat.getTopLeft().getX() - BAT_SPEED) >= 0) {
			bat = bat.createMove(-BAT_SPEED, 0);
		}
	}

	public void movePaddleRight() {
		if (bat.getBottomRight().getX() + BAT_SPEED <= WIDTH) {
			bat = bat.createMove(BAT_SPEED, 0);
		}
	}

	public void updateBall() {
		Ball tempBall = ball.getMove();
		boolean shouldFlipX = false;
		boolean shouldFlipY = false;

		shouldFlipX = tempBall.getTopLeft().getX() <= 0 || tempBall.getBottomRight().getX() >= WIDTH;
		shouldFlipY = tempBall.getBottomRight().getY() <= 0 || tempBall.getTopLeft().getY() >= HEIGHT;
		if (shouldFlipX == true && shouldFlipY == true) {
			ball = tempBall.flipXDirection().flipYDirection();
		} else if (shouldFlipX == true && shouldFlipY == false) {
			ball = tempBall.flipXDirection();
		} else if (shouldFlipX == false && shouldFlipY == true) {
			ball = tempBall.flipYDirection();
		} else {
			ball = tempBall;
		}
	}

	public Rectangle getBat() {
		return bat;
	}

	public Rectangle getBall() {
		return ball;
	}

	public void update() {
		updateBall();
	}

	public int getWidth() {
		return WIDTH;
	}

	public int getHeight() {
		return HEIGHT;
	}

	public LinkedList<Rectangle> getBricks() {
		return new LinkedList<Rectangle>(this.bricks);
	}
}
