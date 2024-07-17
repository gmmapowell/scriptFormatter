package com.gmmapowell.geofs.gdw;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.listeners.BinaryBlockListener;
import com.gmmapowell.geofs.listeners.CharBlockListener;
import com.gmmapowell.geofs.listeners.LineListener;
import com.gmmapowell.geofs.listeners.NumberedLineListener;
import com.google.api.services.drive.Drive;

public class GDWPlace implements Place {
	public GDWPlace(Drive service, String id) {
	}

	@Override
	public void lines(LineListener lsnr) {
		throw new NotImplementedException();
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
