package com.gmmapowell.script.sink.blogger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.utils.GeoFSUtils;

public class PostIndex {
	public class BlogEntry {
		public final String key;
		public final boolean isLive;

		public BlogEntry(String key, boolean isLive) {
			this.key = key;
			this.isLive = isLive;
		}
		
		@Override
		public String toString() {
			return key;
		}
	}

	private final Map<String, String> live = new LinkedHashMap<>();
	private final Map<String, String> draft = new LinkedHashMap<>();
	private FileWriter appendTo;

	public void readFrom(Place postsFile) throws IOException {
		postsFile.lines((n,s) -> {
			s = s.trim();
			if (s.length() == 0 || s.startsWith("#"))
				return;
			int idx = s.indexOf(" ");
			int id2 = s.indexOf(" ", idx+1);
			String status = s.substring(idx, id2).trim().toLowerCase();
			switch (status) {
			case "live":
				live.put(s.substring(0, idx), s.substring(id2+1));
				break;
			case "draft":
				draft.put(s.substring(0, idx), s.substring(id2+1));
				break;
			default:
				throw new RuntimeException("Cannot handle status " + status);
			}
		});
	}

	public void appendTo(Place postsFile) {
		this.appendTo = GeoFSUtils.fileAppender(postsFile);
	}

	public void have(String id, String status, String title) throws IOException {
		if (live.containsKey(id)) {
			if (!"live".equalsIgnoreCase(status))
				System.out.println(id + " " + title + " is now draft");
			return;
		}
		if (draft.containsKey(id)) {
			if (!"draft".equalsIgnoreCase(status))
				System.out.println(id + " " + title + " is now published");
			return;
		}
		System.out.println("adding " + id + " (" + status +  ") => " + title);
		appendTo.append(id);
		appendTo.append(" ");
		appendTo.append(status);
		appendTo.append(" ");
		appendTo.append(title);
		appendTo.append("\n");
	}

	public BlogEntry find(String title) {
		for (Entry<String, String> i : draft.entrySet()) {
			if (i.getValue().equals(title))
				return new BlogEntry(i.getKey(), false);
		}
		for (Entry<String, String> i : live.entrySet()) {
			if (i.getValue().equals(title))
				return new BlogEntry(i.getKey(), true);
		}
		return null;
	}

	public void close() throws IOException {
		if (appendTo != null)
			appendTo.close();
	}
}
