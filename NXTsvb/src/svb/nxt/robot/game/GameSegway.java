package svb.nxt.robot.game;

import lejos.nxt.Battery;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.GyroSensor;
//import lejos.robotics.Gyroscope;
import svb.nxt.robot.MainGame;
import svb.nxt.robot.bt.BTControls;
import svb.nxt.robot.game.gyroscope.Gyroscope;
import svb.nxt.robot.game.gyroscope.GyroscopeData;
import svb.nxt.robot.logic.CommandPerformer;

/** 
 * class for robot type: segway
 * @author svab
 *
 */
public class GameSegway extends GameTemplate{

	private NXTRegulatedMotor leftMotor, rightMotor;
	private Gyroscope gyroscope;
	private GyroSensor g;
	
	private boolean isAlive = false;
	private boolean firstTime = true;	
	private boolean isStand = false;
	private int standStatus = 0;
	
	float mySpeed = 0;
	
	private static final float WHEEL_DIAMETER = 56;
	
	// moving
	public boolean forward = false;
	public boolean backward = false;

	public boolean rotateLeft = false;
	public boolean rotateRight = false;

	public int mSpeed = 0;
	public int leftSpeed = 0;
	public int rightSpeed = 0;	
	
	@Override
	public void setMain(CommandPerformer commandPerformer) {
		this.mainGame = (MainGame) commandPerformer;		
		initMotors();	
		this.setShowText(false);
		
		LCD.clear();
		LCD.drawString("STARTING SEGWAY BALANCING ", 0, 0);
		LCD.refresh();
	}

	private void initMotors() {
		leftMotor = Motor.B; 
		rightMotor = Motor.A;		
		
		gyroscope = new Gyroscope(SensorPort.S1);
//		g = new GyroSensor(SensorPort.S1);
//		g.recalibrateOffset();
//		g.recalibrateOffsetAlt();
		isAlive = true;
	}	
	
	@Override
	public void readInstructions(int command, byte[] parameter) {
		
		switch (parameter[2]) {

			// BOTH MOTORS B, C FORWARD
			case BTControls.GO_FORWARD_B_C_START:
				forward = true;
				mSpeed = parameter[3] * 10;
				break;
			case BTControls.GO_FORWARD_B_C_STOP:
				forward = false;
				mSpeed = parameter[3] * 10;
				break;
	
			// BOTH MOTORS B, C BACKWARD
			case BTControls.GO_BACKWARD_B_C_START:
				backward = true;
				mSpeed = parameter[3] * 10;
				break;
			case BTControls.GO_BACKWARD_B_C_STOP:
				backward = false;
				mSpeed = parameter[3] * 10;
				break;
	
			// BOTH MOTORS B, C LEFT ROTATION
			case BTControls.TURN_LEFT_START:
				rotateLeft = true;
				leftSpeed = parameter[3] * 10;
				break;
			case BTControls.TURN_LEFT_STOP:
				rotateLeft = false;
				leftSpeed = parameter[3] * 10;
				break;
	
			// BOTH MOTORS B, C RIGHT ROTATION
			case BTControls.TURN_RIGHT_START:
				rotateRight = true;
				rightSpeed = parameter[3] * 10;
				break;
			case BTControls.TURN_RIGHT_STOP:
				rotateRight = false;
				rightSpeed = parameter[3] * 10;
				break;		
		}
	}

	@Override
	public void performInstructions() {
		if(firstTime){
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			firstTime = !firstTime;
		}
				
		log1();
		
		//TODO perform readInstructions
	}


	@Override
	public void onDestroy() {		
		isAlive = false;
		leftMotor.setSpeed(0);	
		rightMotor.setSpeed(0);
		gyroscope.onDestroy();
		g = null;
		
	}

	@Override
	public int getRefreshInterval() {
		//return GameTemplate.SEGWAY_REFRESH_INTERVAL;
		//return GameTemplate.DEFAULT_REFRESH_INTERVAL;
		return 2;
	}

	private void log1(){
		if(isAlive && gyroscope != null){
			GyroscopeData gd = gyroscope.getData();
			
			int angle = gd.getAngle();
			int angleVeloci = gd.getAngularVelocity();
			double off = gd.getOffset();
			double angleCh = gd.getAngleChange();									
					
			LCD.clear();
			LCD.drawString("ang: " + angle , 0, 0);
			LCD.drawString("angV: " + angleVeloci, 0, 1);			
			LCD.drawString("off: " + off, 0, 2);
			LCD.drawString("angCH: " + angleCh	 , 0, 3);
			//LCD.drawString("voltage: " + Battery.getVoltage(), 0, 4);
			//if (!isStand) LCD.drawString("stand: " + standStatus	 , 0, 4);					
			LCD.refresh();		

			
			if (standStatus > -1 && angle >=82) {				
				standStatus++;				
			}
			if (standStatus >= 5){
				standStatus = -1;
				isStand = true;
			}
			balance1(gd);
		}
	}
		
	private void balance1(GyroscopeData gd){
		if(isStand){
			
			if (gd.getAngularVelocity() > 0){					
				
				leftMotor.forward();
				rightMotor.forward();
			}else{
				leftMotor.backward();
				rightMotor.backward();
			}
			double v = gd.getAngularVelocity();
//			leftMotor.setAcceleration((int)(v*v*v*v/20));			
//			rightMotor.setAcceleration((int)(v*v*v*v/20));
			mySpeed += v/10;
			leftMotor.setAcceleration((int)mySpeed);
			rightMotor.setAcceleration((int)mySpeed);			
		}else{
			leftMotor.stop(false);
			rightMotor.stop(false);
		}		
	}
	
	private void log2(){
		
		int val = g.readValue();
		float velo = g.getAngularVelocity();
		
		LCD.drawString("val: " + val, 0, 0);
		LCD.drawString("angV: " + velo, 0, 1);			
					
		LCD.refresh();
		
	}
	
	
	int steering = 0;
	int acceleration = 50;
	int speed = 0;
	boolean starting_balancing_task = true;
	float gn_dth_dt,gn_th,gn_y,gn_dy_dt,kp,ki,kd,mean_reading,gear_down_ratio,dt;
	
	private double balance2(double raw, double actSpeed){
		//MATH CONSTANTS
		  float radius = WHEEL_DIAMETER/1000;
		  float degtorad = (float)Math.PI/180;

		  //SETUP VARIABLES FOR CALCULATIONS
		  float u = 0;                    // Sensor Measurement (raw)
		  float th = 0,//Theta            // Angle of robot (degree)
		        dth_dt = 0;//dTheta/dt    // Angular velocity of robot (degree/sec)
		  float e = 0,//Error             // Sum of four states to be kept zero: th, dth_dt, y, dy_dt.
		        de_dt = 0,//dError/dt     // Change of above error
		        _edt = 0,//Integral Error // Accumulated error in time
		        e_prev = 0;//Previous Error/ Error found in previous loop cycle
		  float pid = 0;                  // SUM OF PID CALCULATION
		  float y = 0,//y                     // Measured Motor position (degrees)
		        dy_dt = 0,//dy/dt             // Measured motor velocity (degrees/sec)
			      v = 0,//velocity          // Desired motor velocity (degrees/sec)
			      y_ref = 0;//reference pos // Desired motor position (degrees)
		  int motorpower = 0,             // Power ultimately applied to motors
		      last_steering = 0,          // Steering value in previous cycle
		      straight = 0,               // Average motor position for synchronizing
		      d_pwr = 0;                  // Change in power required for synchronizing
		  int n_max = 7;            // Number of measurement used for floating motor speed average
		  int n = 0,n_comp = 0;           // Intermediate variables needed to compute measured motor speed
		  int[]encoder = new int[n_max];                 // Array containing last n_max motor positions
//		  memset(&encoder[0],0,sizeof(encoder));
//		  starting_balancing_task = false;// We're done configuring. Main task now resumes.
		  
		  
		//COMPUTE GYRO ANGULAR VELOCITY AND ESTIMATE ANGLE
		  	dth_dt = u/2 - mean_reading;
		  	mean_reading = (float) (mean_reading*0.999 + (0.001*(dth_dt+mean_reading)));
		  	th = th + dth_dt*dt;

		    //ADJUST REFERENCE POSITION ON SPEED AND ACCELERATION
		    if(v < speed*10){
		    v = v + acceleration*10*dt;}
		    else if(v > speed*10){
		    v = v - acceleration*10*dt;}
		    y_ref = y_ref + v*dt;

		  	//COMPUTE MOTOR ENCODER POSITION AND SPEED
		  	n++;if(n == n_max){n = 0;}
		  	encoder[n] = (int) (actSpeed + actSpeed + y_ref);
		  	n_comp = n+1;if(n_comp == n_max){n_comp = 0;}
		  	y = encoder[n]*degtorad*radius/gear_down_ratio;
		  	dy_dt = (encoder[n] - encoder[n_comp])/(dt*(n_max-1))*degtorad*radius/gear_down_ratio;

		  	//COMPUTE COMBINED ERROR AND PID VALUES
		  	e = gn_th * th + gn_dth_dt * dth_dt + gn_y * y + gn_dy_dt * dy_dt;
		  	de_dt = (e - e_prev)/dt;
		  	_edt = _edt + e*dt;
		  	e_prev = e;
		  	pid = (kp*e + ki*_edt + kd*de_dt)/radius*gear_down_ratio;

		  	//ADJUST MOTOR SPEED TO STEERING AND SYNCHING
//		    if(steering == 0){
//		        if(last_steering != 0){
//			        straight = nMotorEncoder[rightMotor] - nMotorEncoder[leftMotor];}
//				    d_pwr = (nMotorEncoder[rightMotor] - nMotorEncoder[leftMotor] - straight)/(radius*10/gear_down_ratio);}
//		    else{d_pwr = (int) (steering/(radius*10/gear_down_ratio));}
//		    last_steering = steering;

		  	//CONTROL MOTOR POWER AND STEERING
		  	motorpower = 	(int) pid;
//		    motor[motorB] = motorpower + d_pwr;
//		    motor[motorC] = motorpower - d_pwr;

		    //ERROR CHECKING OR SHUTDOWN
		    if(Math.abs(th)>60 || Math.abs(motorpower) > 2000){
		    //	StopAllTasks();
		    }

		    //WAIT THEN REPEAT
//		  	while(time1[T4] < dt*1000){
//		  	  wait1Msec(1);}
//		  	ClearTimer(T4);
		    return motorpower;
	} 
	
}
