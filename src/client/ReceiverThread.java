package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import server.ServerMessage;
import server.Song;

public class ReceiverThread extends Thread {

	// CONSTANTS - Error Messages
	private static final String ERROR_RECEIVE = "ERROR: Could not read incoming message";
	
	// CONSTANTS - Messages
	private static final String MSG_COMPLETE = "SONG COMPLETE: ";
	private static final String MSG_NEW_SONG = "New song incoming: ";
	
	// DATA MEMBERS
	private PlayerEngine parent;
	private DataInputStream input;
	private ArrayList<Song> incompleteSongs;
	private boolean active;
	
	/**
	 * constructor for a ReceiverThread
	 * 
	 * @param engine engine to which this thread belongs
	 * @param in input stream from socket
	 */
	public ReceiverThread(PlayerEngine engine, InputStream in)
	{
		parent = engine;
		input = new DataInputStream(in);
		incompleteSongs = new ArrayList<Song>();
		active = false;
	}
	
	/**
	 * listen for incoming data
	 */
	public void run()
	{
		active = true;
		while(active)
		{
			try
			{
				ServerMessage message = ServerMessage.createServerMessage(input.readUTF(), null);
				if(message.getMessageType() == ServerMessage.MESSAGE_SONG_INFO) // get ready to receive song
				{
					Song newSong = new Song(message.getFilename(), null, message.getNumBytes(), message.getStartFrame());
					incompleteSongs.add(newSong);
					System.out.println(MSG_NEW_SONG + message.getFilename());
					
					// Read in song bytes
					byte[] bytes = new byte[message.getNumBytes()];
					int curr = 0;
					int bytesRead;
					do
					{
						bytesRead = input.read(bytes, curr, bytes.length - curr);
						if(bytesRead >= 0)
							curr += bytesRead;
					} while((bytesRead > -1) && (curr < bytes.length));
					newSong.setBytes(bytes);
					
					songCompleted(newSong);
				}
			}
			catch(IOException e)
			{
				System.err.println(ERROR_RECEIVE);
			}
		}
	}
	
	/**
	 * turn a song into a file, then give it to the engine
	 * 
	 * @param song complete song
	 */
	private void songCompleted(Song song)
	{
		System.out.println(MSG_COMPLETE + song.getFilename());
		parent.songReceived(song);
		incompleteSongs.remove(song);
	}
}
