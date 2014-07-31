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
	int coun = 0;
	int len = 0;		
	
	private boolean start = false;
	private boolean end = false;
	private boolean doPrintByDrill = false;	
	
	// send data
	private ArrayList<Integer> drillVals;
	private int part = 0;
	
	// printer state
	private int drill_distance_check = -1;	
	private boolean goToTheBeginningOfRow = false;
	private boolean goToTheBeginningOfPage = false;
	
	// printer hardware setup
	NXTRegulatedMotor motorDrill, motor_X, motor_Y;
	TouchSensor touchRow;
	TouchSensor touchColumn;
	int X = 0;

	// show preview on LCD - only possible for small image send together
	private boolean moveHorizontally = true;
	private int scrMoveX = 0;
	private int scrMoveY = 0;
	
	private MyThread sensorThread;	
	private DrillPrinterConst dHelper;
	
	private int drillDown = 0;

	//print status
	private int pRow = 0;
	private int pColumn = 0;	
	
	
	@Override
	public void setMain(CommandPerformer commandPerformer) {
		this.mainGame = (MainGame) commandPerformer;
		this.showText = false;		
		drillVals = new ArrayList<Integer>();
		sensorThread = new MyThread();
		sensorThread.start();
		dHelper = new DrillPrinterConst();
		setVisibilityOfNewInput(false);
		
		initMotors(true);
		LCD.clear();
		LCD.drawString("waiting for", 2, 3);
		LCD.drawString("picture", 5, 4);
		LCD.drawString(" . . . ", 5, 5);
		LCD.refresh();
	}
	
	private void initMotors(boolean register){
		
		if (register){
			
			touchRow = new TouchSensor(SensorPort.S1);
			touchColumn = new TouchSensor(SensorPort.S2);
			
			motorDrill = Motor.A;
			motor_X = Motor.B;
			motor_Y = Motor.C;
		}
		
		motorDrill.setSpeed(dHelper.getDrillSpeed());
		motor_X.setSpeed(dHelper.getMoveSpeed());
		motor_Y.setSpeed(dHelper.getMoveSpeed());
		
		motorDrill.setAcceleration(dHelper.getMoveAcceleration());
		motor_X.setAcceleration(dHelper.getMoveAcceleration());
		motor_Y.setAcceleration(dHelper.getMoveAcceleration());

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
				drillVals.add(DrillPrinterConst.NEW_LINE);
				break;
				
			case BTControls.DRILL_DISTANCE_CHECK_LOW:
				drill_distance_check  = parameter[2]; 				
				break;
				
			case BTControls.DRILL_DISTANCE_CHECK_DEEP:
				drill_distance_check  = parameter[2]; 				
				break;
				
			case BTControls.DRILL_MIN_DOWN:
				dHelper.setDrillMinValue(((int)parameter[3]) *2); 
				drill_distance_check  = parameter[2]; 				
				break;
				
			case BTControls.DRILL_MIN_UP:
				dHelper.setDrillMinValue(((int)parameter[3]) *2);
				drill_distance_check  = parameter[2]; 				
				break;
				
			case BTControls.DRILL_MAX_DOWN :
				dHelper.setDrillMaxValue(((int)parameter[3]) *2);
				drill_distance_check  = parameter[2]; 				
				break;
				
			case BTControls.DRILL_MAX_UP :				
				dHelper.setDrillMaxValue(((int)parameter[3]) *2);
				drill_distance_check  = parameter[2]; 				
				break;
				
			case BTControls.DRILL_SPEED:
				dHelper.setMoveSpeed(((int)parameter[2]));
				break;
				
			case BTControls.DRILL_HEAD_MOVE:
				dHelper.setNextColumnValue(((int)parameter[2]));
				dHelper.setNextRowValue(((int)parameter[2]));				
				break;
			}

	}

	@Override
	public void performInstructions() {
		
		//showLiveOPerformingInstruction();//debug
		
		if (start){			
			if (doPrintByDrill){
				doPrintByDrill = false;
				doDrill();
			}
		}
		
		if (drill_distance_check != -1){
			
			switch(drill_distance_check){
				case BTControls.DRILL_DISTANCE_CHECK_LOW:
					drillDown(dHelper.getDrillMinValue());
					drillUp(dHelper.getDrillMinValue());
					break;
				case BTControls.DRILL_DISTANCE_CHECK_DEEP:
					drillDown(dHelper.getDrillMaxValue());
					drillUp(dHelper.getDrillMaxValue());
					break;
				case BTControls.DRILL_MIN_DOWN:
					drillDown(dHelper.getDrillMinValue());
					break;
				case BTControls.DRILL_MIN_UP:					
					drillUp(dHelper.getDrillMinValue());
					break;
				case BTControls.DRILL_MAX_DOWN :
					drillDown(dHelper.getDrillMaxValue());
					break;
				case BTControls.DRILL_MAX_UP :					
					drillUp(dHelper.getDrillMaxValue());
					break;					
			}			
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
			goToBeginingOfPage1();
			goToBeginningOfRow1();		
			doBeep();
		}
		
		for(int i = 0; i < list.size(); i++){
			
			showProgressBarPart(list, i, part);
			drill(list.get(i), true);
		}
		
		doBeep();
	}	
	
	private void showProgressBarPart(ArrayList<Integer> list, int i, int part){
		
		LCD.clear();
		 
		LCD.drawString("X : " + pRow, 1, 1);
		LCD.drawString("Y: " + pColumn, 1, 2);
		LCD.drawString("part: " + (part+1), 1, 3);
		double percent = (double)((int)((double)((double)i/ list.size()*100) * 100))/100;
		LCD.drawString( percent + " %", 1, 4);
		
		//set progress bar 100×60
		for (int j = 0; j < 100; j++) {
			for (int k = 45; k < 60; k++) {
				if (j == 0 || j == 99 || k == 45 || k == 59){
					LCD.setPixel(j, k, 1);
				}
				if (j <= percent){
					LCD.setPixel(j, k, 1);
				}
			}
		}
					
		LCD.refresh();
	}
	
	/**
	 * val - new drill value
	 * headDown - drill head just change height between old/new value 
	 */
	private void drill(int val, boolean headDown){
		
		if (headDown){
			if (val == DrillPrinterConst.NEW_LINE){	
				pRow ++;
				pColumn = 0;
				
				drill_next_line();
			}else{
				pColumn ++;
				
				drill( (int)(dHelper.getDrillConstant() * val) );						
				drill_next_column();
			}

		}else{
			if (val == DrillPrinterConst.NEW_LINE){
				pRow ++;
				pColumn = 0;
				move_next_line();				
			}else{
				pColumn ++;
				drillDown( (int)(dHelper.getDrillConstant() * val) );
				drillUp( (int)(dHelper.getDrillConstant() * val));			
				move_next_column();							
			}			

		}
		
	}

	private void drill_next_line() {
		if (drillDown != 0)
			drillUp(drillDown);
		
		drillDown = 0;
		
		motor_Y.rotate(dHelper.getNextRowValue() 
				* DrillPrinterConst.MOTOR_DIRECTION_C);		
		goToBeginningOfRow1();
	}

	private void drill_next_column() {
		motor_X.rotate(dHelper.getNextColumnValue() 
				* DrillPrinterConst.MOTOR_DIRECTION_B);
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
		motor_Y.rotate(dHelper.getNextRowValue() 
				* DrillPrinterConst.MOTOR_DIRECTION_C);		
		goToBeginningOfRow1();
	}		
	
	/**
	 * presun na dalsi stlpec
	 */
	private void move_next_column(){
		motor_X.rotate(dHelper.getNextColumnValue() 
				* DrillPrinterConst.MOTOR_DIRECTION_B);
	}
	
	/**
	 * nastavenie hlavice os-X na zaciatok riadku
	 * pocka kym nezmackne button a od neho
	 * ide konstantny kusok
	 */
	private void goToBeginningOfRow1(){		
		motor_X.setSpeed(DrillPrinterConst.RETURN_HEAD * DrillPrinterConst.MOTOR_DIRECTION_B);// go faster
		if (DrillPrinterConst.MOTOR_DIRECTION_B == 1){
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
		motor_X.setSpeed(DrillPrinterConst.RETURN_HEAD * DrillPrinterConst.MOTOR_DIRECTION_B); // go faster
		motor_X.rotate(DrillPrinterConst.BUTTON_SPACE * DrillPrinterConst.MOTOR_DIRECTION_B);
		initMotors(false);
	}
	
	/**
	 * run only once at beginning of print - part 1
	 */
	private void goToBeginingOfPage1() {
		motor_Y.setSpeed(DrillPrinterConst.RETURN_HEAD * DrillPrinterConst.MOTOR_DIRECTION_C);// go faster
		if (DrillPrinterConst.MOTOR_DIRECTION_C == 1){
			motor_Y.backward();
		}else{
			motor_Y.forward();	
		}		
		goToTheBeginningOfPage = true;
		while(goToTheBeginningOfPage){
			// wait until hit the touch sensor
		}
		goToBeginingOfPage2();
		
	}
	
	/**
	 * run only once at beginning of print - part 2s
	 */
	private void goToBeginingOfPage2() {
		motor_Y.stop();
		motor_Y.flt();
		motor_Y.setSpeed(DrillPrinterConst.RETURN_HEAD * DrillPrinterConst.MOTOR_DIRECTION_C); // go faster
		motor_Y.rotate(DrillPrinterConst.BUTTON_SPACE * DrillPrinterConst.MOTOR_DIRECTION_C);
		initMotors(false);
	}
	
	private void drillUp(int height){		
		motorDrill.rotate(height 
				* DrillPrinterConst.MOTOR_DIRECTION_A);
	}

	private void drillDown(int height){		
		motorDrill.rotate(-1 * height 
				* DrillPrinterConst.MOTOR_DIRECTION_A);
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

