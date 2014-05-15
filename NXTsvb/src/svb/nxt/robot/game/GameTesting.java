package svb.nxt.robot.game;

import svb.nxt.robot.MainGame;
import svb.nxt.robot.logic.CommandPerformer;


public class GameTesting extends GameTemplate {
	
	
	@Override
	public void setMain(CommandPerformer commandPerformer) {
		this.mainGame = (MainGame) commandPerformer;
		
	}

	@Override
	public void readInstructions(int command, byte[] parameter) {
		
		switch (parameter[2]) {		
		//case :		
		//	break;
		
		}
		
	}

	@Override
	public void performInstructions() {
		
	}

	@Override
	public void onDestroy() {
		
	}

	@Override
	public void buttonPressed(int btnID) {
		// TODO Auto-generated method stub
		
	}
	
}
