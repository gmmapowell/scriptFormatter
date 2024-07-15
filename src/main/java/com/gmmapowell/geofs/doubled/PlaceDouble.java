package com.gmmapowell.geofs.doubled;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.exceptions.FileStreamingException;
import com.gmmapowell.geofs.listeners.BinaryBlockListener;
import com.gmmapowell.geofs.listeners.CharBlockListener;
import com.gmmapowell.geofs.listeners.LineListener;

public abstract class PlaceDouble implements Place {

	@Override
	public void lines(LineListener lsnr) {
		try (LineNumberReader lnr = new LineNumberReader(contents())) {
			String s;
			while ((s = lnr.readLine()) != null) {
				if (s.endsWith("\r"))
					s = s.substring(0, s.length()-1);
				lsnr.line(s);
			}
		} catch (IOException ex) {
			throw new FileStreamingException(ex);
		}
	}

	@Override
	public void binary(BinaryBlockListener lsnr) {
		// TODO Auto-generated method stub

	}

	@Override
	public void chars(CharBlockListener lsnr) {
		// TODO Auto-generated method stub

	}

	protected abstract Reader contents();
}
