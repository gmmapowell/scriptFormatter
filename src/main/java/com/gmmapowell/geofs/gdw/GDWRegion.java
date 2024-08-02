package com.gmmapowell.geofs.gdw;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

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
	private final String name;
	private final Region parent;

	public GDWRegion(Drive service, String regionId, String name, Region parent) {
		this.service = service;
		this.regionId = regionId;
		this.name = name;
		this.parent = parent;
	}

	@Override
	public Region subregion(String name) {
		try {
			FileList children = service.files().list().setQ("'" + regionId + "' in parents and name='" + name + "'").execute();
	        if (children.getFiles().size() != 1)
	        	throw new GeoFSException("could not find region: " + name);
			return new GDWRegion(service, children.getFiles().get(0).getId(), name, this);
		} catch (IOException ex) {
			throw new GeoFSException(ex);
		}
	}

	@Override
	public Place place(String name) {
		try {
			FileList result = service.files().list().setQ("name='" + name + "' and '" + regionId + "' in parents and mimeType != 'application/vnd.google-apps.folder'").execute();
	        if (result.getFiles().size() != 1)
	        	throw new GeoFSException("Could not find root folder: " + name);
	        String id = result.getFiles().get(0).getId();
			return new GDWPlace(service, id, name, this);
		} catch (Exception ex) {
			throw new GeoFSException(ex);
		}
	}

	@Override
	public Place newPlace(String name) {
		throw new NotImplementedException();
	}

	@Override
	public Place ensurePlace(String name) {
		throw new NotImplementedException();
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Region ensureSubregion(String name) {
		throw new NotImplementedException();
	}

	@Override
	public void places(PlaceListener lsnr) {
		try {
			FileList children = service.files().list().setQ("'" + regionId + "' in parents AND mimeType != 'application/vnd.google-apps.folder'").setFields("files(id, name, mimeType)").execute();
	        List<com.google.api.services.drive.model.File> files = children.getFiles();
	        Collections.reverse(files);
	        for (com.google.api.services.drive.model.File f : files) {
	        	lsnr.place(new GDWPlace(service, f.getId(), f.getName(), this));
	        }			
		} catch (IOException ex) {
			throw new GeoFSException(ex);
		}
	}

	@Override
	public void regions(RegionListener lsnr) {
		try {
			FileList children = service.files().list().setQ("'" + regionId + "' in parents AND mimeType = 'application/vnd.google-apps.folder'").setFields("files(id, name, mimeType)").execute();
	        List<com.google.api.services.drive.model.File> files = children.getFiles();
	        Collections.reverse(files);
	        for (com.google.api.services.drive.model.File f : files) {
	        	lsnr.region(new GDWRegion(service, f.getId(), f.getName(), this));
	        }			
		} catch (IOException ex) {
			throw new GeoFSException(ex);
		}
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
		return parent;
	}
	
	@Override
	public String toString() {
		if (parent == null) {
			return "google://";
		} else
			return parent.toString() + "/" + name;
	}
}
