package com.gmmapowell.script.config.reader;

import com.gmmapowell.script.utils.LineArgsParser;

@FunctionalInterface
public interface ConfigListenerProvider {
	ConfigListener make(LineArgsParser lap);
}
