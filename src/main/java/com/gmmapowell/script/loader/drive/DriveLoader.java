package com.gmmapowell.script.loader.drive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gmmapowell.script.FilesToProcess;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.loader.Loader;
import com.gmmapowell.script.loader.drive.Index.Status;
import com.gmmapowell.script.utils.Utils;
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
	private File webeditFile;
	private String wetitle;

	public DriveLoader(File root, String creds, String folder, String index, String downloads, boolean debug) throws ConfigException {
		this.creds = new File(Utils.subenvs(creds));
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
	public void createWebeditIn(File file, String title) {
		this.webeditFile = file;
		this.wetitle = title;
	}

	@Override
	public FilesToProcess updateIndex() throws IOException, GeneralSecurityException, ConfigException {
		// When we download, if there is anything not in the index add it to the end
		// Reverse the format so that it is <ID> <path>
		// Use full path (from this.folder at least) for every file rather than depending on hierarchy
		// Want a "marker" in the file that says "--excluded--"   
		// because it has been downloaded but not user "approved" or ordered
		Drive service = connectToGoogleDrive();
		Item item = findFolder(service);
        if (debug) {
        	System.out.println("Downloading files from Google ...");
        	System.out.println("  + " + item.folder + " (" + item.id + ")");
        }
        Index currentIndex = readIndex();
        try {
//	        downloadFolder(service, currentIndex, downloads, "    ", item);
	        if (webeditFile != null)
	        	currentIndex.generateWebeditFile(webeditFile, wetitle);
	        return currentIndex;
        } finally {
        	currentIndex.close();
        }
	}

	private Item findFolder(Drive service) throws IOException, ConfigException {
		List<String> segments = folderSegments();
        Item item = findTopFolder(service, segments.remove(0));
        while (!segments.isEmpty()) {
        	item = findNestedFolder(service, item, segments.remove(0));
        }
		return item;
	}
	
	private List<String> folderSegments() {
		File f = new File(folder);
		List<String> ret = new ArrayList<>();
		while (f != null) {
			ret.add(0, f.getName());
			f = f.getParentFile();
		}
		return ret;
	}

	private Drive connectToGoogleDrive() throws IOException, GeneralSecurityException {
		Credential cred = getCredential();
        Drive service = new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), cred)
                .setApplicationName("ScriptFormatter")
                .build();
		return service;
	}

	private Item findTopFolder(Drive service, String f) throws IOException, ConfigException {
		if (debug) {
			System.out.println("Loading root folder " + f);
		}
		FileList result = service.files().list().setQ("name='" + f + "'").execute();
        if (result.getFiles().size() != 1)
        	throw new ConfigException("Could not find root folder: " + f);
        String id = result.getFiles().get(0).getId();
		return new Item(id, f);
	}

	private Item findNestedFolder(Drive service, Item item, String name) throws IOException, ConfigException {
		if (debug) {
			System.out.println("Loading nested folder " + name + " in " + item.id);
		}
		FileList children = service.files().list().setQ("'" + item.id + "' in parents and name='" + name + "'").execute();
        if (children.getFiles().size() != 1)
        	throw new ConfigException("Could not find nested folder: " + name);
		return new Item(children.getFiles().get(0).getId(), name);
	}

	private Index readIndex() throws IOException {
		Index index = new Index(downloads);
		try (FileReader fr = new FileReader(indexFile)) {
			index.readFrom(fr);
		} catch (FileNotFoundException ex) {
			System.out.println(indexFile + " not found; creating");
		}
		
		FileWriter fw = new FileWriter(indexFile, true);
		index.appendTo(fw);
		return index;
	}

	private void downloadFolder(Drive service, Index index, File into, String ind, Item item) throws IOException {
		FileList children = service.files().list().setQ("'" + item.id + "' in parents").setFields("files(id, name, mimeType)").execute();
        List<com.google.api.services.drive.model.File> files = children.getFiles();
        Collections.reverse(files);
        for (com.google.api.services.drive.model.File f : files) {
        	boolean isFolder = f.getMimeType().equals("application/vnd.google-apps.folder");
            if (isFolder) {
            	File folderInto = new java.io.File(into, f.getName());
				folderInto.mkdir();
				downloadFolder(service, index, folderInto, ind+ "  ", new Item(f.getId(), f.getName()));
            } else {
            	java.io.File name = new java.io.File(into, f.getName() + ".txt");
            	Status record = index.record(f.getId(), name);
            	if (debug)
            		System.out.printf("%s%s %s%s (%s)\n", ind, record.flag(), isFolder?"+ ":"", f.getName(), f.getId());
            	if (record != Status.EXCLUDED)
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
