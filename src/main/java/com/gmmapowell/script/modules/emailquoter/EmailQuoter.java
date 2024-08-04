package com.gmmapowell.script.modules.emailquoter;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ModuleActivator;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.VarMap;

public class EmailQuoter implements ModuleActivator {
	public static void activate(ProcessorConfig proc, Region root, VarMap params) throws ConfigException {
		String threads = params.remove("threads");
		Region threadRegion = root.subregion(threads);
		String snaps = params.remove("snaps");
		Place snapsPlace = root.place(snaps);
		try {
			EmailConfig cfg = new EmailConfig(threadRegion, snapsPlace);
			proc.installCommand("emailpara", EmailParaCommand.class, cfg);
			proc.installCommand("emailthreads", EmailThreadsCommand.class, cfg);
			proc.installCommand("snap", SnapCommand.class, cfg);
		} catch (ConfigException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new ConfigException("error configuring EmailQuoter: " + ex);
		}
	}
}
