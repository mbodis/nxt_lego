package svb.nxt.robot.game;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import svb.nxt.robot.MainGame;
import svb.nxt.robot.bt.BTControls;
import svb.nxt.robot.logic.CommandPerformer;
import svb.nxt.robot.logic.constants.PenPrinterConst;

/** 
 * class for robot type: printer
 * @author svab
 *
 */
public class GamePrinterTest extends GameTemplate {

	private static int DISPL_MAX_W = 100;
	private static int DISPL_MAX_H = 60;
	
	private static int PEN_DIST = PenPrinterConst.CONSTANT_PEN_HEAD_DISTANCE;
	private static int COL = PenPrinterConst.CONSTANT_NEXT_ROW;
	private static int ROW = PenPrinterConst.CONSTANT_NEXT_COLUMN;
	
	private static int CONSTANT_RESET_LINE = 20;
	
	private static char NEW_LINE = '#';	
	
	private boolean start = false;
	private boolean doPrintByPen = false;
	private StringBuilder strBuilder;
	
	NXTRegulatedMotor motorPen, motorRow, motorLine;

	// show preview on LCD 
	private boolean moveHorizontally = true;
	private int scrMoveX = 0;
	private int scrMoveY = 0;

	@Override
	public void setMain(CommandPerformer commandPerformer) {
		this.mainGame = (MainGame) commandPerformer;
		this.showText = false;
		strBuilder = new StringBuilder();
		initMotors();		
	}
	
	private void initMotors(){
		motorPen = Motor.A;
		motorRow = Motor.B;
		motorLine = Motor.C;
		
		Motor.A.setSpeed(50);
		Motor.B.setSpeed(50);
		Motor.C.setSpeed(50);
		
//		Motor.A.setAcceleration(4000);
//		Motor.B.setAcceleration(4000);
//		Motor.C.setAcceleration(4000);
		
	}

	@Override
	public void readInstructions(int command, byte[] parameter) {
		switch (parameter[2]) {
			case BTControls.FILE_START:
				strBuilder = new StringBuilder();
				break;
			case BTControls.FILE_END:
				start = true;
				doPrintByPen = (parameter[3] == 1);
				break;
			case BTControls.FILE_DATA:
				int i = parameter[3];
//				strBuilder.append( Integer.toBinaryString((i & 0xFF) + 0x100).substring(1) );
				break;
			case BTControls.FILE_NEW_LINE:
//				strBuilder.append(NEW_LINE);
				break;
			}

	}

	@Override
	public void performInstructions() {
		
		if (start){
			//drawLCD(scrMoveX, scrMoveY);
			drawPen();						
		}		
		
	}
	
	private void drawPen(){
		
		if (doPrintByPen){
			doPrintByPen = false;
//			resetLines();
//			drawLine(strBuilder.toString());
//			/*
			drawLine("1111111111111110");			
			drawLine("1100000000000110");
			drawLine("1100110001100110");
			drawLine("1100110001100110");
			drawLine("1100000000000110");
			drawLine("1100111111100110");
			drawLine("1100000000000110");
			drawLine("1111111111111110");
//			*/			
		}
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

	private void drawLine(String str){
		
		boolean penUp = true;		
		int rowLength = 0;
		
		for(int i = 0; i < str.length(); i++){
			rowLength++;
			if ((str.charAt(i) == NEW_LINE)){
				nextLine(rowLength, penUp);
				rowLength = 0;
				continue;
			}
			if ((str.charAt(i) == '1') && (penUp)){
				penUp = false;
				penDown();
			}else if ((str.charAt(i) == '0') && (!penUp)){
				penUp = true;
				penUp();
			}
			moveLetter(1);
		}				
					
	}
	
	private void resetLines(){
		motorLine.rotate(-CONSTANT_RESET_LINE);
		motorLine.rotate(CONSTANT_RESET_LINE);
	}
	
	private void nextLine(int back, boolean penUp){
		if (!penUp)
			penUp();
		motorLine.rotate(ROW);
		moveLetter(-back);
	}	
	
	private void moveLetter(int numberLetters){
		motorRow.rotate(COL * numberLetters);
	}
	
	private void penUp(){		
		motorPen.rotate(PEN_DIST);
	}
	
	private void penDown(){		
		motorPen.rotate(-PEN_DIST);
	}

	@Override
	public void onDestroy() {		
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

}
