package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.config.NamedExtensionPoint;

public interface DocumentOutline extends NamedExtensionPoint {
	void entry(int level, String title);
}
