package com.gmmapowell.geofs.listeners;

public interface BinaryBlockListener {

	/** Process a block of information from a binary file.  It is not necessary to process all of it;
	 * the return value indicates how many bytes have been processed.  This must however, be at least 1
	 * and less than count.
	 * 
	 * @param block a byte array which is valid from 0 to count-1
	 * @param count the number of valid bytes in the array
	 * @return the number of bytes considered processed (the remainder will be sent in a subsequent call)
	 */
	int block(byte[] block, int count);
}
