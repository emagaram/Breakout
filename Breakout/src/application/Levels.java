package application;

import gameComponents.GameBoardModel;
import gameComponents.JavaCollisionDetector;

public class Levels {
	JavaCollisionDetector jcd = new JavaCollisionDetector();
	GameBoardModel level1 = new GameBoardModel(1,
			500, // Screen Width
			500, // Screen Height
			50, // Brick Height
			30, // Horizontal Brick Gap
			30, // Vertical Brick Gap
			100, // Gap above bricks
			2, // # of Columns
			2, // # of Rows
			100, // Bat Width
			20, // Bat Height
			8, // Bat Speed
			1, // Ball Speed
			jcd);

	GameBoardModel level2 = new GameBoardModel(2,
			500, // Screen Width
			500, // Screen Height
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
			500, // Screen Width
			500, // Screen Height
			80, // Brick Height
			30, // Horizontal Brick Gap
			40, // Vertical Brick Gap
			70, // Gap above bricks
			7, // # of Columns
			4, // # of Rows
			100, // Bat Width
			20, // Bat Height
			8, // Bat Speed
			4, // Ball Speed
			jcd);
}
