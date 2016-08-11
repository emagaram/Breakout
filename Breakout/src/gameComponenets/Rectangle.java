package gameComponenets;

public class Rectangle {
	protected static int count;
	private RectangleType type;
	private int id;
	private Coordinate topLeft;
	private Coordinate bottomRight;

	// Main constructor
	protected Rectangle(Coordinate topLeft, Coordinate bottomRight, int id, RectangleType type) {
		this.id = id;
		this.type = type;
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
	}

	// Alternative to the main constructor, preserves id
	protected Rectangle(Coordinate topLeft, double width, double height, int id, RectangleType type) {
		this(topLeft, topLeft.getMoveDelta(width, height), id, type);
	}

	// For creating brand new rectangles
	public Rectangle(Coordinate topLeft, Coordinate bottomRight, RectangleType type) {
		this(topLeft, bottomRight, count++, type);
	}

	// For creating brand new rectangles as well, calls the constructor above
	public Rectangle(Coordinate topLeft, double width, double height, RectangleType type) {
		this(topLeft, topLeft.getMoveDelta(width, height), type);
	}

	public Rectangle createMove(double dx, double dy, RectangleType type) {
		return new Rectangle(topLeft.getMoveDelta(dx, dy), bottomRight.getMoveDelta(dx, dy), this.id, type);
	}

	public Coordinate getTopLeftCoordinate() {
		return topLeft;
	}

	public Coordinate getBottomRightCoordinate() {
		return bottomRight;
	}

	public Coordinate getTopRightCoordinate() {
		return new Coordinate(topLeft.getX() + this.getWidth(), topLeft.getY());
	}

	public Coordinate getBottomLeftCoordinate() {
		return new Coordinate(topLeft.getX(), topLeft.getY() + this.getHeight());
	}

	public Coordinate getCenterCoordinate() {
		return new Coordinate(this.getTopLeftCoordinate().getX() + (this.getWidth() / 2),
				this.getTopLeftCoordinate().getY() + (this.getHeight() / 2));
	}

	public LineSegment getTopLineSegment() {
		return new LineSegment(this.getTopLeftCoordinate(), this.getTopRightCoordinate());
	}

	public LineSegment getRightLineSegment() {
		return new LineSegment(this.getTopRightCoordinate(), this.getBottomRightCoordinate());
	}

	public LineSegment getBottomLineSegment() {
		return new LineSegment(this.getBottomLeftCoordinate(), this.getBottomRightCoordinate());
	}

	public LineSegment getLeftLineSegment() {
		return new LineSegment(this.getTopLeftCoordinate(), this.getBottomLeftCoordinate());
	}

	public double getWidth() {
		return Math.abs(topLeft.getX() - bottomRight.getX());
	}

	public double getHeight() {
		return Math.abs(topLeft.getY() - bottomRight.getY());
	}

	public double getPercentCoordinate(double percentage) {
		return this.getTopLeftCoordinate().getX() + ((percentage / 100) * this.getWidth());
	}

	public int getId() {
		return id;
	}

	public RectangleType getType() {
		return type;
	}

}
