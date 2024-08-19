package com.gmmapowell.script.config.reader;

import java.util.Map;
import java.util.TreeMap;

import org.zinutils.exceptions.NotImplementedException;
import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.utils.Command;
import com.gmmapowell.script.utils.LineArgsParser;
import com.gmmapowell.script.utils.Utils;

public class ConfigBlockListener implements ConfigListener {
	private final ReadConfigState state;
	private Map<String, ConfigListenerProvider> blocks = new TreeMap<>();

	public ConfigBlockListener(ReadConfigState state) {
		this.state = state;
		loadModules();
	}

	private void loadModules() {
		// TODO: this should come from config earlier
		blocks.put("loader", new ConfigureLoader(state));
		blocks.put("output", new ConfigureOutput(state));
		blocks.put("processor", new ConfigureProcessor(state));
	}

	@Override
	public ConfigListener dispatch(Command cmd) {
		LineArgsParser lap = cmd.line();
		if (!lap.hasMore()) {
			throw WrappedException.wrap(new ConfigException("command '" + cmd.name() + "' requires an argument"));
		}
		if (blocks.containsKey(cmd.name())) {
			ConfigListener nest = blocks.get(cmd.name()).make(lap);
			return nest;
		} else {
			// legacy - move to being blocks
			String value = lap.readArg();
			switch (cmd.name()) {
			case "debug": {
				state.debug = Boolean.parseBoolean(value);
				break;
			}
			case "index": {
				state.index = state.root.ensurePlace(value);
				break;
			}
			case "sshid": {
				state.sshid = Utils.subenvs(value);
				break;
			}
			case "workdir": {
				state.workdir = state.root.regionPath(value);
				break;
			}
			default: {
				// and you should be left with this ...
				throw new NotImplementedException("command '" + cmd.name() + "'");
			}
			}
		}
		
		return null;
	}

	@Override
	public void complete() {
		throw new NotImplementedException("complete " + this);
	}

}
