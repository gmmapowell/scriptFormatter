package com.gmmapowell.geofs.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.swing.text.Segment;

import org.apache.commons.io.output.WriterOutputStream;

import com.gmmapowell.geofs.listeners.LineListener;

public class LineListenerOutputStream extends Writer {
	private final LineListener lsnr;

	public static OutputStream oslsnr(LineListener lsnr) {
		WriterOutputStream wos = new WriterOutputStream(new LineListenerOutputStream(lsnr), "UTF-8");
		return wos;
	}
	
	public LineListenerOutputStream(LineListener lsnr) {
		this.lsnr = lsnr;
	}


	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		Segment sg = new Segment(cbuf, off, len);
		int from = 0;
		for (int i=0;i<sg.length();i++) {
			System.out.println("char " + i + " of " + sg + " => " + sg.charAt(i));
			if (sg.charAt(i) == '\n') {
				System.out.println("have " + sg.subSequence(from, i));
				lsnr.line(sg.subSequence(from, i).toString());
				from = i+1;
			}
		}
		if (from < sg.length())
			lsnr.line(sg.subSequence(from, sg.length()).toString());
	}

	@Override
	public void flush() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws IOException {
		lsnr.complete();
	}
}
