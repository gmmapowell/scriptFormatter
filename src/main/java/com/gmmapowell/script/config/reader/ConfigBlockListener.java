package com.gmmapowell.script.config.reader;

import java.util.Map;
import java.util.TreeMap;

import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.utils.Command;
import com.gmmapowell.script.utils.LineArgsParser;
import com.gmmapowell.script.utils.Utils;

public class ConfigBlockListener implements ConfigListener {
	private final ReadConfigState state;
	private Map<String, ConfigListenerProvider> blocks = new TreeMap<>();
	private ConfigListener nest;

	public ConfigBlockListener(ReadConfigState state) {
		this.state = state;
		blocks.put("module", new ModuleFinder(state));
		loadModules();
	}

	private void loadModules() {
		// TODO: this should come from config earlier
		blocks.put("loader", new ConfigureLoader(state));
		blocks.put("sink", new ConfigureSink(state));
		blocks.put("processor", new ConfigureProcessor(state));
		blocks.put("webedit", new ConfigureWebEdit(state));
	}

	@Override
	public ConfigListener dispatch(Command cmd) {
		this.nest = null;
		LineArgsParser lap = cmd.line();
		if (!lap.hasMore() && !"webedit".equals(cmd.name())) { // TODO: each processor should do this for itself
			throw WrappedException.wrap(new ConfigException("command '" + cmd.name() + "' requires an argument"));
		}
		if (blocks.containsKey(cmd.name())) {
			nest = blocks.get(cmd.name()).make(lap);
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
				throw WrappedException.wrap(new ConfigException("there is no command: '" + cmd.name() + "'"));
			}
			}
		}
		
		return null;
	}

	@Override
	public void complete() throws Exception {
	}
}
