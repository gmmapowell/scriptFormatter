package com.gmmapowell.script.modules.doc.toc;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ModuleActivator;
import com.gmmapowell.script.config.reader.ReadConfigState;

public class TOCPreparer implements ModuleActivator {

	public TOCPreparer(ReadConfigState state, VarMap vars) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void activate(ProcessorConfig proc) throws ConfigException {
		try {
			throw new NotImplementedException();
		} catch (NotImplementedException ex) {
			System.err.println("Not Implemented: " + ex.getStackTrace()[0]);
		}
	}

}
