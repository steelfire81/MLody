package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import server.Song;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.advanced.AdvancedPlayer;

public class PlayerEngine implements ActionListener {

	// CONSTANTS - Error Messages
	private static final String ERROR_CONNECTION = "ERROR: Could not connect to server";
	private static final String ERROR_FORMAT = "ERROR: File format is not usable";
	
	// CONSTANTS - Messages
	private static final String MSG_SENDING = "SENDING...";
	
	// CONSTANTS - Formats
	private static final String[] FORMATS_APPROVED = {"mp3"};
	
	// CONSTANTS - Volume
	private static final int VOLUME_MIN = 0;
	private static final int VOLUME_MAX = 30;
	private static final int VOLUME_START = ((VOLUME_MAX - VOLUME_MIN) / 2) + VOLUME_MIN;
	
	// DATA MEMBERS
	private PlayerWindow parent;
	private AdvancedPlayerThread thread;
	private ArrayList<Song> queue;
	private boolean playing;
	private Socket socket = null;
	private SenderThread sender;
	private ReceiverThread receiver;
	private JavaSoundAudioDevice device;
	private int volume;
	private File directory;
	
	/**
	 * constructor for the PlayerEngine class taking a parent window
	 * 
	 * @param p window to which this engine belongs
	 */
	public PlayerEngine(PlayerWindow p, String hostname, int port)
	{
		parent = p;
		queue = new ArrayList<Song>();
		playing = false;
		
		try
		{
			socket = new Socket(hostname, port);
			sender = new SenderThread(this, socket.getOutputStream());
			receiver = new ReceiverThread(this, socket.getInputStream());
		}
		catch(Exception e) // TODO: Make this not suck
		{
			System.err.println(ERROR_CONNECTION);
			System.exit(1);
		}
	}
	
	/**
	 * start the threads this engine has
	 */
	public void initialize()
	{
		receiver.start();
		setVolume(VOLUME_START);
	}
	
	/**
	 * handle ActionEvents from the parent window
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		
		if(source == parent.buttonAddSong)
			selectSong();
		else if(source == parent.buttonVolumeDown)
			decreaseVolume();
		else if(source == parent.buttonVolumeUp)
			increaseVolume();
	}
	
	/**
	 * select a song to add to the queue
	 */
	private void selectSong()
	{
		JFileChooser selector = new JFileChooser(directory);
		int result = selector.showOpenDialog(parent.buttonAddSong);
		if(result == JFileChooser.APPROVE_OPTION)
		{
			directory = selector.getCurrentDirectory();
			File selected = selector.getSelectedFile();
			if(isApprovedFormat(selected.getName()))
				addSong(selected);
			else
				JOptionPane.showMessageDialog(parent.buttonAddSong, ERROR_FORMAT);
		}
	}
	
	/**
	 * send a song to the server
	 * 
	 * @param song file containing song to be sent
	 */
	private void addSong(File song)
	{
		try
		{
			sender = new SenderThread(this, socket.getOutputStream());
		}
		catch(IOException e)
		{
			System.err.println(ERROR_CONNECTION);
			return;
		}
		sender.sendSong(song);
		sender.start();
	}
	
	/**
	 * notify the engine that a new song has been received
	 * 
	 * @param song newly received song
	 */
	public void songReceived(Song song)
	{
		queue.add(song);
		
		if(!playing)
		{
			playing = true;
			startNextSong();
		}
		
		updateQueueFields();
	}
	
	/**
	 * updates the queue fields in the window
	 */
	private void updateQueueFields()
	{
		for(int i = 0; i < parent.fieldQueue.length; i++)
			if(i >= queue.size())
				parent.fieldQueue[i].setText("");
			else
				parent.fieldQueue[i].setText(queue.get(i).getFilename());
	}
	
	/**
	 * check if a given filename promises a usable file format
	 * 
	 * @param filename
	 * @return <b>true</b> if filename looks like it will work,
	 * <b>false</b> otherwise
	 */
	private static boolean isApprovedFormat(String filename)
	{
		for(int i = 0; i < FORMATS_APPROVED.length; i++)
			if(filename.contains("." + FORMATS_APPROVED[i]))
				return true;
		
		return false;
	}
	
	/**
	 * update the send field with the percent sent
	 * 
	 * @param percent percent sent
	 */
	public void sendUpdate(boolean sending)
	{
		if(sending)
		{
			parent.buttonAddSong.setEnabled(false);
			parent.fieldSend.setText(MSG_SENDING);
		}
		else
		{
			parent.buttonAddSong.setEnabled(true);
			parent.fieldSend.setText("");
			
			// Kill the sender thread
			try
			{
				sender.join();
			}
			catch(InterruptedException e){}
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
			parent.fieldFilename.setText("");
			return;
		}
		
		// Get the next file to be played
		Song curr = queue.get(0);
		
		// Shift the queue
		queue.remove(0);
		
		// Update window
		parent.fieldFilename.setText(curr.getFilename());
		updateQueueFields();
		
		// Start playing the next song (if possible)
		try
		{
			device = new JavaSoundAudioDevice();
			AdvancedPlayer newPlayer = new AdvancedPlayer(new ByteArrayInputStream(curr.getBytes()), device);
			thread = new AdvancedPlayerThread(this, newPlayer, curr.getStartFrame());
			thread.start();
		}
		catch(Exception e)
		{
			System.err.println("ERROR: COULD NOT PLAY SONG");
			startNextSong();
		}
	}
	
	/**
	 * update the volume of the player
	 * 
	 * @param level volume level
	 */
	private void setVolume(int level)
	{
		if((level >= VOLUME_MIN) && (level <= VOLUME_MAX))
		{
			volume = level;
			
			try
			{
				// Update device gain
				float min = device.getMinimumGain();
				float max = device.getMaximumGain();
				float interval = (max - min) / (VOLUME_MAX - VOLUME_MIN);
				float newGain = (interval * volume) + min; // doesn't need to be stored but I like it for debug purposes
				device.setLineGain(newGain);
			}
			catch(NullPointerException npe) {} // Happens if device source is not initialized
			
			// Update volume field
			parent.fieldVolume.setText(Integer.toString(volume));
			
			// Update status of volume buttons
			parent.buttonVolumeDown.setEnabled(volume != VOLUME_MIN);
			parent.buttonVolumeUp.setEnabled(volume != VOLUME_MAX);
		}
	}
	
	/**
	 * decrease the player's volume
	 */
	private void decreaseVolume()
	{
		setVolume(volume - 1);
	}
	
	/**
	 * increase the player's volume
	 */
	private void increaseVolume()
	{
		setVolume(volume + 1);
	}
}
