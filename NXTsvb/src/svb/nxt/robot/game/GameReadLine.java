package svb.nxt.robot.game;

import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import svb.nxt.robot.MainGame;
import svb.nxt.robot.bt.BTControls;
import svb.nxt.robot.logic.CommandPerformer;

/** 
 * class for robot type: Tribot
 * @author svab
 *
 */
public class GameReadLine extends GameTemplate {

	public int BLACK_CONTSTANT = 400;	
	public int STATIC_RADIUS = 10;
				
	LightSensor light;
	public boolean power = false;
		
	public int ground = 0;
	public int lastLightValue = -1;
	public int radius = STATIC_RADIUS;
	
	
	public boolean is_black = true;
	public boolean switcher = false;
	
	
	@Override
	public void setMain(CommandPerformer commandPerformer) {
		this.mainGame = (MainGame) commandPerformer;
		light = new LightSensor(SensorPort.S3);
		light.setFloodlight(false);
	}

	@Override
	public void readInstructions(int command, byte[] parameter) {
		switch (parameter[2]) {
					
			case BTControls.POWER:
				power = !power;
				if (power){				
					light.setFloodlight(true);
					setShowText(false);
				}else if (!power){
					setShowText(true);					
					light.setFloodlight(false);
//					light = null;
					
				}
				break;							
				
			case BTControls.LIGHT_SET_MAX:
				BLACK_CONTSTANT = parameter[3] * 10;				
				break;
		}
		
	}

	@Override
	public void performInstructions() {
		
		if((power) && (light != null)) {
			
			LCD.clear();
			LCD.drawString("Limit: " + BLACK_CONTSTANT, 0 ,0); 
		    LCD.drawString("Actual: " + light.getNormalizedLightValue(), 0, 1);
		    		    
	    	if ((lastLightValue + light.getNormalizedLightValue())/2 <= BLACK_CONTSTANT){
	    		if (!is_black && (ground > STATIC_RADIUS)){	    			
	    			switcher = !switcher;
	    			radius = STATIC_RADIUS;
	    		}
	    		is_black = true;
	    		LCD.drawString("State: BLACK", 0, 2);
	    	}else{	    		
	    		is_black = false;
	    		LCD.drawString("State: GROUND", 0, 2);
	    	}
	    	LCD.drawString("ground: " + ground, 0, 4);
	    	lastLightValue = light.getNormalizedLightValue();	    			    	    		    		    		    
		    LCD.refresh();
		      		     
		    // black 
			if (is_black){
				ground = 0;
				Motor.B.setSpeed(70);
				Motor.C.setSpeed(70);
				Motor.B.forward();
				Motor.C.forward();
				
			// ground	
			}else{				
				ground++;
				Motor.B.setSpeed(50);
				Motor.C.setSpeed(50);
				if(switcher){
					if (ground < radius){
						Motor.B.forward();
						Motor.C.backward();
					}else{
						Motor.B.backward();					
						Motor.C.forward();
					}
				}else{
					if (ground < radius){
						Motor.B.backward();					
						Motor.C.forward();
					}else{
						Motor.B.forward();
						Motor.C.backward();						
					}
				}
				if (ground > 3*radius){
					ground = ground % 3*radius;
					radius += STATIC_RADIUS; 
				}
				
			}
		}		
		
	}

	@Override
	public void onDestroy() {
		Motor.B.stop();
		Motor.C.stop();	
		setShowText(true);
		light.setFloodlight(false);
		light = null;
	}
	
}
