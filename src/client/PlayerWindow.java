package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PlayerWindow {

	// CONSTANTS - Meta
	private static final String META_VERSION_NUMBER = "Alpha 0.4.2";
	
	// CONSTANTS - Debug
	private static final int PORT = 4444;
	
	// CONSTANTS - Button Text
	private static final String BUTTON_ADD_TEXT = "ADD SONG";
	private static final String BUTTON_VOLUME_DOWN_TEXT = "-";
	private static final String BUTTON_VOLUME_UP_TEXT = "+";
	
	// CONSTANTS - Colors
	private static final Color BUTTON_BG_COLOR = Color.BLUE;
	private static final Color BUTTON_FG_COLOR = Color.BLACK;
	private static final Color FIELD_BG_COLOR = Color.WHITE;
	private static final Color FIELD_FG_COLOR = Color.BLACK;
	private static final Color LABEL_BG_COLOR = Color.BLACK;
	private static final Color LABEL_FG_COLOR = Color.WHITE;
	
	// CONSTANTS - Labels
	private static final String LABEL_FILENAME = "FILE:";
	private static final String LABEL_QUEUE = "UP NEXT:";
	
	// CONSTANTS - Messages
	private static final String MSG_HOST = "Please enter the server's hostname";
	
	// CONSTANTS - Queue
	private static final int QUEUE_VISIBLE_SIZE = 5;
	
	// CONSTANTS - Window Settings
	private static final String WINDOW_NAME = "M'Lody " + META_VERSION_NUMBER;
	private static final int WINDOW_WIDTH = 300;
	private static final int WINDOW_HEIGHT = 300;
	
	// WINDOW ELEMENTS
	JTextField fieldFilename;
	JTextField fieldVolume;
	JTextField[] fieldQueue;
	JTextField fieldSend;
	JButton buttonAddSong;
	JButton buttonVolumeDown;
	JButton buttonVolumeUp;
	
	/**
	 * default constructor for the PlayerWindow class
	 */
	public PlayerWindow(String hostname, int port)
	{
		// Create engine
		PlayerEngine engine = new PlayerEngine(this, hostname, port);
		
		// Initialize main panel
		JPanel panelMain = new JPanel(new BorderLayout());
		
		// Initialize song info panel
		JPanel panelInfo = new JPanel(new GridLayout(2, 2));
		// Initialize filename label
		JTextField labelFilename = new JTextField(LABEL_FILENAME);
		setLabelColors(labelFilename);
		labelFilename.setEditable(false);
		panelInfo.add(labelFilename);
		// Initialize filename field
		fieldFilename = new JTextField();
		setFieldColors(fieldFilename);
		fieldFilename.setEditable(false);
		panelInfo.add(fieldFilename);
		// Initialize volume buttons
		JPanel panelVolume = new JPanel(new GridLayout(1, 2));
		buttonVolumeDown = new JButton(BUTTON_VOLUME_DOWN_TEXT);
		setButtonColors(buttonVolumeDown);
		buttonVolumeDown.addActionListener(engine);
		panelVolume.add(buttonVolumeDown);
		buttonVolumeUp = new JButton(BUTTON_VOLUME_UP_TEXT);
		setButtonColors(buttonVolumeUp);
		buttonVolumeUp.addActionListener(engine);
		panelVolume.add(buttonVolumeUp);
		panelInfo.add(panelVolume);
		// Initialize volume field
		fieldVolume = new JTextField();
		setFieldColors(fieldVolume);
		fieldVolume.setEditable(false);
		panelInfo.add(fieldVolume);
		// Add song info panel to main panel
		panelMain.add(panelInfo, BorderLayout.NORTH);
		
		// Initialize queue panel
		JPanel panelQueue = new JPanel(new GridLayout(QUEUE_VISIBLE_SIZE + 2, 1));
		// Initialize queue label
		JTextField labelQueue = new JTextField(LABEL_QUEUE);
		setLabelColors(labelQueue);
		labelQueue.setEditable(false);
		panelQueue.add(labelQueue);
		// Initialize queue fields
		fieldQueue = new JTextField[QUEUE_VISIBLE_SIZE];
		for(int i = 0; i < fieldQueue.length; i++)
		{
			fieldQueue[i] = new JTextField();
			setFieldColors(fieldQueue[i]);
			fieldQueue[i].setEditable(false);
			panelQueue.add(fieldQueue[i]);
		}
		// Initialize add song button
		buttonAddSong = new JButton(BUTTON_ADD_TEXT);
		setButtonColors(buttonAddSong);
		buttonAddSong.addActionListener(engine);
		panelQueue.add(buttonAddSong);
		// Add queue panel to main panel
		panelMain.add(panelQueue, BorderLayout.CENTER);
		
		// Initialize send field
		fieldSend = new JTextField();
		setFieldColors(fieldSend);
		fieldSend.setEditable(false);
		panelMain.add(fieldSend, BorderLayout.SOUTH);
		
		// Make this all visible
		JFrame frame = new JFrame(WINDOW_NAME);
		frame.setContentPane(panelMain);
		frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		// Start engine
		engine.initialize();
	}
	
	/**
	 * sets the colors of a specified field
	 * 
	 * @param field field in need of color configuration
	 */
	private static void setFieldColors(JTextField field)
	{
		field.setBackground(FIELD_BG_COLOR);
		field.setForeground(FIELD_FG_COLOR);
	}
	
	/**
	 * sets the colors of a specified JButton
	 * 
	 * @param button button in need of color configuration
	 */
	private static void setButtonColors(JButton button)
	{
		button.setBackground(BUTTON_BG_COLOR);
		button.setForeground(BUTTON_FG_COLOR);
	}
	
	/**
	 * sets the colors of a specified label
	 * @param label
	 */
	private static void setLabelColors(JTextField label)
	{
		label.setBackground(LABEL_BG_COLOR);
		label.setForeground(LABEL_FG_COLOR);
	}

	/**
	 * prompt for hostname, then launch a new PlayerWindow
	 * 
	 * @param args does nothing
	 */
	public static void main(String[] args)
	{
		String host = JOptionPane.showInputDialog(null, MSG_HOST);
		if(host != null)
			new PlayerWindow(host, PORT);
	}
}