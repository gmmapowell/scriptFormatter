package com.gmmapowell.script;

import java.util.Date;

import org.zinutils.exceptions.NotImplementedException;
import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.geofs.exceptions.GeoFSException;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.processor.NoSuchCommandException;

public class ExceptionHandler {

	public static void handleAllExceptions(Throwable t) {
		t = WrappedException.unwrapThrowable(t);
		try {
			throw t;
		} catch (NotImplementedException ex) {
			System.err.println("Not Implemented: " + ex.getStackTrace()[0]);
		} catch (NoSuchCommandException ex) {
			System.err.println(ex.getMessage() + ": " + ex.getStackTrace()[0]);
		} catch (GeoFSException ex) {
			System.err.println(ex.getMessage());
		} catch (ScriptFormatterHelpException ex) {
			ex.help();
		} catch (ConfigException ex) {
			System.err.println("Error configuring ScriptFormatter: " + ex.getMessage());
		} catch (Throwable ex) {
			System.err.println("Date: " + new Date());
			ex.printStackTrace();
			System.exit(99);
		}
	}

}
