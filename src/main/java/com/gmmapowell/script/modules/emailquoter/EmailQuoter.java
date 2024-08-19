package com.gmmapowell.script.modules.emailquoter;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ModuleActivator;
import com.gmmapowell.script.config.reader.ReadConfigState;

public class EmailQuoter implements ModuleActivator {
	private final EmailConfig cfg;

	public EmailQuoter(ReadConfigState state, VarMap params) throws ConfigException {
		try {
			String threads = params.remove("threads");
			Region threadRegion = state.root.subregion(threads);
			String snaps = params.remove("snaps");
			Place snapsPlace = state.root.place(snaps);
			cfg = new EmailConfig(threadRegion, snapsPlace);
		} catch (Exception ex) {
			throw new ConfigException("error configuring EmailQuoter: " + ex);
		}
	}
		
	@Override
	public void activate(ProcessorConfig proc) throws ConfigException {
		try {
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
