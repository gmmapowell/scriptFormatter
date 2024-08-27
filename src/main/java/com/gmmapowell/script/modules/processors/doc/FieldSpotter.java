package com.gmmapowell.script.modules.processors.doc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingScanner;

public class FieldSpotter implements ProcessingScanner {
	private final Pattern fieldStart = Pattern.compile("([a-z][a-z0-9A-Z_]*)=(.*)");
	private final ScannerAtState ats;
	
	public FieldSpotter(ConfiguredState state) {
		this.ats = state.require(ScannerAtState.class);
	}

	@Override
	public boolean handleLine(String s) {
		if (!ats.hasPendingCommand())
			return false; // we do not apply here
		
		Matcher m = fieldStart.matcher(s);
		if (m.matches()) {
			System.out.println("is-field");
			ats.cmdField(m.group(1), m.group(2));
			return true;
		} else {
			throw new CantHappenException("not a valid field for command at " + ats.state().inputLocation());
		}
	}

}
