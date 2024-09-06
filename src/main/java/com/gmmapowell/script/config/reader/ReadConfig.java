package com.gmmapowell.script.config.reader;

import java.io.IOException;

import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.script.config.Config;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ConfigReader;

public class ReadConfig implements ConfigReader {
	private final Universe universe;
	private final Place place;

	public ReadConfig(Universe universe, Place place) {
		this.universe = universe;
		this.place = place;
	}

	@Override
	public Config read() throws ConfigException {
		ConfigParser parser = new ConfigParser(universe, place.region());
		place.lines(parser);
		try {
			return parser.config();
		} catch (IOException ex) {
			throw new ConfigException("Could not read configuration " + place + ": " + ex.toString());
		} catch (Exception ex) {
			throw WrappedException.wrap(ex);
		}
	}
}
