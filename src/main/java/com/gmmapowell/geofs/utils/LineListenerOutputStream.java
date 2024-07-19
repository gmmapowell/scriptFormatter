package com.gmmapowell.geofs.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.apache.commons.io.output.WriterOutputStream;

import com.gmmapowell.geofs.listeners.LineListener;

public class LineListenerOutputStream extends Writer {
	private final LineListener lsnr;

	public static OutputStream oslsnr(LineListener lsnr) {
		WriterOutputStream wos = new WriterOutputStream(new LineListenerOutputStream(lsnr), "UTF-8");
		// TODO Auto-generated method stub
		return wos;
	}
	
	public LineListenerOutputStream(LineListener lsnr) {
		this.lsnr = lsnr;
	}


	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flush() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}
}
