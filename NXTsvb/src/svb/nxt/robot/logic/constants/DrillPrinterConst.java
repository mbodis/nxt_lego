package svb.nxt.robot.logic.constants;

/**
 * printer movement constants
 * can be modified
 * @author svab
 *
 */
public class DrillPrinterConst {	
	
	/** new line in sended parts */
	public static int NEW_LINE = 999;
	
	/** direction values for motor A [1,-1] 1==forward -1==backward*/	
	public static int MOTOR_DIRECTION_A = -1;
	
	/** direction values for motor B [1,-1] 1==forward -1==backward*/
	public static int MOTOR_DIRECTION_B = -1;
	
	/** direction values for motor C [1,-1] 1==forward -1==backward*/
	public static int MOTOR_DIRECTION_C = -1;
	
	
	/** drill head returning to begin of row - speed */
	public static int RETURN_HEAD = 90;
	
	/** distance in deg. sapce away after hit begin of row/column */
	public static int BUTTON_SPACE = 35;
	
	
	/** speed moving up/down */
	private int penSpeed = 20;
	/** speed moving up/down */
	private int drillSpeed = 20;
		
	/** acceleration value, default: 6000 deg /sec / sec */
	private int moveAcceleration = 800;	
	
	/** min height in deg for drill head */
	private int drillMinValue= 30; //60;
	
	/** max height in deg for drill head */
	private int drillMaxValue= 210; //120;//210;
	
	/** moving pen printer head to next point X-axe */
	private int nextColumnValue = 4;
	
	/**	moving printer head to next line Y-axe */
	private int nextRowValue = 4;
	

	
	
	/** drill head speed deg / sec */
	private int moveSpeed = 40;

	public int getPenSpeed() {
		return penSpeed;
	}

	public void setPenSpeed(int penSpeed) {
		this.penSpeed = penSpeed;
	}

	public int getDrillSpeed() {
		return drillSpeed;
	}

	public void setDrillSpeed(int drillSpeed) {
		this.drillSpeed = drillSpeed;
	}

	public int getMoveAcceleration() {
		return moveAcceleration;
	}

	public void setMoveAcceleration(int moveAcceleration) {
		this.moveAcceleration = moveAcceleration;
	}

	public int getDrillMinValue() {
		return drillMinValue;
	}

	public void setDrillMinValue(int drillMinValue) {
		this.drillMinValue = drillMinValue;
	}

	public int getDrillMaxValue() {
		return drillMaxValue;
	}

	public void setDrillMaxValue(int drillMaxValue) {
		this.drillMaxValue = drillMaxValue;
	}

	public int getNextColumnValue() {
		return nextColumnValue;
	}

	public void setNextColumnValue(int nextColumnValue) {
		this.nextColumnValue = nextColumnValue;
	}

	public int getNextRowValue() {
		return nextRowValue;
	}

	public void setNextRowValue(int nextRowValue) {
		this.nextRowValue = nextRowValue;
	}

	public int getMoveSpeed() {
		return moveSpeed;
	}

	public void setMoveSpeed(int moveSpeed) {
		this.moveSpeed = moveSpeed;
	}
	
	
	
	
	/** 
	 * transform value 0-255 to DRILL_MIN and DRILL_MAX - height to drill 
	 */
	public double getDrillConstant(){
		return (double)( (double)(this.drillMaxValue - this.getDrillMinValue()) / 256);
	}

}
