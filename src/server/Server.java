package server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;

import javazoom.jl.player.advanced.AdvancedPlayer;

public class Server {

	// CONSTANTS - Meta
	private static final String META_VERSION = "Alpha 0.3.2";
	
	// CONSTANTS - Messages
	private static final String MSG_PLAYBACK = "NOW PLAYING: ";
	private static final String MSG_SONG_ENQUEUED = "SONG ENQUEUED: ";
	
	// CONSTANTS - Playback
	private static final int PLAYBACK_START_FRAME = 0;
	
	// CONSTANTS - Ports
	private static final int PORT_DEFAULT = 4444;
	
	// DATA MEMBERS
	private ArrayList<ServerThread> clientThreads;
	private boolean active;
	ArrayList<Song> queue;
	private ServerDJ dj;
	private boolean playing;
	
	/**
	 * constructor for the Server class, taking a port number
	 * 
	 * @param port port on which to host the server
	 */
	public Server(int port) throws IOException
	{
		playing = false;
		clientThreads = new ArrayList<ServerThread>();
		queue = new ArrayList<Song>();
		
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("Socket opened on port " + port);
		System.out.println("Hostname: " + InetAddress.getLocalHost());
		
		active = true;
		while(active)
		{
			ServerThread thread = new ServerThread(serverSocket.accept(), this);
			System.out.println("New connection: " + thread.getIPAddress());
			thread.start();
			clientThreads.add(thread);
			
			if(!queue.isEmpty())
				sendAllSongsToClient(thread);
		}
		
		for(int i = 0; i < clientThreads.size(); i++)
			clientThreads.get(i).kill();
		
		serverSocket.close();
	}
	
	/**
	 * removes a thread handling a client from the list of threads
	 * 
	 * @param client thread handling the client demanding removal
	 */
	public void removeClient(ServerThread client)
	{
		clientThreads.remove(client);
	}
	
	/**
	 * adds a song to the upcoming songs queue
	 * 
	 * @param song completed song to be played
	 */
	public void enqueueSong(Song song)
	{
		queue.add(song);
		System.out.println(MSG_SONG_ENQUEUED + song.getFilename());
		
		// If nothing is playing, start playing
		if(!playing)
		{
			playing = true;
			startNextSong();
		}
		
		sendSongToAllClients(song);
	}
	
	/**
	 * send a song to all connected clients
	 * 
	 * @param song song to be sent
	 */
	private void sendSongToAllClients(Song song)
	{
		for(int i = 0; i < clientThreads.size(); i++)
			sendSongToClient(clientThreads.get(i), song, PLAYBACK_START_FRAME);
	}
	
	/**
	 * send a song to a single client
	 * 
	 * @param client thread handling receiving client
	 * @param song song to be sent
	 * @param offset starting frame of the song (<b>0</b> if playing from start)
	 */
	private void sendSongToClient(ServerThread client, Song song, int offset)
	{
		System.out.println("Sending " + song.getFilename() + " to " + client.getIPAddress());
		
		// Send info message
		client.sendMessage(ServerMessage.createSongInfoMessage(song.getFilename(), song.getBytes().length, offset));
		
		// Send all bytes of song
		client.sendSong(song);
	}
	
	/**
	 * send all enqueued songs to a specified client
	 * 
	 * @param client thread handling receiving client
	 */
	private void sendAllSongsToClient(ServerThread client)
	{
		if(!queue.isEmpty())
		{
			// Send currently playing song with offset
			int offset = 0;
			if(dj != null)
				offset = dj.getCurrentFrame();
			sendSongToClient(client, queue.get(0), offset);
			
			// Send additional songs to client
			if(queue.size() > 1)
				for(int i = 1; i < queue.size(); i++)
					sendSongToClient(client, queue.get(i), 0);
		}
	}
	
	/**
	 * starts the next song if possible
	 */
	public void startNextSong()
	{
		// Safety check - ensure queue is not empty
		if(queue.isEmpty())
		{
			playing = false;
			return;
		}
		
		// Get the next file to be played
		Song curr = queue.get(0);
		System.out.println(MSG_PLAYBACK + curr.getFilename());
		
		// Start playing the next song (if possible)
		try
		{
			AdvancedPlayer newPlayer = new AdvancedPlayer(new ByteArrayInputStream(curr.getBytes()));
			dj = new ServerDJ(this, newPlayer);
			dj.start();
		}
		catch(Exception e)
		{
			System.err.println("ERROR: COULD NOT PLAY SONG");
			startNextSong();
		}
	}
	
	/**
	 * starts the program and creates a server on PORT_DEFAULT
	 * 
	 * @param args does nothing
	 */
	public static void main(String[] args) throws IOException
	{
		System.out.println("Starting M'Lody Server Version " + META_VERSION + "\n");
		new Server(PORT_DEFAULT);
	}
}
