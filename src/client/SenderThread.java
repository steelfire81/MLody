package client;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import server.ServerMessage;

public class SenderThread extends Thread {

	// CONSTANTS - Error Messages
	private static final String ERROR_SEND = "ERROR: Could not send song";
	private static final String ERROR_ALREADY_SONG = "ERROR: Already sending a song";
	
	// CONSTANTS - Messages
	private static final String MSG_PREPARING = "PREPARING TO SEND: ";
	private static final String MSG_SENDING = "SENDING SONG: ";
	private static final String MSG_SENT = "SONG SUCCESSFULLY SENT: ";
	
	// DATA MEMBERS
	private PlayerEngine parent;
	private DataOutputStream output;
	private File songToSend;
	
	/**
	 * constructor for a SenderThread
	 * 
	 * @param engine engine to which this thread belongs
	 * @param out output stream to socket
	 */
	public SenderThread(PlayerEngine engine, OutputStream out)
	{
		parent = engine;
		output = new DataOutputStream(out);
	}
	
	/**
	 * search for songs to send, then send when available
	 */
	public void run()
	{
		try
		{
			if(songToSend != null)
			{
				System.out.println(MSG_SENDING + songToSend.getName());
				parent.sendUpdate(true);
				
				// Convert file to bytes
				byte[] bytes = new byte[(int) songToSend.length()];
				BufferedInputStream input = new BufferedInputStream(new FileInputStream(songToSend));
				input.read(bytes, 0, bytes.length);
				input.close();
				
				// Send song info message
				output.writeUTF(ServerMessage.createSongInfoMessage(songToSend.getName(), bytes.length, 0));
				output.flush();
				
				// Send all song data
				output.write(bytes, 0, bytes.length);
				output.flush();
				
				System.out.println(MSG_SENT + songToSend.getName());
				songToSend = null;
				parent.sendUpdate(false);
			}
		}
		catch(IOException e)
		{
			System.err.println(ERROR_SEND);
			songToSend = null;
			parent.sendUpdate(false);
		}
	}
	
	/**
	 * prepare to send a song
	 * 
	 * @param song the song to send
	 */
	public void sendSong(File song)
	{
		System.out.println(MSG_PREPARING + song.getName());
		
		if(songToSend == null)
			songToSend = song;
		else
			System.err.println(ERROR_ALREADY_SONG);
	}
}
