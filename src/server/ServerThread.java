package server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ServerThread extends Thread {

	// CONSTANTS - Error Messages
	private static final String ERROR_MESSAGE_SEND = "ERROR: Problem sending message to client";
	private static final String ERROR_RUN = "DISCONNECT:";
	
	// CONSTANTS - Messages
	private static final String MSG_SONG_RECEIVING = "RECEIVING SONG: ";
	private static final String MSG_SONG_COMPLETE = "SONG COMPLETE: ";
	
	// CONSTANTS - Thread
	private static final String THREAD_NAME = "ServerThread";
	
	// DATA MEMBERS
	private Server parent;
	private Socket socket = null;
	private DataInputStream input;
	private DataOutputStream output;
	private boolean active;
	
	/**
	 * constructor for the server thread 
	 * @param s socket open to client
	 * @param server server to which this thread belongs
	 */
	public ServerThread(Socket s, Server server)
	{
		super(THREAD_NAME);
		parent = server;
		socket = s;
		
		active = false;
	}
	
	/**
	 * listen for messages from the client
	 */
	public void run()
	{
		try
		{
			input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			output = new DataOutputStream(socket.getOutputStream());
			
			active = true;
			while(active)
			{
				ServerMessage incomingMessage = ServerMessage.createServerMessage(input.readUTF(), getIPAddress());
				if(incomingMessage.getMessageType() == ServerMessage.MESSAGE_SONG_INFO) // Get ready to read song data
				{
					System.out.println(MSG_SONG_RECEIVING + incomingMessage.getFilename());
					Song newSong = new Song(incomingMessage.getFilename(), getIPAddress(), incomingMessage.getNumBytes(), 0);
					
					// Receive song bytes
					byte[] bytes = new byte[incomingMessage.getNumBytes()];
					int curr = 0;
					int bytesRead;
					do
					{
						bytesRead = input.read(bytes, curr, bytes.length - curr);
						if(bytesRead >= 0)
							curr += bytesRead;
					} while((bytesRead > -1) && (curr < bytes.length));
					newSong.setBytes(bytes);
					
					System.out.println(MSG_SONG_COMPLETE + newSong.getFilename());
					parent.enqueueSong(newSong);
				}
			}
		}
		catch(IOException e)
		{
			System.err.println(ERROR_RUN + getIPAddress());
			active = false;
			parent.removeClient(this);
		}
	}
	
	/**
	 * stop the thread
	 */
	public void kill()
	{
		active = false;
	}
	
	/**
	 * return the IP address of the client connected to this thread
	 * @return IP address of client this thread is handling
	 */
	public InetAddress getIPAddress()
	{
		return socket.getInetAddress();
	}
	
	/**
	 * send a message to the client handled by this thread
	 * 
	 * @param message the message (as a string)
	 */
	public void sendMessage(String message)
	{
		try
		{
			output.writeUTF(message);
			output.flush();
		}
		catch(IOException e)
		{
			System.err.println(ERROR_MESSAGE_SEND + " " + getIPAddress());
		}
	}
	
	/**
	 * send data from a song to a client
	 * 
	 * @param song song to be sent
	 */
	public void sendSong(Song song)
	{
		try
		{
			output.write(song.getBytes());
			output.flush();
		}
		catch(IOException e)
		{
			System.err.println(ERROR_MESSAGE_SEND + " " + getIPAddress());
		}
	}
	
}
