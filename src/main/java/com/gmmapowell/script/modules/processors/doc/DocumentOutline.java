package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.config.ExtensionPoint;

public interface DocumentOutline extends ExtensionPoint {
	void entry(int level, String tocFormat, String title, String anchor);
}
