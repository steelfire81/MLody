package server;

import java.net.InetAddress;
import java.util.Scanner;

public class ServerMessage {

	// CONSTANTS - Error Messages
	private static final String ERROR_BAD_MESSAGE = "ERROR: Poorly formatted message";
	
	// CONSTANTS - Message Types
	public static final int MESSAGE_SONG_INFO = 0;
	
	// DATA MEMBERS
	private int messageType;
	private InetAddress senderAddress;
	private String associatedFilename;
	private int numBytes; // used only in info message
	private int startFrame; // used only in info message
	
	/**
	 * creates a ServerMessage from a received String message
	 * 
	 * @param message message received from server
	 * @param sender IP of message sender
	 * @return interpreted ServerMessage
	 */
	public static ServerMessage createServerMessage(String message, InetAddress sender)
	{
		/* MESSAGE FORMATTING:
		 * Messages are formatted based on the message type.
		 * 
		 * Song Info Message:
		 * [MESSAGE_SONG_INFO] [number of bytes in file] [starting frame] [filename]
		 */
		
		Scanner messageScanner = new Scanner(message);
		int type = messageScanner.nextInt();
		if(type == MESSAGE_SONG_INFO)
		{
			int numBytes = messageScanner.nextInt();
			int frame = messageScanner.nextInt();
			String filename = messageScanner.nextLine().trim();
			messageScanner.close();
			
			return new ServerMessage(sender, filename, numBytes, frame);
		}
		else // Indecypherable message
		{
			System.err.println(ERROR_BAD_MESSAGE);
			messageScanner.close();
			return null;
		}
	}
	
	/**
	 * return a string that can be sent and interpreted as an info ServerMessage
	 * 
	 * @param filename associated song filename
	 * @param size number of bytes in the file
	 * @param frame starting frame of the song
	 * @return a string that can be interpreted as an info ServerMessage
	 */
	public static String createSongInfoMessage(String filename, int size, int frame)
	{
		ServerMessage theMessage = new ServerMessage(null, filename, size, frame);
		return theMessage.toString();
	}
	
	/**
	 * constructor for a song info message only
	 * 
	 * @param sender IP address of sender
	 * @param filename associated song filename
	 * @param n number of bytes in the song
	 * @param frame starting frame of the song
	 */
	private ServerMessage(InetAddress sender, String filename, int n, int frame)
	{
		messageType = MESSAGE_SONG_INFO;
		senderAddress = sender;
		associatedFilename = filename;
		numBytes = n;
		startFrame = frame;
	}
	
	@Override
	/**
	 * creates a string representation of the message
	 */
	public String toString()
	{
		if(messageType == MESSAGE_SONG_INFO)
			return MESSAGE_SONG_INFO + " " + numBytes + " " + startFrame + " " + associatedFilename;
		else // Poorly formatted message
		{
			System.err.println(ERROR_BAD_MESSAGE);
			return null;
		}
	}
	
	/**
	 * get the type of message
	 * 
	 * @return message type
	 */
	public int getMessageType()
	{
		return messageType;
	}
	
	/**
	 * get the filename
	 * 
	 * @return associated filename
	 */
	public String getFilename()
	{
		return associatedFilename;
	}
	
	/**
	 * get the number of bytes in the file
	 * 
	 * @return number of bytes in the file
	 */
	public int getNumBytes()
	{
		return numBytes;
	}
	
	/**
	 * get the sender's IP address
	 * 
	 * @return sender's IP address
	 */
	public InetAddress getSender()
	{
		return senderAddress;
	}
	
	/**
	 * get the starting frame of the contained song
	 * 
	 * @return starting frame of the contained song
	 */
	public int getStartFrame()
	{
		return startFrame;
	}
}
