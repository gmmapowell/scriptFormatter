package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.script.config.ExtensionPointRepo;
import com.gmmapowell.script.config.Finisher;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.FlowMap;

public interface GlobalState {

	boolean debug();
	
	ExtensionPointRepo extensions();

	<T> T requireState(Class<T> clz);

	FlowMap flows();

	Flow flow(String flow);

	Universe getUniverse();

	Region getRoot();

	void addFinisher(Finisher f);

}
