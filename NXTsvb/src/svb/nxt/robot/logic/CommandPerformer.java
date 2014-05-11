package svb.nxt.robot.logic;


/**
 * This interface has to be implemented by applications
 * connecting to MINDdroid via the MINDdroidConnector, so
 * is able to call a special command.
 */
public interface CommandPerformer {
    /**
     * Performs a special command, defined via constants and
     * also delivers the needed parameters from LCP
     * @param commandNr the index of the command
     * @param parameter the LCP message array
     */
    public void performCommand(int commandNr, byte[] parameter);
}
