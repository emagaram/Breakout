package application;

import java.util.LinkedList;

import gameComponents.GameBoardModel;
import gameComponents.JavaCollisionDetector;
import gameComponents.RectangleType;

public class Levels {
	public Levels(){
		int brickWidth = 35;
		int brickHeight=35;
		level1.addBrick(brickWidth, brickHeight, 50, 130, RectangleType.Twohit2);
		level1.addBrick(brickWidth, brickHeight, 120, 130, RectangleType.Twohit2);
		level1.addBrick(brickWidth, brickHeight, 190, 130, RectangleType.Twohit2);
		level1.addBrick(brickWidth, brickHeight, 260, 130, RectangleType.Twohit2);
		level1.addBrick(brickWidth, brickHeight, 330, 130, RectangleType.Twohit2);
		level1.addBrick(brickWidth, brickHeight, 400, 130, RectangleType.Twohit2);

		level1.addBrick(brickWidth, brickHeight, 260, 230, RectangleType.Threehit3);
		level1.addBrick(brickWidth, brickHeight, 190, 230, RectangleType.Threehit3);
		level1.addBrick(brickWidth, brickHeight, 120, 230, RectangleType.Threehit3);
		level1.addBrick(brickWidth, brickHeight, 330, 230, RectangleType.Threehit3);

		level1.addBrick(brickWidth, brickHeight, 190, 330, RectangleType.Twohit2);
		level1.addBrick(brickWidth, brickHeight, 260, 330, RectangleType.Twohit2);

		level1.addBrick(brickWidth, brickHeight, 225, 430, RectangleType.ExplosiveBrick);

		levels.add(level1);
		levels.add(level2);
		levels.add(level3);
	}
	LinkedList<GameBoardModel> levels = new LinkedList<GameBoardModel>();

	public GameBoardModel findLevel(int levelNum){
		for(GameBoardModel gbm: this.levels){
			if(gbm.getLevelNum()==levelNum){
				return gbm;
			}
		}
		return null;
	}
	JavaCollisionDetector jcd = new JavaCollisionDetector();
	GameBoardModel level1 = new GameBoardModel(1,
			0, // Brick Height
			0, // Horizontal Brick Gap
			0, // Vertical Brick Gap
			0, // Gap above bricks
			0, // # of Columns
			0, // # of Rows
			100, // Bat Width
			20, // Bat Height
			8, // Bat Speed
			2, // Ball Speed
			jcd);

	GameBoardModel level2 = new GameBoardModel(2,
			50, // Brick Height
			30, // Horizontal Brick Gap
			30, // Vertical Brick Gap
			100, // Gap above bricks
			4, // # of Columns
			4, // # of Rows
			100, // Bat Width
			20, // Bat Height
			8, // Bat Speed
			1, // Ball Speed
			jcd);
	GameBoardModel level3 = new GameBoardModel(3,
			80, // Brick Height
			40, // Horizontal Brick Gap
			40, // Vertical Brick Gap
			70, // Gap above bricks
			6, // # of Columns
			4, // # of Rows
			100, // Bat Width
			20, // Bat Height
			8, // Bat Speed
			1, // Ball Speed
			jcd);
}
