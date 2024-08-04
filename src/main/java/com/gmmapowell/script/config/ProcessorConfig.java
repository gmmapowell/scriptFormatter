package com.gmmapowell.script.config;

import com.gmmapowell.script.processor.prose.LineCommand;

public interface ProcessorConfig {
	void installCommand(String cmd, Class<? extends LineCommand> proc, Object cfg) throws ConfigException;
}
