package svb.nxt.robot.logic.constants;

public class PenPrinterConst {
	
	/**
	 * moving pen head up/down
	 */
	public static int CONSTANT_PEN_HEAD_DISTANCE = 60;
	
	/**
	 * moving printer head to next line Y-axe
	 */
	public static int CONSTANT_NEXT_ROW = 2;
	
	/**
	 * moving pen printer head to next point
	 */
	public static int CONSTANT_NEXT_COLUMN = 8;
	
	/**
	 * motor speed
	 * deg / sec
	 */
	public static int CONSTANT_MOVE_SPEED = 10;
		
	/**
	 * acceleration value 
	 * default: 6000
	 * deg /sec / sec
	 */
	public static int CONSTANT_MOVE_ACCELERATION= 1000;
}
