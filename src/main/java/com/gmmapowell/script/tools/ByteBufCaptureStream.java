package com.gmmapowell.script.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteBufCaptureStream extends InputStream {
	private final InputStream input;
	private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private int offset = 0;

	public ByteBufCaptureStream(InputStream input) {
		this.input = input;
	}

	@Override
	public int read() throws IOException {
		int b = input.read();
		baos.write(b);
		return b;
	}

	public int offset() {
		return offset;
	}
	
	public byte[] buf() {
		byte[] ret = baos.toByteArray();
		offset += ret.length;
		baos.reset();
		return ret;
	}
	
	@Override
	public void close() throws IOException {
		input.close();
	}
}
