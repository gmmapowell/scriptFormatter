package com.gmmapowell.script.config.reader;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.listeners.NumberedLineListener;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.gmmapowell.script.config.Config;
import com.gmmapowell.script.config.ScriptConfig;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.utils.Command;
import com.gmmapowell.script.utils.CommandDispatcher;
import com.gmmapowell.script.utils.NestedCommandDispatcher;
import com.gmmapowell.script.utils.SBLineArgsParser;
import com.gmmapowell.script.utils.Utils;

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
		// if we've already seen an exception, stop
		if (capture != null)
			return;
		
		System.out.println("have " + lno + ": " + s);
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
	/*
//		int idx = s.indexOf(' ');
//		if (idx == -1) {
//			System.out.println("Syntax error on line " + lno);
//			System.out.println("  " + s);
//			config = null;
//			return;
//		}
//		String key = s.substring(0, idx);
//		String value = s.substring(idx+1).trim();
		if (nesting == 0) {
			// if a new block is starting, flush (any) previous block
			if (workdir == null)
				workdir = root.ensureSubregion("downloads");
			if (!handleCreation()) {
				config = null;
				return;
			}
			vars = new VarMap();
			what = null;
			switch (key) {
			case "debug": {
				debug = Boolean.parseBoolean(value);
				break;
			}
			case "index": {
				index = root.ensurePlace(value);
				break;
			}
			case "sshid": {
				sshid = Utils.subenvs(value);
				break;
			}
			case "workdir": {
				workdir = root.regionPath(value);
				break;
			}
			default: {
				what = key;
				type = value;
				wline = lno;
				break;
			}
			}
		} else if (what == null) {
			System.out.println(lno + ": must have outer block to nest inside: " + s);
		} else {
			vars.put(nesting, key, value);
		}
	*/
	
	@Override
	public void complete() {
		/*
		if (workdir == null)
			workdir = GeoFSUtils.ensureRegionPath(root, "downloads");
		if (!handleCreation()) {
			config = null;
		}
		*/
	}
	
	private boolean handleCreation() {
		try {
			case "webedit": {
				config.handleWebedit(vars, type, debug, sshid);
				break;
			}
			return true;
		} catch (Exception ex) {
			this.capture = ex;
			return false;
		}
	}

	public Config config() throws Exception {
		if (this.capture != null)
			throw this.capture;
		return state.config;
	}
}
