package application;

import gameComponents.Button;
import gameComponents.Coordinate;

public class LevelsScreenModel {
	private int numButtonRows;
	private int numButtonColumns;
	private int selectedLevelNum;
	private int buttonGapH;
	private int buttonGapV;
	private int brickRowHeight;
	private int brickColumnWidth;
	private int highScore;
	private Button[][] arrayButtons;

	public LevelsScreenModel(int rows, int columns, int buttonSize, int hs) {
		this.highScore=hs;
		this.numButtonRows = rows;
		this.numButtonColumns = columns;
		int count = 1;

		buttonGapH = (TheController.getBoardWidth() - (buttonSize * numButtonColumns)) / (numButtonColumns + 1);
		buttonGapV = (TheController.getBoardHeight() - (buttonSize * numButtonRows)) / (numButtonRows + 1);

		brickRowHeight = buttonSize + buttonGapV;
		brickColumnWidth = buttonSize + buttonGapH;

		arrayButtons = new Button[numButtonColumns][numButtonRows];

		// Set buttons horizontally
		for (int column = 0; column < numButtonColumns; column++) {
			for (int row = 0; row < numButtonRows; row++) {
				int finalCount = count;
				Button b = new Button(
						new Coordinate(row * brickColumnWidth + (buttonGapH + 5), column * brickRowHeight + buttonGapH),
						buttonSize, buttonSize, new Runnable() {
							@Override
							public void run() {
								selectedLevelNum = finalCount;
								System.out.println("You have selected: " + selectedLevelNum);
							}

						});
				count++;
				arrayButtons[column][row] = b;
			}
		}
	}

	public int getHighScore() {
		return highScore;
	}

	public Button[][] getButtons() {
		return arrayButtons;
	}

	public int getSelectedLevelNum() {
		return selectedLevelNum;
	}

}
