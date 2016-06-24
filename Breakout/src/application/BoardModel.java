package application;

public class BoardModel {
	private final int WIDTH;
	private final int HEIGHT;
	private final int BAT_WIDTH = 20;
	private final int BAT_HEIGHT = 10;
	private final int BAT_SPEED = 10;

	Rectangle bat;
	public BoardModel(){
		this.WIDTH = 500;
		this.HEIGHT = 500;
		Coordinate batUL = new Coordinate((WIDTH-BAT_WIDTH)/2, (HEIGHT-BAT_HEIGHT));
		bat = new Rectangle(batUL, BAT_WIDTH, BAT_HEIGHT);
	}
	public void movePaddleLeft(){
		if((bat.getTopLeft().getX()-BAT_SPEED)>=0){
			bat = bat.CreateMove(-BAT_SPEED, 0);
		}
		else{
		}
	}
	public void movePaddleRight(){
		if(bat.getBottomRight().getX()+BAT_SPEED<=WIDTH){
		bat = bat.CreateMove(BAT_SPEED, 0);
		}
	}
	public Rectangle getBat(){
		return bat;
	}

	public int getWidth() {
		return WIDTH;
	}
	public int getHeight() {
		return HEIGHT;
	}
}