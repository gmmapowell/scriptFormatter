package com.gmmapowell.script;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

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
		FilesToProcess files;
		try {
			files = cfg.updateIndex();
		} catch (IOException | GeneralSecurityException e) {
			System.out.println("Error updating index from Google Drive:\n  " + e.getMessage());
			return;
		}
		try {
			cfg.generate(files);
		} catch (IOException ex) {
			System.out.println("Error processing:\n  " + ex.getMessage());
			return;
		}
		cfg.show();
		try {
			cfg.upload();
		} catch (Exception ex) {
			System.out.println("Error uploading to DH: " + ex.getMessage());
		}
	}
	
	private static void help() {
		System.out.println("Usage: ScriptFormatter <config>");
	}
}
