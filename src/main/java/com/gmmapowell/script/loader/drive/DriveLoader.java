package com.gmmapowell.script.loader.drive;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.gmmapowell.script.FilesToProcess;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.loader.Loader;
import com.gmmapowell.script.loader.drive.Index.Status;

public class DriveLoader implements Loader {
	private final String folder;
	private final Place indexFile;
	private final Region downloads;
	private final boolean debug;
	private File webeditFile;
	private String wetitle;
	private final World gdw;

	public DriveLoader(World gdw, Region root, Region downloads, Place indexFile, String folder, boolean debug) throws ConfigException {
		this.gdw = gdw;
		this.folder = folder;
		this.indexFile = indexFile;
		this.downloads = downloads;
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

		
		Region dl = gdw.regionPath(folder);
        if (debug) {
        	System.out.println("Downloading files from Google ...");
        	System.out.println("  + " + dl /* item.folder + " (" + item.id + ")" */);
        }
        Index currentIndex = Index.read(indexFile, downloads);
        try {
	        downloadFolder(currentIndex, downloads, "    ", dl);
	        if (webeditFile != null)
	        	currentIndex.generateWebeditFile(webeditFile, wetitle);
	        return currentIndex;
        } finally {
        	currentIndex.close();
        }
	}

	/*
	private Item findFolder() throws IOException, ConfigException {
		List<String> segments = folderSegments();
        Item item = findTopFolder(segments.remove(0));
        while (!segments.isEmpty()) {
        	item = findNestedFolder(item, segments.remove(0));
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
	 */

	/*
	// TODO: I think both of these are replaced by "region"
	private Item findTopFolder(String f) throws IOException, ConfigException {
		if (debug) {
			System.out.println("Loading root folder " + f);
		}
		FileList result = service.files().list().setQ("name='" + f + "'").execute();
        if (result.getFiles().size() != 1)
        	throw new ConfigException("Could not find root folder: " + f);
        String id = result.getFiles().get(0).getId();
		return new Item(id, f);
	}

	private Item findNestedFolder(Item item, String name) throws IOException, ConfigException {
		if (debug) {
			System.out.println("Loading nested folder " + name + " in " + item.id);
		}
		FileList children = service.files().list().setQ("'" + item.id + "' in parents and name='" + name + "'").execute();
        if (children.getFiles().size() != 1)
        	throw new ConfigException("Could not find nested folder: " + name);
		return new Item(children.getFiles().get(0).getId(), name);
	}
	*/

	private void downloadFolder(Index index, Region downloads, String ind, Region dlFrom) {
		// TODO: downloads.list()
		// FileList children = service.files().list().setQ("'" + item.id + "' in parents").setFields("files(id, name, mimeType)").execute();
		dlFrom.places(place -> {
			try {
	        	Place local = downloads.place(place.name() + ".txt");
	        	// TODO: we probably want some kind of utility method to get the google ID
	        	// so that we are not foolishly casting things ...
	        	Status record = index.record(GeoFSUtils.getGoogleID(local), place);
	//        	if (debug)
	//        		System.out.printf("%s%s %s%s (%s)\n", ind, record.flag(), isFolder?"+ ":"", f.getName(), f.getId());
	        	if (record != Status.EXCLUDED)
	        		; // TODO: copy this down easily
	//        		service.files().export(f.getId(), "text/plain").executeMediaAndDownloadTo(GeoFSUtils.saveStreamTo(name));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		dlFrom.regions(region -> {
        	Region folderInto = downloads.ensureSubregion(region.name());
			downloadFolder(index, folderInto, ind+ "  ", region);
		});
//        List<com.google.api.services.drive.model.File> files = children.getFiles();
//        Collections.reverse(files);
//        for (com.google.api.services.drive.model.File f : files) {
//        	boolean isFolder = f.getMimeType().equals("application/vnd.google-apps.folder");
//            if (isFolder) {
//            	Region folderInto = downloads.ensureSubregion(f.getName());
//				downloadFolder(index, folderInto, ind+ "  ", new Item(f.getId(), f.getName()));
//            } else {
//            	Place name = downloads.place(f.getName() + ".txt");
//            	Status record = index.record(f.getId(), name);
//            	if (debug)
//            		System.out.printf("%s%s %s%s (%s)\n", ind, record.flag(), isFolder?"+ ":"", f.getName(), f.getId());
//            	if (record != Status.EXCLUDED)
//            		service.files().export(f.getId(), "text/plain").executeMediaAndDownloadTo(GeoFSUtils.saveStreamTo(name));
//            }
//        }
	}
}
