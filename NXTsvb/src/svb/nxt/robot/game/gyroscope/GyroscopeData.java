package svb.nxt.robot.game.gyroscope;

/**
 * 
 * @author Pawel Jankowski
 */
public class GyroscopeData {

	private int angle;	
	private int angularVelocity;
	private double offset;
	private double angleCahange;
	private double raw;

	public GyroscopeData(int angle, int angularVelocity, double offset, double angleCh, double raw) {
		this.angle = angle;
		this.angularVelocity = angularVelocity;
		this.offset = offset;
		this.angleCahange = angleCh;
		this.raw = raw;
	}

	public int getAngle() {
		return angle;
	}

	public int getAngularVelocity() {
		return angularVelocity;
	}

	public double getOffset() {
		return offset;
	}
	
	public double getAngleChange() {
		return angleCahange;
	}

	public double getRaw() {
		return raw;
	}
	

}