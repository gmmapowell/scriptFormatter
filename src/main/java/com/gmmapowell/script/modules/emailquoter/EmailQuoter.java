package com.gmmapowell.script.modules.emailquoter;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ModuleActivator;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.VarMap;

public class EmailQuoter implements ModuleActivator {
	public static void activate(ProcessorConfig proc, VarMap params) throws ConfigException {
		EmailConfig cfg = new EmailConfig();
		proc.installCommand("emailpara", EmailParaCommand.class, cfg);
		proc.installCommand("emailthreads", EmailThreadsCommand.class, cfg);
		proc.installCommand("snap", SnapCommand.class, cfg);
	}
}
