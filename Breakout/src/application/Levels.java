package application;

import gameComponenets.GameBoardModel;
import gameComponenets.JavaCollisionDetector;

public class Levels {
	JavaCollisionDetector jcd = new JavaCollisionDetector();
	public GameBoardModel level1= new GameBoardModel(
			500,// Screen Width
			500,// Screen Height
			50,// Brick Height
			30,// Horizontal Brick Gap
			30,// Vertical Brick Gap
			100,// Gap above bricks
			2,// # of Columns
			2,// # of Rows
			100,// Bat Width
			20,// Bat Height
			8,// Bat Speed
			1, //Ball Speed
			jcd);


	GameBoardModel level2= new GameBoardModel(
			500,// Screen Width
			500,// Screen Height
			50,// Brick Height
			30,// Horizontal Brick Gap
			30,// Vertical Brick Gap
			100,// Gap above bricks
			4,// # of Columns
			4,// # of Rows
			100,// Bat Width
			20,// Bat Height
			8,// Bat Speed
			1, //Ball Speed
			jcd);
	GameBoardModel level3= new GameBoardModel(
			500,// Screen Width
			500,// Screen Height
			40,// Brick Height
			10,// Horizontal Brick Gap
			3,// Vertical Brick Gap
			30,// Gap above bricks
			10,// # of Columns
			2,// # of Rows
			100,// Bat Width
			20,// Bat Height
			8,// Bat Speed
			1, //Ball Speed
			jcd);
}
