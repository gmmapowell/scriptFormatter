package com.gmmapowell.geofs.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.swing.text.Segment;

import org.apache.commons.io.output.WriterOutputStream;

import com.gmmapowell.geofs.listeners.LineListener;
import com.gmmapowell.geofs.listeners.NumberedLineListener;

public class LineListenerOutputStream extends Writer {
	private final LineListener lsnr;
	private final NumberedLineListener nlsnr;
	private final StringBuilder buffer = new StringBuilder();
	private int lno = 0;

	public static OutputStream oslsnr(LineListener lsnr) {
		WriterOutputStream wos = new WriterOutputStream(new LineListenerOutputStream(lsnr), "UTF-8");
		return wos;
	}

	public static OutputStream oslsnr(NumberedLineListener lsnr) {
		WriterOutputStream wos = new WriterOutputStream(new LineListenerOutputStream(lsnr), "UTF-8");
		return wos;
	}
	
	public LineListenerOutputStream(LineListener lsnr) {
		this.lsnr = lsnr;
		this.nlsnr = null;
	}

	public LineListenerOutputStream(NumberedLineListener lsnr) {
		this.nlsnr = lsnr;
		this.lsnr = null;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		Segment sg = new Segment(cbuf, off, len);
		int from = 0;
		for (int i=0;i<sg.length();i++) {
//			System.out.println("char " + i + " of " + sg + " => " + Character.codePointAt(sg, i));
			if (sg.charAt(i) == '\n') {
				int to = i;
				if (to > 0 && sg.charAt(to-1) == '\r')
					to--;
//				System.out.println("have |" + sg.subSequence(from, to) + "|");
				if (buffer.length() > 0) {
					buffer.append(sg.subSequence(from, to));
					line(buffer.toString());
					buffer.setLength(0);
				} else {
					line(sg.subSequence(from, to).toString());
				}
				from = i+1;
			}
		}
		if (from < sg.length())
			buffer.append(sg.subSequence(from, sg.length()).toString());
	}

	@Override
	public void flush() throws IOException {
		// because we do all the work directly in write(), flush() does not do anything
		// we flush unfinished lines only in complete
	}

	@Override
	public void close() throws IOException {
		if (buffer.length() > 0) {
			line(buffer.toString());
		}
		if (lsnr != null) {
			lsnr.complete();
		} else {
			nlsnr.complete();
		}
	}

	private void line(String line) {
		if (lsnr != null) {
			lsnr.line(line);
		} else {
			nlsnr.line(++lno, line);
		}
	}
}
