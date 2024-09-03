package com.gmmapowell.script.modules.doc.toc;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.Creator;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ModuleActivator;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.modules.processors.doc.DocumentOutline;
import com.gmmapowell.script.modules.processors.doc.ScannerAtState;

public class TOCPreparer implements ModuleActivator, Creator<DocumentOutline, ScannerAtState> {

	public TOCPreparer(ReadConfigState state, VarMap vars) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void activate(ProcessorConfig proc) throws ConfigException {
		proc.addExtension(DocumentOutline.class, this);
	}

	@Override
	public DocumentOutline create(ScannerAtState quelle) {
		// TODO Auto-generated method stub
		return null;
	}
}
