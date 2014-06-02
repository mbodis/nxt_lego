package svb.nxt.robot.logic;

import svb.nxt.robot.game.GamePenPrinter;

public class PrinterHeler {
	
	/**
	 * odstranuje prazdne mista ku koncu riadku (nuly)
	 * 1100110001100110# -> 110011000110011#
	 * 1100000000000000# -> 11#
	 */	
	public static StringBuilder removeEmptySlots(StringBuilder input){
		
		StringBuilder resBuff = new StringBuilder();
		StringBuilder temp = new StringBuilder();
		boolean found = false;
		boolean write = false;
		
		for(int i=0;i<input.toString().length();i++){
			
			temp.append(input.charAt(i));
			
			if (input.charAt(i) == '1'){
				found = true;
			}
			if (input.charAt(i) == GamePenPrinter.NEW_LINE){
				write = true;
			}
			
			if (found){
				resBuff.append(temp.toString());
				temp = new StringBuilder();
				found = false;
			}
			
			if (write){
				resBuff.append(GamePenPrinter.NEW_LINE);
				temp = new StringBuilder();
				write = false;
			}
					
		}
			
		return resBuff;
	}
}
