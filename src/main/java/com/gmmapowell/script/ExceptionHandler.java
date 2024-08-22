package com.gmmapowell.script;

import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.geofs.exceptions.GeoFSException;
import com.gmmapowell.script.config.ConfigException;

public class ExceptionHandler {

	public static void handleAllExceptions(Throwable t) {
		t = WrappedException.unwrapThrowable(t);
		try {
			throw t;
		} catch (GeoFSException ex) {
			System.err.println(ex.getMessage());
		} catch (ScriptFormatterHelpException ex) {
			ex.help();
		} catch (ConfigException ex) {
			System.err.println("Error configuring ScriptFormatter: " + ex.getMessage());
		} catch (Throwable ex) {
			ex.printStackTrace();
			System.exit(99);
		}
	}

}
