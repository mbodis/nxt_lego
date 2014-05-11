package svb.nxt.robot.game;

import java.util.Random;

import lejos.nxt.Motor;
import svb.nxt.robot.MainGame;
import svb.nxt.robot.bt.BTControls;
import svb.nxt.robot.logic.CommandPerformer;

/** 
 * class for robot type: leJOS, Tribot
 * @author svab
 *
 */
public class GameMoveMotor extends GameTemplate{
	
	public boolean motor_A_forward = false;
	public boolean motor_A_backward = false;
	public int motor_A_speed = 0;
	public int motor_A_acc = 0;
	
	public boolean motor_B_forward = false;
	public boolean motor_B_backward = false;
	public int motor_B_speed = 0;
	public int motor_B_acc = 0;

	public boolean motor_C_forward = false;
	public boolean motor_C_backward = false;
	public int motor_C_speed = 0;
	public int motor_C_acc = 0;
			
	private int c_speed = 5;
	private int c_acc = 100;
	
	byte [][]arr;
	
	@Override
	public void setMain(CommandPerformer commandPerformer) {
		this.mainGame = (MainGame) commandPerformer;	
		// this.showText = false;
		arr = new byte[100][100];
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 100; j++) {
				Random r= new Random(255);				
				arr[i][j] = (byte) r.nextInt();
			}
		}
		System.out.print("test");
	}

	@Override
	public void readInstructions(int command, byte[] parameter) {
		
		switch (parameter[2]) {
				
			// A MOTOR CLAWS			        			        				        			
			case BTControls.MOTOR_A_FORWARD_START:
				motor_A_forward = true;
				break;
			case BTControls.MOTOR_A_FORWARD_STOP:
				motor_A_forward = false;
				break;	        				
			case BTControls.MOTOR_A_BACKWARD_START:
				motor_A_backward = true;
				break;
			case BTControls.MOTOR_A_BACKWARD_STOP:
				motor_A_backward = false;
				break;
				
				
			// B MOTOR 
			case BTControls.MOTOR_B_FORWARD_START:
				motor_B_forward = true;
				break;        				        			
			case BTControls.MOTOR_B_FORWARD_STOP:
				motor_B_forward = false;
				break;
			case BTControls.MOTOR_B_BACKWARD_START:
				motor_B_backward = true;
				break;        				        			
			case BTControls.MOTOR_B_BACKWARD_STOP:
				motor_B_backward = false;
				break;	
				
				
			// C MOTOR
			case BTControls.MOTOR_C_FORWARD_START:
				motor_C_forward = true;
				break;        				        				
			case BTControls.MOTOR_C_FORWARD_STOP:
				motor_C_forward = false;
				break;
			case BTControls.MOTOR_C_BACKWARD_START:
				motor_C_backward = true;
				break;        				        				
			case BTControls.MOTOR_C_BACKWARD_STOP:
				motor_C_backward = false;				
				break;
				
			case BTControls.MOTOR_SET_SPEED:
				motor_A_speed = parameter[3] * c_speed;
				motor_B_speed = parameter[3] * c_speed;
				motor_C_speed = parameter[3] * c_speed;
				break;
				
			case BTControls.MOTOR_SET_ACC:
				motor_A_acc = parameter[3] * c_acc;
				motor_B_acc = parameter[3] * c_acc;
				motor_C_acc = parameter[3] * c_acc;				
				break;
		}
		
	}

	@Override
	public void performInstructions() {

		if (((MainGame) mainGame).
				getRobotType() == BTControls.ROBOT_TYPE_TRIBOT) {
			robotTypeLimitationsTribot();
		} else {
			if (motor_A_forward) {
				Motor.A.forward();
				Motor.A.setAcceleration(motor_A_acc);
				Motor.A.setSpeed(motor_A_speed);
			} else if (motor_A_backward) {				
				Motor.A.backward();
				Motor.A.setAcceleration(motor_A_acc);
				Motor.A.setSpeed(motor_A_speed);
			} else {
				Motor.A.setSpeed(0);
				Motor.C.flt();
			}
		}

		if (motor_B_backward) {
			Motor.B.backward();
			Motor.B.setAcceleration(motor_B_acc);
			Motor.B.setSpeed(motor_B_speed);
		} else if (motor_B_forward) {
			Motor.B.forward();
			Motor.B.setAcceleration(motor_B_acc);
			Motor.B.setSpeed(motor_B_speed);
		} else {
			Motor.B.setSpeed(0);			
			Motor.C.flt();
		}

		if (motor_C_backward) {
			Motor.C.backward();
			Motor.C.setAcceleration(motor_C_acc);
			Motor.C.setSpeed(motor_C_speed);
		} else if (motor_C_forward) {
			Motor.C.forward();
			Motor.C.setAcceleration(motor_C_acc);
			Motor.C.setSpeed(motor_C_speed);
		} else {
			Motor.C.setSpeed(0);
			Motor.C.flt();
		}

		
	}	
	
	public void robotTypeLimitationsTribot() {
		if (motor_A_backward && (Motor.A.getPosition() > 0)) {
			Motor.A.backward();
			Motor.A.setSpeed(motor_A_speed);
		} else if (motor_A_forward && (Motor.A.getPosition() < 50)) {
			Motor.A.forward();
			Motor.A.setSpeed(motor_A_speed);
		} else {
			Motor.A.setSpeed(0);
		}
	}

	@Override
	public void onDestroy() {		
	}

}
