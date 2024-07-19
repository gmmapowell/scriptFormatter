package com.gmmapowell.geofs.listeners;

/** Listen to the contents of a text file one line at a time.
 * All newline information (\r and \n) will have been removed from the end of the line, but it will not be otherwise trimmed.
 */
public interface LineListener {

	/** Receive a line from a file and process it.
	 * @param line the next line from a file without any end-of-line characters
	 */
	void line(String line);

	/** When all the lines have been handled, inform the listener
	 * 
	 */
	default void complete() { }
}
