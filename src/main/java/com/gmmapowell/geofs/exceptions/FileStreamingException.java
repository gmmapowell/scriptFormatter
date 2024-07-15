package com.gmmapowell.geofs.exceptions;

import java.io.IOException;

@SuppressWarnings("serial")
public class FileStreamingException extends RuntimeException {

	public FileStreamingException(IOException cause) {
		super("error reading place", cause);
	}

}
