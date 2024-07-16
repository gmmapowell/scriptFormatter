package com.gmmapowell.geofs.lfs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.exceptions.FileStreamingException;
import com.gmmapowell.geofs.listeners.BinaryBlockListener;
import com.gmmapowell.geofs.listeners.CharBlockListener;
import com.gmmapowell.geofs.listeners.LineListener;
import com.gmmapowell.geofs.listeners.NumberedLineListener;

public class LFSPlace implements Place {
	private final File file;

	public LFSPlace(File file) {
		if (!file.isFile())
			throw new CantHappenException("there is no directory " + file);
		this.file = file;
	}

	@Override
	public Region region() {
		return new LFSRegion(file.getParentFile());
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
		try (LineNumberReader lnr = new LineNumberReader(new FileReader(file))) {
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
		throw new NotImplementedException();
	}

	@Override
	public void chars(CharBlockListener lsnr) {
		throw new NotImplementedException();
	}
}
