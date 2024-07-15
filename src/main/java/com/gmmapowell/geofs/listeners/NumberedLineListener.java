package com.gmmapowell.geofs.listeners;

/** Listen to the contents of a text file one line at a time.
 * All newline information (\r and \n) will have been removed from the end of the line, but it will not be otherwise trimmed.
 */
public interface NumberedLineListener {

	/** Receive a line from a file and process it.
	 * @param number the line number
	 * @param line the next line from a file without any end-of-line characters
	 */
	void line(int number, String line);

}
