package application;
public class Ball extends Rectangle {
	private double speed;
	private double angleOfMovement;
	//For creating a brand new ball
	public Ball(Coordinate topLeft, double width, double height, double speed, double angleOfMovement) {
		super(topLeft, width, height);
		this.speed = speed;
		this.angleOfMovement = angleOfMovement;
	}
	private Ball(Coordinate topLeft, double width, double height, double speed, double angleOfMovement, int id) {
		super(topLeft, width, height,id);
		this.speed = speed;
		this.angleOfMovement = angleOfMovement;
	}

	public Ball getMove() {
		return new Ball(this.getTopLeftCoordinate().getMoveVelocity(angleOfMovement, speed), this.getWidth(), this.getHeight(),
		speed, angleOfMovement,this.getId());
	}
	public Ball flipXDirection() {
		double cos = Math.cos(angleOfMovement);
		double sin = Math.sin(angleOfMovement);
		double newAngle = Math.atan2(sin, -cos);
		return new Ball(this.getTopLeftCoordinate(), this.getWidth(), this.getHeight(), this.speed, newAngle, this.getId());
	}

	public Ball flipYDirection() {
		double cos = Math.cos(angleOfMovement);
		double sin = Math.sin(angleOfMovement);
		double newAngle = Math.atan2(-sin, cos);
		return new Ball(this.getTopLeftCoordinate(), this.getWidth(), this.getHeight(), this.speed, newAngle, this.getId());
	}
	public static double randomAngle(double rangeInDegrees){
		double rangeInRadians = Math.toRadians(rangeInDegrees);
		return (Math.random()*rangeInRadians)-(rangeInRadians/2);
	}
	public Ball changeAngleRandom(double range){
		return new Ball(this.getTopLeftCoordinate(), this.getWidth(),this.getHeight(),this.speed,this.angleOfMovement+Math.toRadians(randomAngle(range)), this.getId());
	}
	public Ball changeAngleDegrees(double changeAmount){
		return new Ball(this.getTopLeftCoordinate(), this.getWidth(),this.getHeight(),this.speed,(this.angleOfMovement+Math.toRadians(changeAmount)), this.getId());
	}
	public Ball setAngleInDegrees(double changeAmount){
		return new Ball(this.getTopLeftCoordinate(), this.getWidth(),this.getHeight(),this.speed,(Math.toRadians(changeAmount)), this.getId());
	}
	public double getAngleOfMovement() {
		return angleOfMovement;
	}
	public double getAngleInDegrees(){
		return Math.toDegrees(angleOfMovement);
	}
	public double getSpeed() {
		return speed;
	}
	public double calculatePercentageOffsetWith(Rectangle r){
		//Formula is (centerCoordinate of ball / (1/2) r's width)*50 = percentage offset from -50% to +50%
		double offset = this.getCenterCoordinate().getX()-r.getCenterCoordinate().getX();
		return (offset/(0.5*r.getWidth()))*50;
	}


}
