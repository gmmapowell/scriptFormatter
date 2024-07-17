package com.gmmapowell.geofs.gdw;

import java.io.IOException;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

public class GDWRootRegion extends GDWRegion {

	public GDWRootRegion(Drive service) throws IOException {
		super(service, findRootId(service));
	}

	private static String findRootId(Drive service) throws IOException {
		File execute = service.files().get("root").execute();
		return execute.getId();
	}
}
