package svb.nxt.robot.game;

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import svb.nxt.robot.MainGame;
import svb.nxt.robot.bt.BTControls;
import svb.nxt.robot.logic.CommandPerformer;

/** 
 * class for robot type: Tribot
 * @author svab
 *
 */
public class GameMoveAccelerometer extends GameTemplate {

	public boolean forward = false;
	public boolean backward = false;

	public boolean rotateLeft = false;
	public boolean rotateRight = false;

	public boolean openClaw = false;
	public boolean closeCraw = false;

	public int speed = 0;
	public int leftSpeed = 0;
	public int rightSpeed = 0;
	public int clawSpeed = 0;
	
	public boolean power = false;

	@Override
	public void setMain(CommandPerformer commandPerformer) {
		this.mainGame = (MainGame) commandPerformer;
	}

	@Override
	public void readInstructions(int command, byte[] parameter) {
		switch (parameter[2]) {

		// BOTH MOTORS B, C FORWARD
		case BTControls.GO_FORWARD_B_C_START:
			forward = true;
			speed = parameter[3] * 10;
			break;
		case BTControls.GO_FORWARD_B_C_STOP:
			forward = false;
			speed = parameter[3] * 10;
			break;

		// BOTH MOTORS B, C BACKWARD
		case BTControls.GO_BACKWARD_B_C_START:
			backward = true;
			speed = parameter[3] * 10;
			break;
		case BTControls.GO_BACKWARD_B_C_STOP:
			backward = false;
			speed = parameter[3] * 10;
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

		// A MOTOR CLAWS
		case BTControls.MOTOR_A_FORWARD_START:
			openClaw = true;
			clawSpeed = parameter[3] * 10;
			break;
		case BTControls.MOTOR_A_FORWARD_STOP:
			openClaw = false;
			clawSpeed = parameter[3] * 10;
			break;
		case BTControls.MOTOR_A_BACKWARD_START:
			closeCraw = true;
			clawSpeed = parameter[3] * 10;
			break;
		case BTControls.MOTOR_A_BACKWARD_STOP:
			closeCraw = false;
			clawSpeed = parameter[3] * 10;
			break;
			
		case BTControls.POWER:
			power = !power;
			break;
		}
	}

	@Override
	public void performInstructions() {
		if(power){
		
			boolean leftForward = true;
			boolean rightForward = true;
			int leftMotor = 0;
			int rightMotor = 0;
	
			if (forward) {
				leftForward = true;
				rightForward = true;
				leftMotor = speed;
				rightMotor = speed;
				if (rotateRight) {
					rightMotor += rightSpeed;
				}
				if (rotateLeft) {
					leftMotor += leftSpeed;
				}
			} else if (backward) {
				leftForward = false;
				rightForward = false;
				leftMotor = speed;
				rightMotor = speed;
				if (rotateRight) {
					leftMotor += rightSpeed;
				}
				if (rotateLeft) {
					rightMotor += leftSpeed;
				}
			} else if (rotateLeft) {
				leftForward = true;
				leftMotor = leftSpeed;
				rightForward = false;
				rightMotor = leftSpeed;
			} else if (rotateRight) {
				rightForward = true;
				rightMotor = rightSpeed;
				leftForward = false;
				leftMotor = rightSpeed;
			}
	
			if (rotateLeft && rotateRight) {
				if (forward) {
					leftForward = false;
					rightForward = false;
					leftMotor = speed;
					rightMotor = speed;
				} else if (backward) {
					leftForward = false;
					rightForward = false;
					leftMotor = speed;
					rightMotor = speed;
				}
			}
	
			if (forward && backward) {
				leftMotor = 0;
				rightMotor = 0;
			}
	
			if (((MainGame) mainGame).
					getRobotType() == BTControls.ROBOT_TYPE_TRIBOT) {
				robotTypeLimitationsTribot();
			} else {
				if (openClaw) {
					Motor.A.backward();
					Motor.A.setSpeed(clawSpeed);
				} else if (closeCraw) {
					Motor.A.forward();
					Motor.A.setSpeed(clawSpeed);
				} else {
					Motor.A.setSpeed(0);
				}
			}
	
			LCD.drawString("A pos: " + Motor.A.getPosition(), 2, 3);
			LCD.refresh();
	
			if (leftForward) {
				Motor.B.forward();
			} else {
				Motor.B.backward();
			}
			if (leftMotor > 0) {
				Motor.B.setSpeed(leftMotor);
			} else {
				Motor.B.setSpeed(0);
			}
	
			if (rightForward) {
				Motor.C.forward();
			} else {
				Motor.C.backward();
			}
			if (rightMotor > 0) {
				Motor.C.setSpeed(rightMotor);
			} else {
				Motor.C.setSpeed(0);
			}
		}else{
			Motor.A.setSpeed(0);
			Motor.B.setSpeed(0);
			Motor.C.setSpeed(0);
		}
	}

	public void robotTypeLimitationsTribot() {
		if (openClaw && (Motor.A.getPosition() > 0)) {
			Motor.A.backward();
			Motor.A.setSpeed(clawSpeed);
		} else if (closeCraw && (Motor.A.getPosition() < 50)) {
			Motor.A.forward();			
			Motor.A.setSpeed(clawSpeed);
		} else {
			Motor.A.setSpeed(0);
		}
	}

	@Override
	public void onDestroy() {
	}

	@Override
	public void buttonPressed(int btnID) {
		// TODO Auto-generated method stub
		
	}

}