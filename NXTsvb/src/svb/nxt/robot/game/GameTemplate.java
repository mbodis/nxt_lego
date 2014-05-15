package svb.nxt.robot.game;

import svb.nxt.robot.MainGame;
import svb.nxt.robot.logic.CommandPerformer;


/** 
 * default class for various type of robot
 * @author svab
 *
 */
public abstract class GameTemplate {
	
	public static final int DEFAULT_REFRESH_INTERVAL = 50;
	public static final int SEGWAY_REFRESH_INTERVAL = 10;
	
	
	protected boolean showText = true;
	
	/** main runnable class */
	public MainGame mainGame;
	
	/** connection to main class*/
	public abstract void setMain(CommandPerformer commandPerformer);
	
	/**
	 * reading incomming instructions
	 * @param command message type
	 * @param parameter message content
	 */
	public abstract void readInstructions(int command, byte[] parameter);
	
	/**
	 * what to do after reading instruction
	 */
	public abstract void performInstructions();
	
	/**
	 * when button pressed
	 */
	public abstract void buttonPressed(int btnID);	
	
	/**
	 * show moving text in mainGame or hide when showing own info
	 * @param showText
	 */
	public void setShowText(boolean showText){
		this.showText = showText;
	}
	
	public boolean getShowText(){
		return this.showText;
	}
	
	/**
	 * default: DEFAULT_REFRESH_INTERVAL
	 */	
	public int getRefreshInterval() {
		return GameTemplate.DEFAULT_REFRESH_INTERVAL;
	}
	
	/**
	 * pri ukonceni, ak treba nieco spravit
	 */
	public abstract void onDestroy();
		
}
