package svb.nxt.robot.game;

import java.util.ArrayList;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import svb.nxt.robot.MainGame;
import svb.nxt.robot.bt.BTConnector;
import svb.nxt.robot.bt.BTControls;
import svb.nxt.robot.logic.CommandPerformer;
import svb.nxt.robot.logic.constants.DrillPrinterConst;


/** 
 * class for robot type: printer<br>
 * 
 * <b>setup:</b><br>
 * Motor A - drill head (up down)<br>
 * Motor B - X-axis<br>
 * Motor C - Y-axis<br>
 * Sensor 1: - Touch sensor row<br>
 * Sensor 2: - Touch sensor column<br>
 * @author svab
 *
 */
public class GameDrillPrinter extends GameTemplate {
	// debug 
	//int coun = 0;
	//int len = 0;
	
	public static int NEW_LINE = 999;
	
	/** max height in deg for drill head */
	public int CONSTANT_DRILL_MIN= 80;
	
	/** max height in deg for drill head */
	public int CONSTANT_DRILL_MAX= 230;
	
	/** moving pen printer head to next point */
	public int CONSTANT_NEXT_COLUMN = 6;
	
	/**	moving printer head to next line X-axe */
	public int CONSTANT_NEXT_ROW = 6;
	
	/** transform value 0-255 to DRILL_MIN and DRILL_MAX - height to drill */
	public double CONSTANT_DRILL = (double)( (double)(CONSTANT_DRILL_MAX - CONSTANT_DRILL_MIN) / 256);
	
	/** drii head speed deg / sec */
	public int CONSTANT_MOVE_SPEED = 10;
	
	private boolean start = false;
	private boolean end = false;
	private boolean doPrintByDrill = false;	
	
	// send data
	private ArrayList<Integer> drillVals;
	private int part = 0;
	
	
	private int drill_distance_check = -1;	
	private boolean goToTheBeginningOfRow = false;
	private boolean goToTheBeginningOfPage = false;
	
	NXTRegulatedMotor motorDrill, motor_X, motor_Y;
	TouchSensor touchRow;
	TouchSensor touchColumn;
	int X = 0;

	// show preview on LCD 
	private boolean moveHorizontally = true;
	private int scrMoveX = 0;
	private int scrMoveY = 0;
	
	private MyThread sensorThread;	
	
	private int drillDown = 0;

	@Override
	public void setMain(CommandPerformer commandPerformer) {
		this.mainGame = (MainGame) commandPerformer;
		this.showText = false;		
		drillVals = new ArrayList<Integer>();
		sensorThread = new MyThread();
		sensorThread.start();
		
		initMotors(true);		
	}
	
	private void initMotors(boolean register){
		
		if (register){
			
			touchRow = new TouchSensor(SensorPort.S1);
			touchColumn = new TouchSensor(SensorPort.S2);
			
			motorDrill = Motor.A;
			motor_X = Motor.B;
			motor_Y = Motor.C;
		}
		
		motorDrill.setSpeed(DrillPrinterConst.CONSTANT_MOVE_SPEED_PEN);
		motor_X.setSpeed(CONSTANT_MOVE_SPEED);
		motor_Y.setSpeed(CONSTANT_MOVE_SPEED);
		
		motorDrill.setAcceleration(DrillPrinterConst.CONSTANT_MOVE_ACCELERATION);
		motor_X.setAcceleration(DrillPrinterConst.CONSTANT_MOVE_ACCELERATION);
		motor_Y.setAcceleration(DrillPrinterConst.CONSTANT_MOVE_ACCELERATION);

	}

	@Override
	public void readInstructions(int command, byte[] parameter) {
		
		switch (parameter[2]) {
		
			case BTControls.FILE_START_PACKAGE:
				//break; //XXX comment
			case BTControls.FILE_START:			
				drillVals = new ArrayList<Integer>();				
				break;
				
			case BTControls.FILE_END_PACKAGE:
			case BTControls.FILE_END:
				start = true;
				doPrintByDrill = ((parameter[3] == BTControls.ACTION_PRINT));				
				end = (parameter[2] == BTControls.FILE_END);
				break;		
				
			case BTControls.FILE_DATA:
				int v = (parameter[3] < 0) ? parameter[3] + 256 : parameter[3];
				drillVals.add(v);
				//coun +=v;
				//len ++;
				break;
				
			case BTControls.FILE_NEW_LINE:
				drillVals.add(NEW_LINE);
				break;
				
			case BTControls.DRILL_DISTANCE_CHECK_LOW:
				drill_distance_check  = parameter[2]; 				
				break;
				
			case BTControls.DRILL_DISTANCE_CHECK_DEEP:
				drill_distance_check  = parameter[2]; 				
				break;
				
			case BTControls.DRILL_MIN_DOWN:
				this.CONSTANT_DRILL_MIN = ((int)parameter[3]) *2; 
				drill_distance_check  = parameter[2]; 				
				break;
				
			case BTControls.DRILL_MIN_UP:
				this.CONSTANT_DRILL_MIN = ((int)parameter[3]) *2;
				drill_distance_check  = parameter[2]; 				
				break;
				
			case BTControls.DRILL_MAX_DOWN :
				this.CONSTANT_DRILL_MAX = ((int)parameter[3]) *2;
				drill_distance_check  = parameter[2]; 				
				break;
				
			case BTControls.DRILL_MAX_UP :				
				this.CONSTANT_DRILL_MAX = ((int)parameter[3]) *2;
				drill_distance_check  = parameter[2]; 				
				break;
				
			case BTControls.DRILL_SPEED:
				this.CONSTANT_MOVE_SPEED = ((int)parameter[2]);
				break;
				
			case BTControls.DRILL_HEAD_MOVE:
				this.CONSTANT_NEXT_COLUMN = ((int)parameter[2]);
				this.CONSTANT_NEXT_ROW = ((int)parameter[2]);
				break;
			}

	}

	@Override
	public void performInstructions() {
		
		//showLiveOPerformingInstruction();
		
		if (start){			
			if (doPrintByDrill){
				doPrintByDrill = false;
				doDrill();
			}
		}
		
		if (drill_distance_check != -1){
			
			switch(drill_distance_check){
				case BTControls.DRILL_DISTANCE_CHECK_LOW:
					drillDown(CONSTANT_DRILL_MIN);
					drillUp(CONSTANT_DRILL_MIN);
					break;
				case BTControls.DRILL_DISTANCE_CHECK_DEEP:
					drillDown(CONSTANT_DRILL_MAX);
					drillUp(CONSTANT_DRILL_MAX);
					break;
				case BTControls.DRILL_MIN_DOWN:
					drillDown(CONSTANT_DRILL_MIN);
					break;
				case BTControls.DRILL_MIN_UP:					
					drillUp(CONSTANT_DRILL_MIN);
					break;
				case BTControls.DRILL_MAX_DOWN :
					drillDown(CONSTANT_DRILL_MAX);
					break;
				case BTControls.DRILL_MAX_UP :					
					drillUp(CONSTANT_DRILL_MAX);
					break;					
			}
			motorDrill.stop();
			motorDrill.flt();
			drill_distance_check = -1;
		}
		
		//if (drillVals.size() > 1){
		//	LCD.drawString(" drill[0] " + drillVals.get(0) , 1, 1);				
		//	LCD.drawString(" drill[1] " + drillVals.get(drillVals.size()-1) , 1, 2);
		//}
		
		//LCD.drawString("pt: " + part + " siz: "+ len , 1, 7);				
		//LCD.drawString("count:" + coun, 1, 6);
		//LCD.refresh();

	}
	
	/*
	 * vidime ci je hlavny thread volny alebo nieco vykonava
	 * zobrazovanie cisel 0-9 dokola
	 */
	@SuppressWarnings("unused")
	private void showLiveOPerformingInstruction(){
		LCD.clear();
		LCD.drawString("X"+X, 3, 3);
		LCD.refresh();
		X=(X+1)%10;
	}
	
	private void doDrill(){
				
		//drillHeights = PrinterHeler.removeEmptySlots(drillHeights);//TODO ADD OPTIMALIZATION VALUES??
		drillValues(drillVals, part);//XXX uncomment
		part ++;
		
		// ak nie je koniec pytaj si dalsiu cast
		if (!end){				
			MainGame.lcpThread.sendMsgToPhone(BTConnector.FILE_NEXT_PART, (byte)0x0);
		}
		
	}
		
	private void drillValues(ArrayList<Integer> list, int part){
		
		if (part == 0){
			goToBeginingOrPage1();
			goToBeginningOfRow1();		
			doBeep();
		}
		
		for(int i = 0; i < list.size(); i++){
			
			LCD.clear();
			 
			LCD.drawString("const:" + CONSTANT_DRILL, 1, 5);
			LCD.drawString("print:" + list.get(i), 1, 6);
			LCD.drawString("down:" + (int)(((double)CONSTANT_DRILL * list.get(i))), 1, 7);
			LCD.refresh();
						
			drill(list.get(i), true);
		}
		
		doBeep();
	}	
	
	/**
	 * val - new drill value
	 * headDown - drill head just change height between old/new value 
	 */
	private void drill(int val, boolean headDown){
		
		if (headDown){
			if (val == NEW_LINE){				
				drill_next_line();
			}else{													
				drill( (int)(CONSTANT_DRILL * val) );						
				drill_next_column();
			}

		}else{
			if (val == NEW_LINE){				
				move_next_line();				
			}else{					
				drillDown( (int)(CONSTANT_DRILL * val) );
				drillUp( (int)(CONSTANT_DRILL * val));			
				move_next_column();							
			}			

		}
		
	}

	private void drill_next_line() {
		if (drillDown != 0)
			drillUp(drillDown);
		
		drillDown = 0;
		
		motor_Y.rotate(CONSTANT_NEXT_ROW 
				* DrillPrinterConst.CONS_MOTOR_C_FORWARD);		
		goToBeginningOfRow1();
	}

	private void drill_next_column() {
		motor_X.rotate(CONSTANT_NEXT_COLUMN 
				* DrillPrinterConst.CONS_MOTOR_B_FORWARD);
	}

	private void drill(int i) {
		if (drillDown==0){
			drillDown(i);
		}else{
			if(drillDown-i > 0){
				drillUp(drillDown - i);
			}else{
				drillDown(i - drillDown);
			}
		}	
		
		drillDown = i; 
	}

	/**
	 * presun na dalsi riadok
	 * @param back
	 */
	private void move_next_line(){		
		motor_Y.rotate(CONSTANT_NEXT_ROW 
				* DrillPrinterConst.CONS_MOTOR_C_FORWARD);		
		goToBeginningOfRow1();
	}		
	
	/**
	 * presun na dalsi stlpec
	 */
	private void move_next_column(){
		motor_X.rotate(CONSTANT_NEXT_COLUMN 
				* DrillPrinterConst.CONS_MOTOR_B_FORWARD);
	}
	
	/**
	 * nastavenie hlavice os-X na zaciatok riadku
	 * pocka kym nezmackne button a od neho
	 * ide konstantny kusok
	 */
	private void goToBeginningOfRow1(){		
		motor_X.setSpeed(30 * DrillPrinterConst.CONS_MOTOR_B_FORWARD);// go faster
		if (DrillPrinterConst.CONS_MOTOR_B_FORWARD == 1){
			motor_X.backward();
		}else{
			motor_X.forward();	
		}		
		goToTheBeginningOfRow = true;
		while(goToTheBeginningOfRow){
			// wait until hit the touch sensor
		}
		goToBeginningOfRow2();
	}
	private void goToBeginningOfRow2(){
		motor_X.stop();
		motor_X.flt();
		motor_X.setSpeed(10 * DrillPrinterConst.CONS_MOTOR_B_FORWARD); // go faster
		motor_X.rotate(35 * DrillPrinterConst.CONS_MOTOR_B_FORWARD);
		initMotors(false);
	}
	
	private void goToBeginingOrPage1() {
		motor_Y.setSpeed(30 * DrillPrinterConst.CONS_MOTOR_C_FORWARD);// go faster
		if (DrillPrinterConst.CONS_MOTOR_C_FORWARD == 1){
			motor_Y.backward();
		}else{
			motor_Y.forward();	
		}		
		goToTheBeginningOfPage = true;
		while(goToTheBeginningOfPage){
			// wait until hit the touch sensor
		}
		goToBeginingOrPage2();
		
	}
	
	private void goToBeginingOrPage2() {
		motor_Y.stop();
		motor_Y.flt();
		motor_Y.setSpeed(10 * DrillPrinterConst.CONS_MOTOR_C_FORWARD); // go faster
		motor_Y.rotate(35 * DrillPrinterConst.CONS_MOTOR_C_FORWARD);
		initMotors(false);
	}
	
	private void drillUp(int height){		
		motorDrill.rotate(height 
				* DrillPrinterConst.CONS_MOTOR_A_FORWARD);
	}

	private void drillDown(int height){		
		motorDrill.rotate(-1 * height 
				* DrillPrinterConst.CONS_MOTOR_A_FORWARD);
	}
	
	@Override
	public void onDestroy() {
		sensorThread.setIsAlive(false);
		sensorThread.interrupt();		
	}

	@Override
	public void buttonPressed(int btnID) {
		
		switch (btnID){
			case Button.ID_RIGHT:
				if(moveHorizontally){
					scrMoveX ++;
				}else{
					scrMoveY ++;
				}
				break;
				
			case Button.ID_LEFT:
				if(moveHorizontally){				
					
					
					if (scrMoveX > 0)
						scrMoveX --;
				}else{
					if (scrMoveY > 0)
						scrMoveY --;
				}
				
				break;
			case Button.ID_ENTER:
				moveHorizontally = !moveHorizontally;
				break;
		}
		//LCD.drawString("X:" + scrMoveX + " Y:" + scrMoveY , 1, 1);
	}
	
	private void doBeep(){
		Sound.playNote(Sound.PIANO, 3654, 200);	
		Sound.playNote(Sound.PIANO, 3654, 200);	
		Sound.playNote(Sound.PIANO, 3654, 200);
	}
	
	private class MyThread extends Thread{
		
		private boolean alive = false;
		
		public MyThread() {
			super();
			alive = true;
		}
		
		public void setIsAlive(boolean set){
			this.alive = set;
		}
		
		
		@Override
		public void run() {
			while(alive){
				if (touchRow != null && touchRow.isPressed()) {	
					goToTheBeginningOfRow = false;
					// LCD.clear();
					// LCD.drawString("Touch me!", 3, 4);				
					// LCD.refresh();					
				}
				if (touchColumn != null && touchColumn.isPressed()) {	
					goToTheBeginningOfPage = false;
					// LCD.clear();
					// LCD.drawString("Touch me!", 3, 4);				
					// LCD.refresh();					
				}
				
				try {
					sleep(50);
				} catch (InterruptedException e) { 
					e.printStackTrace();
				}
			}			
		}
		
	}
}

