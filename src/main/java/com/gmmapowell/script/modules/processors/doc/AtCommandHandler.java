package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.config.NamedExtensionPoint;

/** This class is the extension point to which all the @ commands are bound.
 * 
 */
public interface AtCommandHandler extends NamedExtensionPoint {

	void invoke(AtCommand cmd);
	
	default void onEnd(AtCommand cmd) {}

}
