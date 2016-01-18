package client;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

/**
 * NOTE: THIS CLASS IS OBSOLETE
 * @see AdvancedPlayerThread
 */
public class PlayerThread extends Thread {

	// CONSTANTS - Error Messages
	private static final String ERROR_UNPLAYABLE = "ERROR: Could not play file";
	
	// DATA MEMBERS
	private PlayerEngine parent;
	private Player player;
	
	/**
	 * constructor for the PlayerThread class
	 * 
	 * @param p PlayerEngine to which this thread belongs
	 * @param pl already initialized Player to be played
	 */
	public PlayerThread(PlayerEngine p, Player pl)
	{
		parent = p;
		player = pl;
	}
	
	@Override
	/**
	 * runs the thread, playing the song
	 */
	public void run()
	{
		try
		{
			player.play();
		}
		catch(JavaLayerException jle)
		{
			System.err.println(ERROR_UNPLAYABLE);
		}
		
		parent.startNextSong();
	}
	
	/**
	 * returns whether or not the player's song has finished
	 * 
	 * @return <b>true</b> if song is done playing, <b>false</b> otherwise
	 */
	public boolean isComplete()
	{
		return player.isComplete();
	}
	
	/**
	 * returns the time into the song in seconds
	 * 
	 * @return how far the player is into the song in seconds
	 */
	public int getTime()
	{
		return player.getPosition() / 1000;
	}
	
	/**
	 * kills the player
	 */
	public void kill()
	{
		player.close();
	}
}
