package com.gmmapowell.geofs.lfs;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Writer;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;
import org.zinutils.utils.FileUtils;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.exceptions.FileStreamingException;
import com.gmmapowell.geofs.exceptions.GeoFSException;
import com.gmmapowell.geofs.exceptions.GeoFSNoPlaceException;
import com.gmmapowell.geofs.listeners.BinaryBlockListener;
import com.gmmapowell.geofs.listeners.CharBlockListener;
import com.gmmapowell.geofs.listeners.LineListener;
import com.gmmapowell.geofs.listeners.NumberedLineListener;

public class LFSPlace implements Place {
	private final LocalFileSystem world;
	protected final File file;

	public LFSPlace(LocalFileSystem world, File file) {
		this.world = world;
		if (!file.isFile())
			throw new CantHappenException("there is no file " + file);
		this.file = file;
	}

	protected LFSPlace(LocalFileSystem world, File file, boolean notExists) {
		this.world = world;
		if (!notExists)
			throw new CantHappenException("this constructor must be called with notExists = true");
		if (file.exists())
			throw new CantHappenException("the file exists (may be directory): " + file);
		this.file = file;
	}

	@Override
	public Region region() {
		return new LFSRegion(world, file.getParentFile());
	}
	
	@Override
	public String name() {
		return file.getName();
	}
	
	@Override
	public String read() {
		if (!file.exists())
			throw new GeoFSNoPlaceException(file.getPath());
		return FileUtils.readFile(file);
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
	public Writer writer() {
		if (!file.exists()) {
			createFile();
		}
		try {
			return new FileWriter(file);
		} catch (Exception ex) {
			throw new GeoFSException(ex);
		}
	}

	@Override
	public Writer appender() {
		if (!file.exists()) {
			createFile();
		}
		try {
			return new FileWriter(file, true);
		} catch (Exception ex) {
			throw new GeoFSException(ex);
		}
	}

	@Override
	public void store(String contents) {
		throw new NotImplementedException();
	}
	
	@Override
	public void copyTo(Place to) {
		throw new NotImplementedException();
	}

	@Override
	public void binary(BinaryBlockListener lsnr) {
		throw new NotImplementedException();
	}

	@Override
	public void chars(CharBlockListener lsnr) {
		throw new NotImplementedException();
	}

	public File getFile() {
		return file;
	}

	@Override
	public boolean exists() {
		return file.exists();
	}

	protected void createFile() {
		throw new GeoFSNoPlaceException(file.getPath());
	}
	
	@Override
	public String toString() {
		return file.toString();
	}
}
