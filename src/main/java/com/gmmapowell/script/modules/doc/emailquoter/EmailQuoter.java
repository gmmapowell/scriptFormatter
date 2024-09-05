package com.gmmapowell.script.modules.doc.emailquoter;


import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.Creator;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ModuleActivator;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;

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
			proc.addExtension(AmpCommandHandler.class, new EmailParaCommandCreator());
//			proc.installCommand("emailpara", EmailParaCommand.class, cfg);
//			proc.installCommand("emailthreads", EmailThreadsCommand.class, cfg);
			proc.addExtension(AmpCommandHandler.class, (ScannerAmpState q) -> new SnapCommand(cfg, q));
			proc.addExtension(AmpCommandHandler.class, (ScannerAmpState q) -> new EmailThreadsCommand(cfg, q));
			proc.addExtension(AmpCommandHandler.class, (ScannerAmpState q) -> new EmailsByDateCommand(cfg, q));
//		} catch (ConfigException ex) {
//			throw ex;
		} catch (Exception ex) {
			throw new ConfigException("error configuring EmailQuoter: " + ex);
		}
	}

	public class EmailParaCommandCreator implements Creator<EmailParaCommand, ScannerAmpState> {

		@Override
		public EmailParaCommand create(ScannerAmpState quelle) {
			return new EmailParaCommand(cfg, quelle);
		}

	}
}
