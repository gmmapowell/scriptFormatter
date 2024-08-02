package com.gmmapowell.geofs;

import java.io.Writer;

import com.gmmapowell.geofs.listeners.BinaryBlockListener;
import com.gmmapowell.geofs.listeners.CharBlockListener;
import com.gmmapowell.geofs.listeners.LineListener;
import com.gmmapowell.geofs.listeners.NumberedLineListener;

/** A <tt>Place</tt> corresponds to the conventional notion of a file in a filesystem, that is, something with a string of bytes.
 * 
 * However, because we generally want to be doing things in a TDA fashion (and certainly care about testing), this is a much more
 * active object than usual.
 * 
 * So instead of creating an InputStream from this object, it is your job to ask for the contents to be streamed to you, either
 * as a binary or text stream.  Alternatively, you can opt to copy it to another <tt>Region</tt>, or delete it.
 * 
 * Also, the fact that you have this object says that the corresponding file or object exists (or at least existed when
 * you obtained it).
 */
public interface Place {
	/** Read the entirety of the file as a String.
	 * 
	 * @return the String contents of the place
	 */
	String read();
	
	/** Read the file one line at a time, providing each line in turn to a listener
	 * 
	 * @param lsnr a listener expecting a String line of input
	 */
	void lines(LineListener lsnr);
	
	/** Read the file one line at a time, providing each line in turn to a listener along with the line number.
	 * 
	 * @param lsnr a listener expecting a line number and a String line of input
	 */
	void lines(NumberedLineListener lsnr);
	
	/** Read a binary file in blocks, providing block in turn to a listener.
	 * 
	 * @param lsnr a listener expecting a byte block
	 */
	void binary(BinaryBlockListener lsnr);
	
	/** Read a text file in blocks, providing block in turn to a listener.
	 * 
	 * @param lsnr a listener expecting a char[] block
	 */
	void chars(CharBlockListener lsnr);

	/** Open the place for writing, deleting any existing contents
	 * 
	 * @return a Writer to write to the file
	 */
	Writer writer();
	
	/** Open the place for writing, appending after any existing contents
	 * 
	 * @return a Writer to append to the file
	 */
	Writer appender();

	/** Store a given string in the place
	 * 
	 * @param contents - the contents to store
	 */
	void store(String contents);
	
	Region region();
	
	/** Return the name of this place in its Region 
	 * 
	 * @return the name of the Place within the Region
	 */
	String name();
	
	// TODO: more generally, we need to consider how we want to handle the difference between:
	// a) places that already exist
	// b) places that don't exist yet, but we want to create
	// c) places that may or may not exist and we want to be able to tell
	// It seems to me that subclasses may be an option - not sure
	boolean exists();
	
	/** Copy the contents of this place to another place
	 * 
	 * @param to the place to copy to
	 */
	void copyTo(Place to);
	
	// TODO: deleteMe();
	
}
