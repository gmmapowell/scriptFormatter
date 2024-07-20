package com.gmmapowell.geofs.gdw;

import java.io.IOException;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.exceptions.GeoFSException;
import com.gmmapowell.geofs.listeners.BinaryBlockListener;
import com.gmmapowell.geofs.listeners.CharBlockListener;
import com.gmmapowell.geofs.listeners.LineListener;
import com.gmmapowell.geofs.listeners.NumberedLineListener;
import com.gmmapowell.geofs.utils.LineListenerOutputStream;
import com.google.api.services.drive.Drive;

public class GDWPlace implements Place {
	private final Drive service;
	private final String id;

	public GDWPlace(Drive service, String id) {
		this.service = service;
		this.id = id;
	}

	@Override
	public String name() {
		// DO NOT RETURN THE ID
		// FIND THE ACTUAL NAME!
		throw new NotImplementedException();
	}
	
	@Override
	public void lines(LineListener lsnr) {
		try {
			service.files().export(id, "text/plain").executeMediaAndDownloadTo(LineListenerOutputStream.oslsnr(lsnr));
		} catch (IOException ex) {
			throw new GeoFSException(ex);
		}
	}

	@Override
	public void lines(NumberedLineListener lsnr) {
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

	@Override
	public Region region() {
		throw new NotImplementedException();
	}
}
