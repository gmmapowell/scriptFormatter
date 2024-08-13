package com.gmmapowell.script;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.lfs.LocalFileSystem;
import com.gmmapowell.geofs.simple.SimpleUniverse;
import com.gmmapowell.script.config.Config;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ReadConfig;
import com.gmmapowell.script.intf.FilesToProcess;

public class Main {
	public static void main(String[] args) {
		Universe uv = new SimpleUniverse();
		LocalFileSystem lfs = new LocalFileSystem(uv);
		Config cfg = gatherConfig(lfs, args);
		if (cfg == null)
			return;
		int ret = 2;
		try {
			ret = format(cfg);
		} catch (Throwable t) {
			t.printStackTrace(System.out);
		} finally {
			System.exit(ret);
		}
	}

	private static Config gatherConfig(World lfs, String[] args) {
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
		return new ReadConfig(lfs).read(config);
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
