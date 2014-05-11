package svb.nxt.robot.logic;


/**
 * This class provides simple utility tasks.
 */
public class LMDutils {
    /**
      * Waits the given amount in ms and returns in the case
      * of interruption.
      * @param time the time to sleep in ms
      * @return true, if the method is interrupted
      */
    public static boolean interruptedSleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {        	
            return true;
        }
        return false;
    }
}

