package com.gmmapowell.script.modules.processors.movie;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

import com.gmmapowell.geofs.Place;

public class DramatisPersonae {
	private final Map<String, Persona> personae = new TreeMap<>();

	public DramatisPersonae(Place dramatis) throws IOException {
		AtomicReference<Persona> current = new AtomicReference<>();
		dramatis.lines(s -> {
			if (s.trim().length() == 0 || s.trim().startsWith("#"))
				return;
			else if (Character.isWhitespace(s.charAt(0))) {
				current.get().addComment(s.trim());
			} else {
				int idx = s.indexOf(' ');
				current.set(new Persona(s.substring(idx).trim()));
				String abbrev = s.substring(0, idx);
				if (personae.containsKey(abbrev))
					throw new RuntimeException("Duplicate persona index: " + abbrev);
				personae.put(abbrev, current.get());
			}
		});
	}

	public String getSpeaker(String spkr) {
		Persona persona = personae.get(spkr);
		if (persona == null)
			return null;
		else
			return persona.getName();
	}
}
