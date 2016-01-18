package client;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class AdvancedPlayerThread extends Thread {

	// CONSTANTS - Error Messages
	private static final String ERROR_UNPLAYABLE = "ERROR: Could not play song";
	
	// CONSTANTS - Playback
	public static final int PLAYBACK_DEFAULT_START = 0;
	private static final int PLAYBACK_END = Integer.MAX_VALUE;
	
	// DATA MEMBERS
	private PlayerEngine parent;
	private AdvancedPlayer player;
	private int startTime;
	
	/**
	 * constructor for the AdvancedPlayerThread class
	 * 
	 * @param p PlayerEngine to which this thread belongs
	 * @param pl already initialized AdvancedPlayer
	 * @param start time at which song should be started
	 */
	public AdvancedPlayerThread(PlayerEngine p, AdvancedPlayer pl, int start)
	{
		parent = p;
		player = pl;
		startTime = start;
	}
	
	@Override
	/**
	 * runs the thread, playing the song from <b>startTime</b>
	 */
	public void run()
	{
		System.out.println("NOW PLAYING FROM FRAME " + startTime); // DEBUG
		try
		{
			player.play(startTime, PLAYBACK_END);
		}
		catch(JavaLayerException jle)
		{
			System.err.println(ERROR_UNPLAYABLE);
		}
		
		parent.startNextSong();
	}
	
	/**
	 * kills the player
	 */
	public void kill()
	{
		player.close();
	}
}
