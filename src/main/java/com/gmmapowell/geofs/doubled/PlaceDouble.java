package com.gmmapowell.geofs.doubled;

import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.Writer;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.exceptions.FileStreamingException;
import com.gmmapowell.geofs.listeners.BinaryBlockListener;
import com.gmmapowell.geofs.listeners.CharBlockListener;
import com.gmmapowell.geofs.listeners.LineListener;
import com.gmmapowell.geofs.listeners.NumberedLineListener;

public abstract class PlaceDouble implements Place {

	// I think this is fairly easy to implement - you just pass it to the constructor
	@Override
	public Region region() {
		throw new NotImplementedException();
	}
	
	@Override
	public String name() {
		throw new NotImplementedException();
	}

	@Override
	public void lines(LineListener lsnr) {
		streamLines(lsnr, null);
	}

	@Override
	public void lines(NumberedLineListener lsnr) {
		streamLines(null, lsnr);
	}
	
	private void streamLines(LineListener lsnr, NumberedLineListener nlsnr) {
		try (LineNumberReader lnr = new LineNumberReader(textContents())) {
			String s;
			while ((s = lnr.readLine()) != null) {
				if (s.endsWith("\r"))
					s = s.substring(0, s.length()-1);
				if (lsnr != null)
					lsnr.line(s);
				else
					nlsnr.line(lnr.getLineNumber(), s);
			}
			if (lsnr != null)
				lsnr.complete();
			else
				nlsnr.complete();
		} catch (IOException ex) {
			throw new FileStreamingException(ex);
		}
	}

	@Override
	public void binary(BinaryBlockListener lsnr) {
		try (InputStream is = binaryContents()) {
			byte[] bs = new byte[4096];
			int cnt;
			while ((cnt = is.read(bs, 0, 4096)) > 0) {
				lsnr.block(bs, cnt);
			}
		} catch (IOException ex) {
			throw new FileStreamingException(ex);
		}
	}

	@Override
	public void chars(CharBlockListener lsnr) {
		throw new NotImplementedException();
	}

	@Override
	public Writer writer() {
		throw new NotImplementedException();
	}

	protected abstract Reader textContents();
	protected abstract InputStream binaryContents();

}
