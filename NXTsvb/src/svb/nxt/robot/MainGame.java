package svb.nxt.robot;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import svb.nxt.robot.bt.BTConnector;
import svb.nxt.robot.bt.BTControls;
import svb.nxt.robot.game.GameMoveAccelerometer;
import svb.nxt.robot.game.GameMoveDirection;
import svb.nxt.robot.game.GameMoveMotor;
import svb.nxt.robot.game.GamePrinterTest;
import svb.nxt.robot.game.GameReadLine;
import svb.nxt.robot.game.GameSegway;
import svb.nxt.robot.game.GameTemplate;
import svb.nxt.robot.game.GameTesting;
import svb.nxt.robot.logic.CommandPerformer;
import svb.nxt.robot.logic.LMDutils;

/**
 * This class is for implementing the first real game combining MINDdroid and
 * NXT robots. The motors/sensors have to be connected as the following: Motor
 * left: PORT C Motor right: PORT B Light/Color Sensor: PORT 3
 */
public class MainGame implements CommandPerformer {
	
	public static final int GAME_SEGWAY = 1;
	public static final int GAME_EMPTY = 2;
	public static final int NUMBER_LEVELS = 2;
	
	
	private static BTConnector lcpThread;	

	public static GameTemplate game;
	private static int gameType = BTControls.NO_PROGRAM;
	private int robotType = BTControls.NO_ROBOT_TYPE;
    
	private static int selectManualLevel = GAME_SEGWAY;
	
	public int getRobotType(){
		return robotType;
	}
    
	/**
	 * The start of the program. Does registering of the commands.
	 * 
	 * @args The command line paramaters
	 */
	public static void main(String[] args) throws Exception {
		lcpThread = new BTConnector(new MainGame());

		// also register the output and action command here,
		// so we can decide for ourselves what to do when
		// we get new motor commands or action button press
		
		// from MINDdroid
		lcpThread.registerCommand("OUTPUT", BTConnector.OUTPUT_COMMAND);
		lcpThread.registerCommand("ACTION", BTConnector.ACTION_COMMAND);
		
		// register the command for displaying effects and start it as a thread
		lcpThread.registerCommand("DISPLAY", BTConnector.DAEMON_1);
		lcpThread.startThreadForCommand(BTConnector.DAEMON_1);
		
		// register the command for driving the robot and start it as a thread
		lcpThread.registerCommand("DRIVE", BTConnector.DAEMON_2);
		lcpThread.startThreadForCommand(BTConnector.DAEMON_2);

		lcpThread.setDaemon(true);
		lcpThread.start();

		boolean running = true;		
		
		Button.setKeyClickTone(0, 0);
		Button.setKeyClickVolume(0);
		
		
		while (running) {
			// Read Escape Button and eventually stop the program
			if (Button.ESCAPE.isDown()) {
				try{
					if (game != null)
						game.onDestroy();
				}catch(Exception e){
					//nope pop
				}
				running = false;				
				lcpThread.terminate();
			}
			if (Button.RIGHT.isDown()) {
				if (game != null)
					game.buttonPressed(Button.ID_RIGHT);
				if(selectManualLevel < NUMBER_LEVELS )
				selectManualLevel += 1;
			}
			if (Button.LEFT.isDown()) {
				if (game != null)
					game.buttonPressed(Button.ID_LEFT);
				if(selectManualLevel > 1)
					selectManualLevel -= 1;
			}
			if (Button.ENTER.isDown()) {
				if (game == null){
					switch (selectManualLevel) {				
					case GAME_SEGWAY:
						gameType = BTControls.PROGRAM_SEGWAY;
						createGame();
						break;
					case GAME_EMPTY:					
						gameType = BTControls.PROGRAM_TESTING;
						createGame();
						break;
	
					default:
						break;
					}
				}else{
					game.buttonPressed(Button.ID_ENTER);
				}
			}
			if (LMDutils.interruptedSleep(100))
				break;
		}		
	}

	/**
	 * Displays text on the LCD depending on the current state of the game.
	 * 
	 * @parame line the display position of the first line
	 */
	private void displayText(int loading) {
		
		String displayText1 = "";
		String displayText2 = "";
		String displayText3 = "";
		String displayText4 = "";
		String displayText5 = "";
		
		
		if (lcpThread.isConnected()){
			displayText1 = "Connected";
			displayText2 = "game: " + BTControls.getProgramNameByType(gameType);
			displayText3 = "robot: " + BTControls.getRobotNameByType(robotType);
			
			LCD.drawString(displayText1, 8 - displayText1.length() / 2, 0);
			LCD.drawString(displayText2, 8 - displayText2.length() / 2, 1);
			LCD.drawString(displayText3, 8 - displayText3.length() / 2, 2);
		}else{
			displayText1 = "BT: waiting ";	
			LCD.drawString(displayText1, 8 - displayText1.length() / 2, 0);
			displayText2 = (".......".substring(0,loading*2+1));	
			LCD.drawString(displayText2, 8 - displayText2.length() / 2, 1);
						
			displayText3 = "SELECT GAME";
			LCD.drawString(displayText3, 8 - displayText3.length() / 2, 4);
			
			switch(selectManualLevel){			
			case GAME_SEGWAY:
				displayText4 = "SEGWAY >";
				break;
			case GAME_EMPTY:
				displayText4 = "< EMPTY ";
				break;
			}			
			LCD.drawString(displayText4, 8 - displayText4.length() / 2, 5);
			
			displayText5 = selectManualLevel + " / " + (NUMBER_LEVELS);			
			LCD.drawString(displayText5, 8 - displayText5.length() / 2, 6);
		}

		
	}	

	/**
	 * Performs a special command, defined via constants and also delivers the
	 * needed parameters from LCP
	 * 
	 * @param commandNr
	 *            the index of the command
	 * @param parameter
	 *            the LCP message array
	 */
	public void performCommand(int command, byte[] parameter) {
		try {
			LCD.drawString("PERFORM : " + command, 2, 3);
			LCD.drawString("OUT 2: " + parameter[2], 2, 4);
			LCD.drawString("OUT 3: " + parameter[3], 2, 5);
			LCD.drawString("OUT 4: " + parameter[4], 2, 6);			
			LCD.refresh();
		} catch (Exception e) {

		}
		switch (command) {

			case BTConnector.GAME_TYPE:
				gameType = parameter[2];
				createGame();
				break;
				
			case BTConnector.ROBOT_TYPE:
				robotType = parameter[2];
				break;
					
			case BTConnector.OUTPUT_COMMAND://accelerometer
			case BTConnector.ACTION_COMMAND:
				if (game != null) 
					game.readInstructions(command, parameter);
				break;					
	
			case BTConnector.DAEMON_1:
				// Display some nice effects
				LCD.setAutoRefresh(false);
				LCD.clear();
				int loading = 0;
				while (true) {
					
					if ((game == null) || ((game != null) && (game.getShowText())) ){									
						displayText(loading++);
						LCD.refresh();
						if (LMDutils.interruptedSleep(750))
							break;
						LCD.clear();
						if (loading > 3)
							loading = 0;
					}
				}
				break;
	
			case BTConnector.DAEMON_2:
				// the main game actions: announcing, waiting for actions, playing
				while (true) {
					if (game != null){
						game.performInstructions();
					
						if (LMDutils.interruptedSleep(game.getRefreshInterval()))
							break;
					}else{
						if (LMDutils.interruptedSleep(50))
							break;
					}
				}
				break;
	
			default:
				break;
	
		}
	}

	private static void createGame() {		
		
		switch (gameType) {
			case BTControls.PROGRAM_MOVE_DIRECTION:
			case BTControls.PROGRAM_MOVE_OPEN_CV_COLOR:
				game = new GameMoveDirection();
				break;
			case BTControls.PROGRAM_MOVE_MOTOR:
				game = new GameMoveMotor();
				break;
			case BTControls.PROGRAM_MOVE_ACCELEROMETER:
				game = new GameMoveAccelerometer();
				break;
			case BTControls.PROGRAM_READ_LINE:	
				game = new GameReadLine();
				break;
			case BTControls.PROGRAM_PRINTER_TEST:
				game = new GamePrinterTest();
				break;
			//TODO	
//			case BTControls.PROGRAM_PRINTER_FOTO:
//				game = new GamePrinterFoto();
//				break;
				
			case BTControls.PROGRAM_TESTING:
				game = new GameTesting();
				break;
			case BTControls.PROGRAM_SEGWAY:
				game = new GameSegway();
				break;
			default:
				break;
		}
		if (game != null)
			game.setMain(lcpThread.getCommandPerformer());
	}

}
