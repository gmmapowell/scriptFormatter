package com.gmmapowell.geofs.stream;

import java.io.IOException;
import java.io.OutputStream;

public class NoCloseStream extends OutputStream {
	private final OutputStream os;

	public NoCloseStream(OutputStream os) {
		this.os = os;
	}

	@Override
	public void close() throws IOException {
		// The important thing here is to do nothing
	}

	@Override
	public void flush() throws IOException {
		os.flush();
	}

	@Override
	public void write(int b) throws IOException {
		os.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		os.write(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		os.write(b);
	}
}
