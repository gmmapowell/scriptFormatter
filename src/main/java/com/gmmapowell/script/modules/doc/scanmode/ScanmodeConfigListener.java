package com.gmmapowell.script.modules.doc.scanmode;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ModuleConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.AtCommandHandler;
import com.gmmapowell.script.utils.Command;

public class ScanmodeConfigListener implements ModuleConfigListener {
	private final ReadConfigState state;
	private ScanMode scanMode = ScanMode.NONE;

	public ScanmodeConfigListener(ReadConfigState state) {
		this.state = state;
	}
	
	@Override
	public ConfigListener dispatch(Command cmd) throws Exception {
		switch (cmd.name()) {
		case "scanmode": {
			String scanMode = cmd.line().readArg();
			this.scanMode = ScanMode.valueOf(scanMode.toUpperCase());
			return null;
		}
		default: {
			throw new CantHappenException("scanmode module does not have parameter " + cmd.name());
		}
		}
	}

	@Override
	public void complete() throws Exception {
	}

	@Override
	public void activate(ProcessorConfig proc) throws ConfigException {
		ScanmodeState scanning = proc.global().requireState(ScanmodeState.class);
		scanning.scanningFor(scanMode);

		proc.addScanner(ScanningModeIgnorer.class);

		state.config.extensions().bindExtensionPoint(AtCommandHandler.class, OverviewCommand.class);
		state.config.extensions().bindExtensionPoint(AtCommandHandler.class, DetailsCommand.class);
		state.config.extensions().bindExtensionPoint(AtCommandHandler.class, ConclusionCommand.class);

		state.config.extensions().bindExtensionPoint(AtCommandHandler.class, NumberingCommand.class);
		state.config.extensions().bindExtensionPoint(AmpCommandHandler.class, NumberAmpCommand.class);
	}
}
