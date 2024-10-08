package svb.nxt.robot.logic.constants;

public class PenPrinterConst {
	
	/**
	 * moving pen head up/down
	 */
	public static int CONSTANT_PEN_HEAD_DISTANCE = 60;
	
	/**
	 * moving pen printer head to next point
	 */
	public static int CONSTANT_NEXT_COLUMN = 9;
	
	/**	 
	 * moving printer head to next line X-axe
	 */
	public static int CONSTANT_NEXT_ROW = 9;
	
	/**
	 * motor speed
	 * deg / sec
	 */
	public static int CONSTANT_MOVE_SPEED = 10;
	public static int CONSTANT_MOVE_SPEED_PEN = 20;
		
	/**
	 * acceleration value 
	 * default: 6000
	 * deg /sec / sec
	 */
	public static int CONSTANT_MOVE_ACCELERATION = 800;
	
	public static int CONS_MOTOR_A_FORWARD = 1;
	public static int CONS_MOTOR_B_FORWARD = -1;
	public static int CONS_MOTOR_C_FORWARD = -1;
}
