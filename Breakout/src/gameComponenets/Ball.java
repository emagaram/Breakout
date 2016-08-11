package gameComponenets;

public class Ball extends Rectangle {
	private double speed;
	private double angleOfMovement;

	// For creating a brand new ball
	public Ball(Coordinate topLeft, double width, double height, double speed, double angleOfMovement,
			RectangleType type) {
		super(topLeft, width, height, type);
		this.speed = speed;
		this.angleOfMovement = angleOfMovement;
	}

	private Ball(Coordinate topLeft, double width, double height, double speed, double angleOfMovement, int id,
			RectangleType type) {
		super(topLeft, width, height, id, type);
		this.speed = speed;
		this.angleOfMovement = angleOfMovement;
	}

	public Ball getMove() {
		return new Ball(this.getTopLeftCoordinate().getMoveVelocity(angleOfMovement, speed), this.getWidth(),
				this.getHeight(), speed, angleOfMovement, this.getId(), this.getType());
	}

	public Ball flipXDirection() {
		double cos = Math.cos(angleOfMovement);
		double sin = Math.sin(angleOfMovement);
		double newAngle = Math.atan2(sin, -cos);
		return new Ball(this.getTopLeftCoordinate(), this.getWidth(), this.getHeight(), this.speed, newAngle,
				this.getId(), this.getType());
	}

	public Ball flipYDirection() {
		double cos = Math.cos(angleOfMovement);
		double sin = Math.sin(angleOfMovement);
		double newAngle = Math.atan2(-sin, cos);
		return new Ball(this.getTopLeftCoordinate(), this.getWidth(), this.getHeight(), this.speed, newAngle,
				this.getId(), this.getType());
	}

	public Ball changeAngleDegrees(double changeAmount) {
		return new Ball(this.getTopLeftCoordinate(), this.getWidth(), this.getHeight(), this.speed,
				(this.angleOfMovement + Math.toRadians(changeAmount)), this.getId(), this.getType());
	}

	public Ball setPosition(double setX, double setY) {
		return new Ball(new Coordinate(setX, setY), this.getWidth(), this.getHeight(), this.speed, this.angleOfMovement,
				this.getId(), this.getType());
	}

	public Ball setAngleInDegrees(double changeAmount) {
		return new Ball(this.getTopLeftCoordinate(), this.getWidth(), this.getHeight(), this.speed,
				(Math.toRadians(changeAmount)), this.getId(), this.getType());
	}

	public double getAngleOfMovement() {
		return angleOfMovement;
	}

	public double getAngleInDegrees() {
		return Math.toDegrees(angleOfMovement);
	}

	public double getSpeed() {
		return speed;
	}

	public double calculatePercentageOffsetWith(Rectangle r) {
		// Formula is (centerCoordinate of ball / (1/2) r's width)*50 =
		// percentage offset from -50% to +50%
		double offset = this.getCenterCoordinate().getX() - r.getCenterCoordinate().getX();
		return (offset / (0.5 * r.getWidth())) * 50;
	}

}