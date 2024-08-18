package com.gmmapowell.script.config.reader;

import com.gmmapowell.geofs.World;
import com.gmmapowell.script.ScriptFormatterHelpException;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ConfigReader;

public class ConfigArgs {
	public static ConfigReader processConfig(World world, String[] args) throws ScriptFormatterHelpException, ConfigException {
		String config = null;
		for (int i=0;i<args.length;i++) {
			if (args[i].startsWith("-")) {
				throw new ScriptFormatterHelpException();
			} else if (config != null) {
				throw new ScriptFormatterHelpException("May only specify one config file");
			} else
				config = args[i];
		}
		if (config == null) {
			throw new ScriptFormatterHelpException();
		}
		return new ReadConfig(world.getUniverse(), world.placePath(config));
	}
}
