package svb.nxt.robot.game;

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import svb.nxt.robot.MainGame;
import svb.nxt.robot.bt.BTControls;
import svb.nxt.robot.logic.CommandPerformer;

/** 
 * class for robot type: printer
 * @author svab
 *
 */
public class GamePrinterTest extends GameTemplate {

	private static int CONSTANT_PEN_DISTANCE = 30;
	private static int CONSTANT_NEXT_LINE = 2;
	private static int CONSTANT_NEXT_CHAR = -2;
	
	private static int CONSTANT_RESET_LINE = 20;
	
	private static char NEW_LINE = '#';
	
	
	private boolean start = false;
	private StringBuilder strBuilder;
	
	NXTRegulatedMotor motorPen, motorRow, motorLine;
	

	@Override
	public void setMain(CommandPerformer commandPerformer) {
		this.mainGame = (MainGame) commandPerformer;
		strBuilder = new StringBuilder();
		initMotors();
	}
	
	private void initMotors(){
		motorPen = Motor.A;
		motorRow = Motor.B;
		motorLine = Motor.C;
		
		Motor.A.setSpeed(20);
		Motor.B.setSpeed(20);
		Motor.C.setSpeed(20);
	}

	@Override
	public void readInstructions(int command, byte[] parameter) {
		switch (parameter[2]) {
			case BTControls.FILE_START:			
				strBuilder = new StringBuilder();
				break;
			case BTControls.FILE_END:
				start = true;
				break;
			case BTControls.FILE_DATA:
				int i = parameter[3];
				strBuilder.append(Integer.toBinaryString(i));			
				break;
			case BTControls.FILE_NEW_LINE:
				strBuilder.append(NEW_LINE);
				break;
			}

	}

	@Override
	public void performInstructions() {
		if (start){
			String str = strBuilder.toString();
//			LCD.drawString(str.substring(0, 8), 0, 0);
			LCD.drawString("a" + str.length(), 0, 0);
		}
		
//		if (start){
//			start = false;
//			resetLines();
//			drawLine(strBuilder.toString());
			
//			drawLine("1111111111111110");			
//			drawLine("1100000000000110");
//			drawLine("1100110001100110");
//			drawLine("1100110001100110");
//			drawLine("1100000000000110");
//			drawLine("1100111111100110");
//			drawLine("1100000000000110");
//			drawLine("1111111111111110");			
//		}
		

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
		motorLine.rotate(CONSTANT_NEXT_LINE);
		moveLetter(-back);
	}
	
	
	private void moveLetter(int numberLetters){
		motorRow.rotate(CONSTANT_NEXT_CHAR * numberLetters);
	}
	
	private void penUp(){		
		motorPen.rotate(CONSTANT_PEN_DISTANCE);
	}
	
	private void penDown(){		
		motorPen.rotate(-CONSTANT_PEN_DISTANCE);
	}


	@Override
	public void onDestroy() {		
	}

}
