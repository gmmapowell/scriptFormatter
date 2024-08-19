package com.gmmapowell.script.config.reader;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ProcessorConfig;

public interface ModuleActivator {

	void activate(ProcessorConfig proc) throws ConfigException;

}
