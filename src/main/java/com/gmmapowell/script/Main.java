package com.gmmapowell.script;

import java.io.File;

import com.gmmapowell.script.config.Config;
import com.gmmapowell.script.config.ReadConfig;

public class Main {
	public static void main(String[] args) {
		String config = null;
		for (int i=0;i<args.length;i++) {
			if (args[i].startsWith("-")) {
				help();
				return;
			} else if (config != null) {
				System.out.println("May only specify one config file");
				return;
			} else
				config = args[i];
		}
		if (config == null) {
			help();
			return;
		}
		Config cfg = new ReadConfig().read(new File(config));
		if (cfg == null)
			return;
		cfg.updateIndex();
		cfg.generate();
		cfg.show();
	}
	
	private static void help() {
		System.out.println("Usage: ScriptFormatter <config>");
	}
}
