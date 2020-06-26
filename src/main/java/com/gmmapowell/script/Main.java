package com.gmmapowell.script;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

import com.gmmapowell.script.config.Config;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ReadConfig;

public class Main {
	public static void main(String[] args) {
		Config cfg = gatherConfig(args);
		if (cfg == null)
			return;
		int ret = 2;
		try {
			ret = format(cfg);
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			System.exit(ret);
		}
	}

	private static Config gatherConfig(String[] args) {
		String config = null;
		for (int i=0;i<args.length;i++) {
			if (args[i].startsWith("-")) {
				help();
				return null;
			} else if (config != null) {
				System.out.println("May only specify one config file");
				return null;
			} else
				config = args[i];
		}
		if (config == null) {
			help();
			return null;
		}
		return new ReadConfig().read(new File(config));
	}
	
	private static int format(Config cfg) {
		FilesToProcess files;
		try {
			files = cfg.updateIndex();
		} catch (ConfigException ex) {
			System.out.println("Error updating index from Google Drive:\n  " + ex.getMessage());
			return 1;
		} catch (IOException | GeneralSecurityException e) {
			System.out.println("Error updating index from Google Drive:\n  " + e.getMessage());
			return 1;
		}
		try {
			cfg.generate(files);
		} catch (IOException ex) {
			System.out.println("Error processing:\n  " + ex.getMessage());
			return 1;
		}
		cfg.show();
		try {
			cfg.upload();
		} catch (Exception ex) {
			System.out.println("Error uploading: " + ex.getMessage());
			ex.printStackTrace();
		}
		return 0;
	}

	private static void help() {
		System.out.println("Usage: ScriptFormatter <config>");
	}
}
