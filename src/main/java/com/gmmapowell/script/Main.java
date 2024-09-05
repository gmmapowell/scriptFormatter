package com.gmmapowell.script;

import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.lfs.LocalFileSystem;
import com.gmmapowell.geofs.simple.SimpleUniverse;
import com.gmmapowell.script.config.Config;
import com.gmmapowell.script.config.reader.ConfigArgs;
import com.gmmapowell.script.intf.FilesToProcess;

public class Main {
	public static void main(String[] args) {
		Universe uv = new SimpleUniverse();
		LocalFileSystem lfs = new LocalFileSystem(uv);
		try {
			Config cfg = ConfigArgs.processConfig(lfs, args).read();
			uv.prepareWorlds();
			FilesToProcess files = cfg.updateIndex();
			cfg.prepare();
			cfg.generate(files);
			cfg.dump();
			cfg.sink();
			cfg.show();
			cfg.upload();
			cfg.finish();
		} catch (Throwable t) {
			ExceptionHandler.handleAllExceptions(t);
		}
	}
}
