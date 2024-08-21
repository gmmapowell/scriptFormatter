package com.gmmapowell.script.config.reader;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.listeners.NumberedLineListener;
import com.gmmapowell.script.config.Config;
import com.gmmapowell.script.config.ScriptConfig;
import com.gmmapowell.script.utils.Command;
import com.gmmapowell.script.utils.NestedCommandDispatcher;
import com.gmmapowell.script.utils.SBLineArgsParser;

public class ConfigParser implements NumberedLineListener {
	private Exception capture;
	private final ReadConfigState state;
	private final NestedCommandDispatcher<ReadConfigState> dispatcher;

	public ConfigParser(Universe universe, Region root) {
		state = new ReadConfigState(root, new ScriptConfig(universe, root));
		dispatcher = new NestedCommandDispatcher<ReadConfigState>(state, new ConfigBlockListener(state));
	}
	
	@Override
	public void line(int lno, String s) {
		state.wline = lno;
		// if we've already seen an exception, stop
		if (capture != null)
			return;
		
		try {
			// Parse this input line
			SBLineArgsParser<ReadConfigState> lp = new SBLineArgsParser<>(state, s);
			Command cmd = lp.readCommand();
			if (cmd == null)
				return;
			
			dispatcher.dispatch(cmd);
		} catch (Exception ex) {
			this.capture = ex;
		}
	}
	
	public Config config() throws Exception {
		if (this.capture != null)
			throw this.capture;
		return state.config;
	}
}
