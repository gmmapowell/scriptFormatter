package com.gmmapowell.script.loader.drive;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.gmmapowell.script.FilesToProcess;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.loader.Loader;

public class DriveLoader implements Loader {
	private final String folder;
	private final Place indexFile;
	private final Region downloads;
	private final boolean debug;
	private Place webeditFile;
	private String wetitle;
	private final World gdw;

	public DriveLoader(World gdw, Region root, Region downloads, Place indexFile, String folder, boolean debug)
			throws ConfigException {
		this.gdw = gdw;
		this.folder = folder;
		this.indexFile = indexFile;
		this.downloads = downloads;
		this.debug = debug;
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

	private void downloadFolder(Index index, Region downloads, String ind, Region dlFrom) {
		dlFrom.places(place -> {
			try {
				Place local = downloads.place(place.name() + ".txt");
				String id = GeoFSUtils.getGoogleID(place);
				boolean download = index.record(id, local);
				if (debug)
					System.out.printf("%s%s %s%s (%s)\n", ind, download?"+":"-", "", place.name(), id);
				if (download)
					place.copyTo(local);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		dlFrom.regions(region -> {
			Region folderInto = downloads.ensureSubregion(region.name());
			downloadFolder(index, folderInto, ind + "  ", region);
		});
	}
}
