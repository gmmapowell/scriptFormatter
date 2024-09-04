package com.gmmapowell.script.modules.doc.toc;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.Creator;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ModuleActivator;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.modules.doc.emailquoter.EmailParaCommand;
import com.gmmapowell.script.modules.doc.emailquoter.EmailQuoter.EmailParaCommandCreator;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.DocumentOutline;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;
import com.gmmapowell.script.modules.processors.doc.ScannerAtState;

public class TOCPreparer implements ModuleActivator, Creator<TOCOutline, ScannerAtState> {
	private final Region root;
	private final VarMap vars;

	public TOCPreparer(ReadConfigState state, VarMap vars) {
		this.root = state.root;
		this.vars = vars;
	}

	@Override
	public void activate(ProcessorConfig proc) throws ConfigException {
		TOCState toc = proc.global().requireState(TOCState.class);
		String meta = vars.remove("meta");
		if (meta == null)
			throw new ConfigException("TOC requires a meta file");
		Place metaFile = root.ensurePlace(meta);
		Place tocFile = null;
		String tocVar = vars.remove("toc");
		if (tocVar != null)
			tocFile = root.ensurePlace(tocVar);
		toc.configure(metaFile, tocFile);
		proc.addExtension(DocumentOutline.class, this);
		proc.addExtension(AmpCommandHandler.class, RefAmpCommand.class);
	}

	@Override
	public TOCOutline create(ScannerAtState quelle) {
		return new TOCOutline(quelle);
	}
}
