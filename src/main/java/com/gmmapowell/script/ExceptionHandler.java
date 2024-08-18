package com.gmmapowell.script;

import com.gmmapowell.script.config.ConfigException;

public class ExceptionHandler {

	public static void handleAllExceptions(Throwable t) {
		try {
			throw t;
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
