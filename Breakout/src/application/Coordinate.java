package application;

public class Coordinate {
	private int x;
	private int y;

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Coordinate(int x, int y){
		this.x = x;
		this.y = y;
	}

	public Coordinate CreateMove(int dx,int dy) {
		return new Coordinate(this.x+dx,this.y+dy);
	}

}