package com.gmmapowell.script.config;

import com.gmmapowell.geofs.World;
import com.gmmapowell.script.ScriptFormatterHelpException;

public class ConfigArgs {
	public static Config processConfig(World lfs, String[] args) throws ScriptFormatterHelpException {
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
		return new ReadConfig(lfs).read(config);
	}
}
