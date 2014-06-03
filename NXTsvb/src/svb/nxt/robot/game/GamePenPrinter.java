package svb.nxt.robot.game;

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
import svb.nxt.robot.logic.PrinterHeler;
import svb.nxt.robot.logic.constants.PenPrinterConst;

/** 
 * class for robot type: printer<br>
 * 
 * <b>setup:</b><br>
 * Motor A - pen head (up down)<br>
 * Motor B - X-axis<br>
 * Motor C - Y-axis<br>
 * Sensor 1: - Touch sensor<br>
 * @author svab
 *
 */
public class GamePenPrinter extends GameTemplate {

	private static int DISPL_MAX_W = 100;
	private static int DISPL_MAX_H = 60;
	
	public static char NEW_LINE = '#';	
	
	private boolean start = false;
	private boolean end = false;
	private boolean doPrintByPen = false;
	private boolean doDrawLCD = false;
	
	// send data
	private StringBuilder strBuilder;
	private int part = 0;
	
	
	private int pen_distance_check = -1;	
	private boolean goToTheBeginningOfRow = false;
	
	NXTRegulatedMotor motorPen, motor_X, motor_Y;
	TouchSensor touch;
	int X = 0;

	// show preview on LCD 
	private boolean moveHorizontally = true;
	private int scrMoveX = 0;
	private int scrMoveY = 0;
	
	private MyThread myThread;

	@Override
	public void setMain(CommandPerformer commandPerformer) {
		this.mainGame = (MainGame) commandPerformer;
		this.showText = false;		
		strBuilder = new StringBuilder();
		myThread = new MyThread();
		myThread.start();
		
		initMotors(true);		
	}
	
	private void initMotors(boolean register){
		
		if (register){
			
			touch = new TouchSensor(SensorPort.S1);
			
			motorPen = Motor.A;
			motor_X = Motor.B;
			motor_Y = Motor.C;
		}
		
		motorPen.setSpeed(PenPrinterConst.CONSTANT_MOVE_SPEED_PEN);
		motor_X.setSpeed(PenPrinterConst.CONSTANT_MOVE_SPEED);
		motor_Y.setSpeed(PenPrinterConst.CONSTANT_MOVE_SPEED);
		
		motorPen.setAcceleration(PenPrinterConst.CONSTANT_MOVE_ACCELERATION);
		motor_X.setAcceleration(PenPrinterConst.CONSTANT_MOVE_ACCELERATION);
		motor_Y.setAcceleration(PenPrinterConst.CONSTANT_MOVE_ACCELERATION);

	}

	@Override
	public void readInstructions(int command, byte[] parameter) {
		
		switch (parameter[2]) {
		
			case BTControls.FILE_START_PACKAGE:
			case BTControls.FILE_START:			
				strBuilder = new StringBuilder();				
				break;
				
			case BTControls.FILE_END_PACKAGE:
			case BTControls.FILE_END:
				start = true;
				doPrintByPen = ((parameter[3] == BTControls.ACTION_PRINT) 
						|| (parameter[3] == BTControls.ACTION_PRINT_AND_DISPLAY));
				doDrawLCD = ((parameter[3] == BTControls.ACTION_DISPLAY) 
						|| (parameter[3] == BTControls.ACTION_PRINT_AND_DISPLAY));
				end = (parameter[2] == BTControls.FILE_END);
				break;		
				
			case BTControls.FILE_DATA:
				int i = parameter[3];				
				strBuilder.append( Integer.toBinaryString((i & 0xFF) + 0x100).substring(1) );
				break;
				
			case BTControls.FILE_NEW_LINE:
				strBuilder.append(NEW_LINE);
				break;
				
			case BTControls.PEN_DISTANCE_CHECK:
			case BTControls.PEN_UP:
			case BTControls.PEN_DOWN:
				pen_distance_check  = parameter[2]; 				
				break;				
			}

	}

	@Override
	public void performInstructions() {
		
		//showLiveOPerformingInstruction();
		
		if (start){
			if (doDrawLCD){
				drawLCD(scrMoveX, scrMoveY);
			}
			if (doPrintByPen){
				doPrintByPen = false;
				drawPen();
			}
		}
		
		if (pen_distance_check != -1){
			
			switch(pen_distance_check){
				case BTControls.PEN_DISTANCE_CHECK:
					penDown();
					penUp();
					break;
				case BTControls.PEN_UP:				
					penUp();
					break;
					
				case BTControls.PEN_DOWN:
					penDown();
					break;
			}
			pen_distance_check = -1;
		}
		
		LCD.drawString("p: " + part + " l: "+ strBuilder.toString().length(), 1, 7);				
		LCD.refresh();

	}
	
	/*
	 * vidime ci je hlavny thread volny alebo nieco vykonava
	 * zobrazovanie cisel 0-9 dokola
	 */
	private void showLiveOPerformingInstruction(){
		LCD.clear();
		LCD.drawString("X"+X, 3, 3);
		LCD.refresh();
		X=(X+1)%10;
	}
	
	private void drawPen(){
				
		strBuilder = PrinterHeler.removeEmptySlots(strBuilder);
		drawString(strBuilder.toString(), part);//TODO UNCOMMENT
		part ++;
		
		// ak nie je koniec pytaj si dalsiu cast
		if (!end){				
			this.mainGame.lcpThread.sendMsgToPhone(BTConnector.FILE_NEXT_PART, (byte)0x0);
		}
		
		/*
		drawLine("1111111111111110");			
		drawLine("1100000000000110");
		drawLine("1100110001100110");
		drawLine("1100110001100110");
		drawLine("1100000000000110");
		drawLine("1100111111100110");
		drawLine("1100000000000110");
		drawLine("1111111111111110");
		*/		
	}
		
	private void drawLCD(int moveColumn, int moveRow) {
		LCD.clear();
		
		int limitColumn = DISPL_MAX_W + moveColumn;
		int limitRow = DISPL_MAX_H + moveRow;
		
		int column = 0-moveColumn;
		int row = 0;
		
		for(int k=0; k< strBuilder.length(); k++){
			if (moveRow>0){
				if (strBuilder.charAt(k) == NEW_LINE){
					moveRow --;
				}
				continue;
			}
				
			if (strBuilder.charAt(k) == NEW_LINE){
				row++;
				column = 0-moveColumn;
				continue;
			}
			
			if (strBuilder.charAt(k) == '1'){
				if (column >0 && row < limitRow){ 
					LCD.setPixel(column, row, 1);
				}
			}
			
			column++;
		}
		
		LCD.refresh();
	}

	private void drawString(String str, int part){
		
		if (part == 0){
			initLineYMotor();
			goToBeginningOfRow1();		
			doBeep();
		}
		
		// rata dlzku riadku
		int rowLength = 0; 
		
		for(int i = 0; i < str.length(); i++){
			
			if ((str.charAt(i) == NEW_LINE)){
				move_next_line();
				rowLength = 0;
				continue;
			}
			if ((str.charAt(i) == '1')){
				penDown();
				penUp();			
			}else if (str.charAt(i) == '0'){
				
			}
						
			move_next_column();			
			rowLength++;
		}
		
		doBeep();
	}
	
	/**
	 * presun na dalsi riadok
	 * @param back
	 */
	private void move_next_line(){		
		motor_Y.rotate(PenPrinterConst.CONSTANT_NEXT_ROW 
				* PenPrinterConst.CONS_MOTOR_C_FORWARD);		
		goToBeginningOfRow1();
	}		
	
	/**
	 * presun na dalsi stlpec
	 */
	private void move_next_column(){
		motor_X.rotate(PenPrinterConst.CONSTANT_NEXT_COLUMN 
				* PenPrinterConst.CONS_MOTOR_B_FORWARD);
	}
	
	/**
	 * nastavenie hlavice os-X na zaciatok riadku
	 * pocka kym nezmackne button a od neho
	 * ide konstantny kusok
	 */
	private void goToBeginningOfRow1(){		
		motor_X.setSpeed(20 * PenPrinterConst.CONS_MOTOR_B_FORWARD);// go faster
		if (PenPrinterConst.CONS_MOTOR_B_FORWARD == 1){
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
		motor_X.setSpeed(10 * PenPrinterConst.CONS_MOTOR_B_FORWARD); // go faster
		motor_X.rotate(10 * PenPrinterConst.CONS_MOTOR_B_FORWARD);
		initMotors(false);
	}
	
	private void initLineYMotor(){
		motor_Y.rotate(-50 * PenPrinterConst.CONS_MOTOR_C_FORWARD);
		motor_Y.rotate(50 * PenPrinterConst.CONS_MOTOR_C_FORWARD);		
		initMotors(false);
	}	
	
	private void penUp(){		
		motorPen.rotate(PenPrinterConst.CONSTANT_PEN_HEAD_DISTANCE 
				* PenPrinterConst.CONS_MOTOR_A_FORWARD);
	}
	
	private void penDown(){		
		motorPen.rotate(-1 * PenPrinterConst.CONSTANT_PEN_HEAD_DISTANCE 
				* PenPrinterConst.CONS_MOTOR_A_FORWARD);
	}

	@Override
	public void onDestroy() {
		myThread.setIsAlive(false);
		myThread.interrupt();		
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
				if (touch != null && touch.isPressed()) {	
					goToTheBeginningOfRow = false;
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
