package com.gmmapowell.geofs.gdw;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.exceptions.GeoFSException;
import com.gmmapowell.geofs.listeners.PlaceListener;
import com.gmmapowell.geofs.listeners.RegionListener;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

public class GDWRegion implements Region {
	protected final Drive service;
	protected final String regionId;

	public GDWRegion(Drive service, String regionId) {
		this.service = service;
		this.regionId = regionId;
	}

	@Override
	public Region subregion(String name) {
		throw new NotImplementedException();
	}

	@Override
	public Place place(String name) {
		try {
			FileList result = service.files().list().setQ("name='" + name + "' and '" + regionId + "' in parents and mimeType != 'application/vnd.google-apps.folder'").execute();
	        if (result.getFiles().size() != 1)
	        	throw new GeoFSException("Could not find root folder: " + name);
	        String id = result.getFiles().get(0).getId();
			return new GDWPlace(service, id);
		} catch (Exception ex) {
			throw new GeoFSException(ex);
		}
	}

	@Override
	public String name() {
		throw new NotImplementedException();
	}

	@Override
	public Region ensureSubregion(String name) {
		throw new NotImplementedException();
	}

	@Override
	public void places(PlaceListener lsnr) {
		throw new NotImplementedException();
	}

	@Override
	public void regions(RegionListener lsnr) {
		throw new NotImplementedException();
	}

	@Override
	public Region regionPath(String path) {
		throw new NotImplementedException();
	}

	@Override
	public Place placePath(String path) {
		throw new NotImplementedException();
	}

	@Override
	public Region parent() {
		throw new NotImplementedException();
	}
}
