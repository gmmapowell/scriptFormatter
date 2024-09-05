package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.config.ExtensionPointRepo;

public interface GlobalState {

	boolean debug();
	
	ExtensionPointRepo extensions();

	<T> T requireState(Class<T> clz);


}
