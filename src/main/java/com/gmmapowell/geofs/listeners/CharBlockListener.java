package com.gmmapowell.geofs.listeners;

public interface CharBlockListener {

	/** Process a block of information from a text file.  It is not necessary to process all of it;
	 * the return value indicates how many characters have been processed.  This must however, be at least 1
	 * and less than count.
	 * 
	 * @param block a char array which is valid from 0 to count-1
	 * @param count the number of valid characters in the array
	 * @return the number of characters considered processed (the remainder will be sent in a subsequent call)
	 */
	int block(char[] block, int count);

}
