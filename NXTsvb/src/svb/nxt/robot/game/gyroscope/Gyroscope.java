package svb.nxt.robot.game.gyroscope;

import lejos.nxt.*;
//import lejos.nxt.addon.GyroSensor;

/**
 * 
 * @author Pawel Jankowski
 */
public class Gyroscope {

	private SensorPort sensorPort;
	private double angle = 0;
	private double angleChange = 0;
	private double angularVelocity = 0;
		
	private double offset = (double) 608.0875;//608.832285714;
	private long lastCall = 0;
	private GyroSensor sensor;

	public Gyroscope(SensorPort s) {
		this.sensorPort = s;
		this.sensor = new GyroSensor(sensorPort);
		//sensor.recalibrateOffset();
//		this.calibrate();
		sensor.setOffset((int) offset);
	}

	public GyroscopeData getData() {

		
		long now = System.currentTimeMillis();
		long difference = now - lastCall;

		this.angularVelocity = (double) this.sensorPort.readRawValue()
				- this.offset;

		if (difference != now) {
			angleChange = this.angularVelocity * ((double) difference / 1000.0);
			if((angleChange > 0.0009)||(angleChange < -0.0009)){
				this.angle += angleChange;
			}
		}

		GyroscopeData gyroscopeData = new GyroscopeData((int) this.angle,
				(int) this.angularVelocity, this.offset, this.angleChange,
				this.sensorPort.readRawValue());

		lastCall = now;

		return gyroscopeData;
	}
	
	/*
	public void getOffset(double rawVelocity){
		//offset = intersect + slope * voltage
		// slope 4.891
		//Battery.getVoltage()
	
		//intersect = gyro reading – 4.891 * voltage
		double intersect = rawVelocity - (double) (4.891 * Battery.getVoltage());
		double offset = intersect + slope * voltage
	}
	*/

	public void calibrate() {
		this.offset = 0;
		double offsetTotal = 0;

		for (int i = 0; i < 2000; i++) {
			offsetTotal += (double) this.sensorPort.readRawValue();			
		}
		this.offset = offsetTotal / 2000;

		try {
			Thread.sleep(12000);
		} catch (Exception e) {
			//nope
		}
	}

	public void resetAngle() {
		this.angle = 0;
	}
	
    public void onDestroy(){
    	sensor = null;
    	sensorPort = null;
    	try {
			Thread.sleep(1000);
		} catch (Exception e) {
			//nope
		}
    }
	
	
}
