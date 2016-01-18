package server;

import java.net.InetAddress;

public class Song {

	// DATA MEMBERS
	private String filename;
	private InetAddress senderAddress;
	private byte[] bytes;
	private int numBytesAdded;
	private int startFrame;
	
	/**
	 * creates a song given the filename, sender's address, and number of bytes in the file
	 * 
	 * @param name filename
	 * @param address sender's IP address
	 * @param size number of bytes in the song
	 * @param offset starting frame of the song
	 */
	public Song(String name, InetAddress address, int size, int offset)
	{
		filename = name;
		senderAddress = address;
		bytes = new byte[size];
		numBytesAdded = 0;
		startFrame = offset;
	}
	
	/**
	 * get the filename of the song
	 * 
	 * @return song filename
	 */
	public String getFilename()
	{
		return filename;
	}
	
	/**
	 * get the IP address of the person who send this song
	 * 
	 * @return sender's address
	 */
	public InetAddress getSenderAddress()
	{
		return senderAddress;
	}
	
	/**
	 * says if this song has been completed
	 * 
	 * @return <b>true</b> if all bytes have been written, <b>false</b>
	 * otherwise
	 */
	public boolean isComplete()
	{
		return bytes.length == numBytesAdded;
	}
	
	/**
	 * get the byte array for the file
	 * 
	 * @return all bytes in the file
	 */
	public byte[] getBytes()
	{
		return bytes;
	}
	
	/**
	 * write the song's bytes
	 * 
	 * @param byteArray the song's bytes
	 */
	public void setBytes(byte[] byteArray)
	{
		bytes = byteArray;
	}
	
	/**
	 * get the starting frame of the song
	 * 
	 * @return starting frame of the song
	 */
	public int getStartFrame()
	{
		return startFrame;
	}
	
}
