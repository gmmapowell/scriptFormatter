package com.gmmapowell.geofs.gdw;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;
import org.zinutils.utils.FileUtils;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.exceptions.GeoFSException;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.DriveList;

public class GoogleDriveWorld implements World {
	private final Universe universe;
	private final String appName;
	private final Place creds;
	private Drive service;

	public GoogleDriveWorld(Universe universe, String appName, Place creds) {
		this.universe = universe;
		universe.register("google", this);
		this.appName = appName;
		this.creds = creds;
	}
	
	public void prepare() throws Exception {
		int i=0;
		Drive s = null;
		while (i<2) {
			s = connectToGoogleDrive();
			try {
				DriveList list = s.drives().list().execute();
				List<com.google.api.services.drive.model.Drive> drives = list.getDrives();
				// TODO: this is the list of "roots", I think ...
				break;
			} catch (TokenResponseException ex) {
				FileUtils.cleanDirectory(tokensdir());
			}
			i++;
		}
		if (i == 2) {
			throw new CantHappenException("could not connect to drive");
		}
		this.service = s;
	}
	
	@Override
	public Universe getUniverse() {
		return universe;
	}
	
	@Override
	public Region root() {
		try {
			return new GDWRootRegion(this.service);
		} catch (Exception ex) {
			throw new GeoFSException(ex);
		}
	}

	@Override
	public Region root(String root) {
		// This would be for shared drives ...
		throw new NotImplementedException();
	}

	@Override
	public Region regionPath(String path) {
		return GeoFSUtils.regionPath(this, null, path);
	}

	@Override
	public Region newRegionPath(String path) {
		return GeoFSUtils.newRegionPath(this, null, path);
	}

	@Override
	public Place placePath(String path) {
		return GeoFSUtils.placePath(this, null, path);
	}
	
	@Override
	public Place newPlacePath(String path) {
		return GeoFSUtils.newPlacePath(this, null, path);
	}

	@Override
	public Place ensurePlacePath(String path) {
		return GeoFSUtils.ensurePlacePath(this, null, path);
	}

	private Drive connectToGoogleDrive() throws IOException, GeneralSecurityException {
		Credential cred = getCredential();
        Drive service = new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), cred)
                .setApplicationName(appName)
                .build();
		return service;
	}

	private Credential getCredential() throws IOException, GeneralSecurityException {
//		System.out.println("Getting credential for Drive from " + creds);
		GoogleClientSecrets secrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), GeoFSUtils.fileReader(creds));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), secrets, DriveScopes.all())
                .setDataStoreFactory(new FileDataStoreFactory(tokensdir()))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8803).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	private File tokensdir() {
		return new File(GeoFSUtils.file(creds.region()), "google_scriptformatter_drive_tokens");
	}
}
