package application;

import gameComponenets.Coordinate;
import gameComponenets.GameBoardModel;
import gameComponenets.Button;
import gameComponenets.Variables;

public class LevelsScreenModel {
	private int numButtonRows;
	private int numButtonColumns;
	private GameBoardModel selectedLevel;
	private int buttonGapH;
	private int buttonGapV;
	private int brickRowHeight;
	private int brickColumnWidth;
	private Button[][] arrayButtons;

	public LevelsScreenModel(int rows, int columns, int buttonSize) {
		this.numButtonRows = rows;
		this.numButtonColumns = columns;
		int count = 1;

		buttonGapH = (Variables.getWIDTH() - (buttonSize * numButtonColumns)) / (numButtonColumns + 1);
		buttonGapV = (Variables.getHEIGHT() - (buttonSize * numButtonRows)) / (numButtonRows + 1);

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
								System.out.println("Directing you to: " + finalCount);
							}

						});
				count += 1;
				arrayButtons[column][row] = b;
			}
		}
	}

	public Button[][] getButtons() {
		return arrayButtons;
	}
}