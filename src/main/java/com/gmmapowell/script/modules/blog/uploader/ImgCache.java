package com.gmmapowell.script.modules.blog.uploader;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.geofs.Place;

public class ImgCache {
	private final Place in;
	private final Map<String, String> cache = new TreeMap<>();

	public ImgCache(Place in) {
		this.in = in;
		if (in.exists())
			readCache();
	}

	private void readCache() {
		in.lines(line -> {
			String[] args = line.split(" ");
			associate(args[0], args[1]);
		});
	}
	
	public boolean hasAlready(String encoded, String hash) {
		if (!this.cache.containsKey(encoded))
			return false;
		
		if (!this.cache.get(encoded).equals(hash))
			return false;
		
		return true;
	}
	
	public void associate(String encoded, String hash) {
		this.cache.put(encoded, hash);
	}

	public void write() {
		try (Writer w = in.writer()) {
			for (Entry<String, String> e : cache.entrySet()) {
				w.append(e.getKey());
				w.append(" ");
				w.append(e.getValue());
				w.append("\n");
			}
		} catch (IOException ex) {
			throw WrappedException.wrap(ex);
		}
	}

}
