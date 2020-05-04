package com.gmmapowell.script.processor.movie;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.TreeMap;

public class DramatisPersonae {
	private final Map<String, Persona> personae = new TreeMap<>();

	public DramatisPersonae(File dramatis) throws IOException {
		try (LineNumberReader lnr = new LineNumberReader(new FileReader(dramatis))) {
			String s;
			Persona current = null;
			while ((s = lnr.readLine()) != null) {
				if (s.trim().length() == 0 || s.trim().startsWith("#"))
					continue;
				else if (Character.isWhitespace(s.charAt(0))) {
					current.addComment(s.trim());
				} else {
					int idx = s.indexOf(' ');
					current = new Persona(s.substring(idx).trim());
					String abbrev = s.substring(0, idx);
					if (personae.containsKey(abbrev))
						throw new RuntimeException("Duplicate persona index: " + abbrev);
					personae.put(abbrev, current);
				}
			}
		}
	}

	public String getSpeaker(String spkr) {
		Persona persona = personae.get(spkr);
		if (persona == null)
			return null;
		else
			return persona.getName();
	}

}
