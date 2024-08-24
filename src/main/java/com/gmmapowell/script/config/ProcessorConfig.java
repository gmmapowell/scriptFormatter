package com.gmmapowell.script.config;

import com.gmmapowell.script.processor.configured.ProcessingScanner;

public interface ProcessorConfig {
	void addScanner(Class<? extends ProcessingScanner> scanner);
	<T, Z extends T, Q> void addExtension(Class<T> ep, Creator<Z, Q> impl);
	<T, Z extends T> void addExtension(Class<T> ep, Class<Z> impl);
}
