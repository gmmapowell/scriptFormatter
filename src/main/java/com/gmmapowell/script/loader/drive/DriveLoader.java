package com.gmmapowell.script.loader.drive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import com.gmmapowell.script.FilesToProcess;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.loader.Loader;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.FileList;

public class DriveLoader implements Loader {
	private final File creds;
	private final String folder;
	private final File indexFile;
	private final File downloads;
	private final boolean debug;

	public DriveLoader(File root, String creds, String folder, String index, String downloads, boolean debug) throws ConfigException {
		this.creds = new File(creds);
		this.folder = folder;
		File i = new File(index);
		if (i.isAbsolute())
			this.indexFile = i;
		else
			this.indexFile = new File(root, index);
		File d = new File(downloads);
		if (d.isAbsolute())
			this.downloads = d;
		else
			this.downloads = new File(root, downloads);
		if (!this.downloads.exists()) {
			if (!this.downloads.mkdir())
				throw new ConfigException("Could not create " + this.downloads);
		} else if (!this.downloads.isDirectory())
			throw new ConfigException(this.downloads + " is not a directory");
		this.debug = debug;
	}

	@Override
	public FilesToProcess updateIndex() throws IOException, GeneralSecurityException {
		// When we download, if there is anything not in the index add it to the end
		// Reverse the format so that it is <ID> <path>
		// Use full path (from this.folder at least) for every file rather than depending on hierarchy
		// Want a "marker" in the file that says "--excluded--"   
		// because it has been downloaded but not user "approved" or ordered
		Drive service = connectToGoogleDrive();
        Item item = findTopFolder(service);
        Index currentIndex = readIndex(item.folder);
        try {
        	// TODO: this needs to be here!  Just optimizing test speeds
//	        downloadFolder(service, currentIndex, downloads, "    ", item.id);
	        return currentIndex;
        } finally {
        	currentIndex.close();
        }
	}
	private Drive connectToGoogleDrive() throws IOException, GeneralSecurityException {
		Credential cred = getCredential();
        Drive service = new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), cred)
                .setApplicationName("ScriptFormatter")
                .build();
		return service;
	}

	private Item findTopFolder(Drive service) throws IOException {
		FileList result = service.files().list().setQ("name='" + folder + "'").execute();
        if (result.getFiles().size() != 1)
        	throw new RuntimeException("Could not find root folder");
        String id = result.getFiles().get(0).getId();
        if (debug) {
        	System.out.println("Downloading files from Google ...");
        	System.out.println("  + " + folder + " (" + id + ")");
        }
		return new Item(id, folder);
	}

	private Index readIndex(String title) throws IOException {
		// TODO: read the current index & build a hashmap
		// Index also wants to hold the set of files that are included, in order.
		//
		Index index = new Index(downloads, title);
		try (FileReader fr = new FileReader(indexFile)) {
			index.readFrom(fr);
		}
		
		FileWriter fw = new FileWriter(indexFile, true);
		index.appendTo(fw);
		return index;
	}

	private void downloadFolder(Drive service, Index index, File into, String ind, String id) throws IOException {
		FileList children = service.files().list().setQ("'" + id + "' in parents").setFields("files(id, name, mimeType)").execute();
        List<com.google.api.services.drive.model.File> files = children.getFiles();
        Collections.reverse(files);
        for (com.google.api.services.drive.model.File f : files) {
        	boolean isFolder = f.getMimeType().equals("application/vnd.google-apps.folder");
        	if (debug)
        		System.out.printf("%s%s%s (%s)\n", ind, isFolder?"+ ":"", f.getName(), f.getId());
            if (isFolder) {
            	File folderInto = new java.io.File(into, f.getName());
				folderInto.mkdir();
				downloadFolder(service, index, folderInto, ind+ "  ", f.getId());
            } else {
            	java.io.File name = new java.io.File(into, f.getName() + ".txt");
            	index.record(f.getId(), name);
				service.files().export(f.getId(), "text/plain").executeMediaAndDownloadTo(new FileOutputStream(name));
            }
        }
	}

	private Credential getCredential() throws IOException, GeneralSecurityException {
		GoogleClientSecrets secrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new FileReader(creds));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), secrets, DriveScopes.all())
                .setDataStoreFactory(new FileDataStoreFactory(new File(creds.getParentFile(), "google_scriptformatter_tokens")))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

}
