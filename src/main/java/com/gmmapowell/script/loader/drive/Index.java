package com.gmmapowell.script.loader.drive;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.gmmapowell.script.FilesToProcess;

public class Index implements FilesToProcess {
	private final File downloads;
	private FileWriter appendTo;
	private boolean writtenExcluded;
	private Map<String, String> current = new LinkedHashMap<>();
	private int included;

	public Index(File downloads) {
		this.downloads = downloads;
	}
	
	public void readFrom(FileReader fr) throws IOException {
		LineNumberReader lnr = new LineNumberReader(fr);
		String s;
		while ((s = lnr.readLine()) != null) {
			s = s.trim();
			if (s.length() == 0 || s.startsWith("#"))
				continue;
			if (s.equals("--excluded--")) {
				included = current.size();
				writtenExcluded = true;
				continue;
			}
			int idx = s.indexOf(" ");
			current.put(s.substring(0, idx), s.substring(idx+1));
		}
	}

	public void appendTo(FileWriter fw) {
		this.appendTo = fw;
	}

	public void record(String id, File name) throws IOException {
		if (current.containsKey(id)) {
			return;
		}
		if (!writtenExcluded) {
			appendTo.append("--excluded--\n");
			writtenExcluded = true;
		}
		appendTo.append(id);
		appendTo.append(" ");
		appendTo.append(name.getPath().replace(downloads.getPath() + "/", ""));
		appendTo.append("\n");
	}

	@Override
	public Iterable<File> included() {
		List<File> fs = new ArrayList<>();
		Iterator<String> it = current.values().iterator();
		for (int i=0; i<included; i++) {
			fs.add(new File(downloads, it.next()));
		}
		return fs;
	}

	public void close() throws IOException {
		appendTo.close();
	}
}
