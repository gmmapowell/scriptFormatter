package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.processor.configured.ProcessingScanner;
import com.gmmapowell.script.utils.Command;
import com.gmmapowell.script.utils.LineArgsParser;
import com.gmmapowell.script.utils.SBLineArgsParser;

public class AmpSpotter implements ProcessingScanner {
	private final ConfiguredState state;
	private final ScannerAmpState amps;

	public AmpSpotter(ConfiguredState state) {
		this.state = state;
		this.amps = state.require(ScannerAmpState.class);
		this.amps.configure(state, state.extensions());
	}

	@Override
	public void closeIfNotContinued(String nx) {
		if (amps.hasPendingCommand()) {
			amps.handleAmpCommand();
		}
	}

	@Override
	public boolean handleLine(String s) {
		if (s.startsWith("&")) {
			LineArgsParser lap = new SBLineArgsParser<ConfiguredState>(state, s.substring(1));
			System.out.println("is-amp");
			Command cmd = lap.readCommand();
			amps.startCommand(cmd.name(), lap);
			return true;
		}
			
		return false;
	}

}
