package application;

public class Rectangle {
	private static int count;
	private int id;
	public int getId() {
		return id;
	}
	private Coordinate topLeft;
	private Coordinate bottomRight;
	public Coordinate getTopLeft() {
		return topLeft;
	}
	public Coordinate getBottomRight() {
		return bottomRight;
	}
	public double getWidth(){
		return Math.abs(topLeft.getX()-bottomRight.getX());
	}
	public double getHeight(){
		return Math.abs(topLeft.getY()-bottomRight.getY());
	}

	//Main constructor
	protected Rectangle(Coordinate topLeft, Coordinate bottomRight,int id){
		this.id=id;
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
	}

	//Alternative to the main constructor, preserves id
	protected Rectangle(Coordinate topLeft,double width,double height, int id){
		this(topLeft,topLeft.getMoveDelta(width, height),id);
	}

	//For creating brand new rectangles
	public Rectangle(Coordinate topLeft, Coordinate bottomRight){
		this(topLeft,bottomRight,count++);
	}

	//For creating brand new rectangles as well, calls the constructor above
	public Rectangle(Coordinate topLeft,double width,double height){
		this(topLeft,topLeft.getMoveDelta(width, height));
	}



	public Rectangle createMove(double dx,double dy){
		return new Rectangle(topLeft.getMoveDelta(dx, dy),bottomRight.getMoveDelta(dx, dy),this.id);
	}
}
