package com.gmmapowell.script.sink.blogger;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.LinkedHashMap;
import java.util.Map;

public class PostIndex {
	private final Map<String, String> current = new LinkedHashMap<>();
	private FileWriter appendTo;

	public void readFrom(FileReader fr) throws IOException {
		LineNumberReader lnr = new LineNumberReader(fr);
		String s;
		while ((s = lnr.readLine()) != null) {
			s = s.trim();
			if (s.length() == 0 || s.startsWith("#"))
				continue;
			int idx = s.indexOf(" ");
			current.put(s.substring(0, idx), s.substring(idx+1));
		}
	}

	public void appendTo(FileWriter fw) {
		this.appendTo = fw;
	}

	public void have(String id, String title) throws IOException {
		if (current.containsKey(id)) {
			return;
		}
		System.out.println("adding " + id + " .. " + title);
		appendTo.append(id);
		appendTo.append(" ");
		appendTo.append(title);
		appendTo.append("\n");
	}

}
