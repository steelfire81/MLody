package server;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class ServerDJ extends Thread {

	// CONSTANTS - Error Messages
	private static final String ERR_PLAYBACK = "ERROR: Could not play song ";
	
	// DATA MEMBERS
	private Server parent;
	private AdvancedPlayer playerSimulator;
	
	/**
	 * constructor for the ServerDJ class
	 * 
	 * @param parent associated Server
	 */
	public ServerDJ(Server server, AdvancedPlayer player)
	{
		parent = server;
		playerSimulator = player;
	}
	
	@Override
	/**
	 * starts the dj playing through the queue
	 */
	public void run()
	{
		try
		{
			playerSimulator.play();
		}
		catch(JavaLayerException jle)
		{
			System.err.println(ERR_PLAYBACK);
		}
		
		parent.queue.remove(0);
		parent.startNextSong();
	}
	
	/**
	 * get the current frame the player simulator is at
	 * 
	 * @return playerSimulator's current frame location, or <b>0</b>
	 * if playerSimulator is not currently active
	 */
	public int getCurrentFrame()
	{
		int frame = 0;
		if(playerSimulator != null)
			frame = playerSimulator.getFrameLocation();
		return frame;
	}
}
