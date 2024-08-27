package com.gmmapowell.script.processor.configured;

import com.gmmapowell.script.config.NamedExtensionPoint;

public interface InlineCommandHandler extends NamedExtensionPoint {
	void invoke();
}
