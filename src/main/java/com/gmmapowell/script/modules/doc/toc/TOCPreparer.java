package com.gmmapowell.script.modules.doc.toc;

import java.lang.reflect.InvocationTargetException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.Creator;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ModuleActivator;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.AtCommandHandler;
import com.gmmapowell.script.modules.processors.doc.DocumentOutline;
import com.gmmapowell.script.modules.processors.doc.OutlineNumbering;
import com.gmmapowell.script.modules.processors.doc.ScannerAtState;

public class TOCPreparer implements ModuleActivator, Creator<TOCOutline, ScannerAtState> {
//	private final ReadConfigState state;
	private final Region root;
	private final VarMap vars;

	public TOCPreparer(ReadConfigState state, VarMap vars) {
//		this.state = state;
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
		String numbering = vars.remove("numbering");
		if (numbering == null) {
			throw new ConfigException("TOC requires a numbering algorithm");
		}
		try {
			@SuppressWarnings("unchecked")
			Class<? extends OutlineNumbering> clz = (Class<? extends OutlineNumbering>) Class.forName(numbering);
			OutlineNumbering inst = clz.getConstructor(TOCState.class).newInstance(toc);
			toc.configure(metaFile, tocFile, inst);
			proc.addExtension(DocumentOutline.class, this);
			proc.addExtension(AmpCommandHandler.class, RefAmpCommand.class);
			proc.addExtension(AtCommandHandler.class, AtTOCCommand.class);
			proc.lifecycleObserver(new TOCObserver(toc));
		} catch (ClassNotFoundException ex) {
			throw new ConfigException("Could not find numbering class: " + numbering);
		} catch (NoSuchMethodException ex) {
			throw new ConfigException("Could not find TOCState constructor for numbering class: " + numbering);
		} catch (InvocationTargetException | IllegalAccessException | InstantiationException ex) {
			throw new ConfigException("Could not instantiate numbering class: " + numbering + ": " + ex.getMessage());
		}
		
	}

	@Override
	public TOCOutline create(ScannerAtState quelle) {
		return new TOCOutline(quelle);
	}
}
