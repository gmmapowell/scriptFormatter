package com.gmmapowell.script.loader;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.intf.FilesToProcess;

public class ContentLoader implements Loader {
	private final String have_internet = System.getenv("HAVE_INTERNET");
	private final boolean LOAD_FROM_REMOTE = have_internet == null || ("true".equals(have_internet));
	private final String folder;
	private final Place indexFile;
	private final Region downloads;
	private final boolean debug;
	private Place webeditFile;
	private String wetitle;
	private final Universe u;
	private boolean recursive;

	public ContentLoader(Universe u, Region root, Region downloads, Place indexFile, String folder, boolean recursive, boolean debug) throws ConfigException {
		this.u = u;
		this.folder = folder;
		this.indexFile = indexFile;
		this.downloads = downloads;
		this.recursive = recursive;
		this.debug = debug;
		if (downloads == null) {
			throw new ConfigException("downloads cannot be null");
		}
	}

	@Override
	public void createWebeditIn(Place file, String title) {
		this.webeditFile = file;
		this.wetitle = title;
	}

	@Override
	public FilesToProcess updateIndex() throws IOException, GeneralSecurityException, ConfigException {
		// When we download, if there is anything not in the index add it to the end
		// Reverse the format so that it is <ID> <path>
		// Use full path (from this.folder at least) for every file rather than
		// depending on hierarchy
		// Want a "marker" in the file that says "--excluded--"
		// because it has been downloaded but not user "approved" or ordered

		Index currentIndex = Index.read(indexFile, downloads);
		try {
			if (LOAD_FROM_REMOTE) {
				Region dl = u.regionPath(folder);
				if (debug) {
					System.out.println("Downloading files from Google ...");
					System.out.println("  + " + dl /* item.folder + " (" + item.id + ")" */);
				}
				downloadFolder(currentIndex, dl, downloads, "    ", "");
			}
			if (webeditFile != null)
				currentIndex.generateWebeditFile(webeditFile, wetitle);
			return currentIndex;
		} finally {
			currentIndex.close();
		}
	}

	@Override
	public void reload(FilesToProcess files) {
		if (!LOAD_FROM_REMOTE)
			return; // we can't do the thing we've been asked to do ...
		Region dl = u.regionPath(folder);
		for (LabelledPlace p : files.included()) {
			GeoFSUtils.placePath(null, dl, p.label.replace(".txt", "")).copyTo(p.place);
		}
	}

	private void downloadFolder(Index index, Region dlFrom, Region downloads, String ind, String prefix) {
		if (downloads == null) {
			throw new CantHappenException("downloads cannot be null");
		}
		dlFrom.places(place -> {
			try {
				Place local = downloads.ensurePlace(place.name() + ".txt");
				String id = GeoFSUtils.getGoogleID(place);
				boolean download = index.record(id, doPrefix(prefix, local.name()));
				if (debug)
					System.out.printf("%s%s %s%s (%s)\n", ind, download?"+":"-", "", place.name(), id);
				if (download)
					place.copyTo(local);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		if (recursive) {
			dlFrom.regions(region -> {
				if (debug)
					System.out.printf("%s%s %s%s\n", ind, "/", "", region.name());
				Region folderInto = downloads.ensureSubregion(region.name());
				downloadFolder(index, region, folderInto, ind + "  ", doPrefix(prefix, region.name()));
			});
		}
	}
	
	private String doPrefix(String prefix, String now) {
		if (prefix.length() == 0)
			return now;
		else
			return prefix + "/" + now;
		
	}
}
