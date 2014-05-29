package svb.nxt.robot.bt;

public class BTControls {

	/**
	 * PROGRAM CODE
	 */
	public static final int NO_PROGRAM = -1;
	public static final int PROGRAM_MOVE_DIRECTION = 0;
	public static final int PROGRAM_MOVE_MOTOR = 1;
	public static final int PROGRAM_MOVE_OPEN_CV_COLOR = 2;
	public static final int PROGRAM_MOVE_ACCELEROMETER = 3;
	public static final int PROGRAM_READ_LINE = 4;
	public static final int PROGRAM_PRINTER_TEST = 5;
	public static final int PROGRAM_PRINTER_TEST_2 = 6;
	public static final int PROGRAM_PEN_PRINTER= 7;
	public static final int PROGRAM_DRILL_PRINTER= 8;
	public static final int PROGRAM_SEGWAY = 9;
	
	public static final int PROGRAM_TESTING = 20;
	
	/**
	 * PROGRAM NAME
	 */
	public static final String NO_PROGRAM_NAME = "";
	public static final String PROGRAM_MOVE_DIRECTION_NAME = "direction";
	public static final String PROGRAM_MOVE_MOTOR_NAME = "motor";
	public static final String PROGRAM_MOVE_OPEN_CV_COLOR_NAME = "colors";
	public static final String PROGRAM_MOVE_ACCELEROMETER_NAME = "accelerome";	
	public static final String PROGRAM_READ_LINE_NAME = "readLine";
	public static final String PROGRAM_PRINTER_TEST_NAME = "printerTest";
	public static final String PROGRAM_PRINTER_TEST_NAME_2 = "printerTest2";
	public static final String PROGRAM_PEN_PRINTER_NAME = "penPrinter";
	public static final String PROGRAM_DRILL_PRINTER_NAME= "drillPrinter";
	public static final String PROGRAM_SEGWAY_NAME = "Segway";

	public static String getProgramNameByType(int program) {
		switch (program) {
		case PROGRAM_MOVE_DIRECTION:
			return PROGRAM_MOVE_DIRECTION_NAME;
		case PROGRAM_MOVE_MOTOR:
			return PROGRAM_MOVE_MOTOR_NAME;
		case PROGRAM_MOVE_OPEN_CV_COLOR:
			return PROGRAM_MOVE_OPEN_CV_COLOR_NAME;
		case PROGRAM_MOVE_ACCELEROMETER:
			return PROGRAM_MOVE_ACCELEROMETER_NAME;
		case PROGRAM_READ_LINE:
			return PROGRAM_READ_LINE_NAME;
		case PROGRAM_PRINTER_TEST:
			return PROGRAM_PRINTER_TEST_NAME;
		case PROGRAM_PRINTER_TEST_2:
			return PROGRAM_PRINTER_TEST_NAME_2;
		case PROGRAM_PEN_PRINTER:
			return PROGRAM_PEN_PRINTER_NAME;
		case PROGRAM_DRILL_PRINTER:
			return PROGRAM_DRILL_PRINTER_NAME;
		case PROGRAM_SEGWAY:
			return PROGRAM_SEGWAY_NAME;
		default:
			return NO_PROGRAM_NAME;
		}	
	}

	/**
	 * ROBOT TYPE
	 */
	public static final int NO_ROBOT_TYPE = -1;
	public static final int ROBOT_TYPE_TRIBOT = 10;
	public static final int ROBOT_TYPE_LEJOS = 11;
	public static final int ROBOT_TYPE_PRINTER = 12;
	public static final int ROBOT_TYPE_SEGWAY = 13;
	/**
	 * ROBOT TYPE
	 */
	public static final String NO_ROBOT_TYPE_NAME = "";
	public static final String ROBOT_TYPE_TRIBOT_NAME = "tribot";
	public static final String ROBOT_TYPE_LEJOS_NAME = "lejos";
	public static final String ROBOT_TYPE_PRINTER_NAME = "printer";

	
	public static String getRobotNameByType(int robotType) {
		switch (robotType) {
		case ROBOT_TYPE_TRIBOT:
			return ROBOT_TYPE_TRIBOT_NAME;
		case ROBOT_TYPE_LEJOS:
			return ROBOT_TYPE_LEJOS_NAME;
		case ROBOT_TYPE_PRINTER:
			return ROBOT_TYPE_PRINTER_NAME;
		default:
			return NO_ROBOT_TYPE_NAME;
		}		
	}
	
	/**
	 * MOTORS SINGLE
	 */
	public static final int MOTOR_A_FORWARD_START = 10;
	public static final int MOTOR_A_FORWARD_STOP = 11;
	public static final int MOTOR_A_BACKWARD_START = 12;
	public static final int MOTOR_A_BACKWARD_STOP = 13;

	public static final int MOTOR_B_FORWARD_START = 20;
	public static final int MOTOR_B_FORWARD_STOP = 21;
	public static final int MOTOR_B_BACKWARD_START = 22;
	public static final int MOTOR_B_BACKWARD_STOP = 23;

	public static final int MOTOR_C_FORWARD_START = 30;
	public static final int MOTOR_C_FORWARD_STOP = 31;
	public static final int MOTOR_C_BACKWARD_START = 32;
	public static final int MOTOR_C_BACKWARD_STOP = 33;
	
	public static final int MOTOR_SET_SPEED = 34;
	public static final int MOTOR_SET_ACC = 35;

	/**
	 * MOTORS COMBINATION, ACCELEROMETER, OPEN_CV_COLOR_MOVE, READLINE, PRINTER
	 */
	public static final int GO_FORWARD_B_C_START = 40;
	public static final int GO_FORWARD_B_C_STOP = 41;
	public static final int GO_BACKWARD_B_C_START = 42;
	public static final int GO_BACKWARD_B_C_STOP = 43;

	public static final int TURN_RIGHT_START = 50;
	public static final int TURN_RIGHT_STOP = 51;
	public static final int TURN_LEFT_START = 52;
	public static final int TURN_LEFT_STOP = 53;
	
	public static final int POWER = 55;
	
	/**
	 * SENSORS
	 */
	
	public static final int LIGHT_SET_MIN = 60;
	public static final int LIGHT_SET_MAX = 61;
	
	/**
	 * PRINTER
	 */
	public static final int FILE_PAUSE = 53;
	public static final int FILE_RESUME = 54;
	public static final int FILE_START = 55;
	public static final int FILE_END = 56;
	public static final int FILE_NEW_LINE = 57;
	public static final int FILE_DATA = 58;
	public static final int PEN_DISTANCE_CHECK = 59;
	
}
