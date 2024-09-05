package com.gmmapowell.script.modules.doc.toc;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.processor.prose.TableOfContents;

public class TOCState {
	private JSONObject currentMeta;
	private TableOfContents toc;
	public String chapterStyle;
	public int chapter = 1;
	public int section;
	public boolean commentary;
	public boolean wantSectionNumbering;
	
	public void configure(Place meta, Place toc) throws ConfigException {
		this.toc = new TableOfContents(toc, meta);
		if (meta.exists()) {
			try {
				currentMeta = GeoFSUtils.readJSON(meta);
			} catch (JSONException e) {
				throw new ConfigException("Failed to read " + meta + ": " + e);
			}
		}
	}

	public JSONObject currentMeta() {
		return currentMeta;
	}
	
	// TODO: reset should probably be called through some kind of EP mechanism
	public void reset() {
		section = 0;
		commentary = false;
	}

	public void resetNumbering() {
		chapter = 1;
		section = 0;
		commentary = false;
	}

	public TableOfContents toc() {
		return toc;
	}

	public void save() {
		try {
			toc.write();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
