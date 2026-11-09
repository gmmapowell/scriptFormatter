package com.gmmapowell.script.loader;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.intf.FilesToProcess;
import com.gmmapowell.script.loader.Index.Status;

public class LFSContentLoader implements Loader {
	private Region downloads;
	private Place indexFile;
	private boolean recursive;
	private boolean debug;

	public LFSContentLoader(Region downloads, Place indexFile, boolean recursive, boolean debug) {
		this.downloads = downloads;
		this.indexFile = indexFile;
		this.recursive = recursive;
		this.debug = debug;
	}
	
	@Override
	public void createWebeditIn(Place file, String title) {
		throw new NotImplementedException();
	}

	@Override
	public FilesToProcess updateIndex() throws IOException, GeneralSecurityException, ConfigException {
		Index currentIndex = Index.read(indexFile, downloads);
		try {
			if (debug) {
				System.out.println("Recognizing files from " + downloads);
			}
			recognizeFolder(currentIndex, downloads, "    ", "");
			return currentIndex;
		} finally {
			currentIndex.close();
		}
	}

	@Override
	public void reload(FilesToProcess files) {
		throw new NotImplementedException();
	}
	
	private void recognizeFolder(Index index, Region downloads, String ind, String prefix) {
		if (downloads == null) {
			throw new CantHappenException("downloads cannot be null");
		}
		downloads.places(place -> {
			try {
				String id = place.name().replaceAll(" ", "");
				Status download = index.record(id, doPrefix(prefix, place.name()), place.lastModified());
				if (debug)
					System.out.printf("%s%s %s%s (%s)\n", ind, download.flag(), "", place.name(), id);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		if (recursive) {
			downloads.regions(region -> {
				if (debug)
					System.out.printf("%s%s %s%s\n", ind, "/", "", region.name());
				recognizeFolder(index, region, ind + "  ", doPrefix(prefix, region.name()));
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
