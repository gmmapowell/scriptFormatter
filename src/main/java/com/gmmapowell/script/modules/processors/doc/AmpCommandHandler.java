package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.config.NamedExtensionPoint;
import com.gmmapowell.script.utils.Command;
import com.gmmapowell.script.utils.LineArgsParser;

/** This class is the extension point to which all the @ commands are bound.
 * 
 */
public interface AmpCommandHandler extends NamedExtensionPoint {

	void invoke(AmpCommand cmd);

	default boolean continuation(Command cont, LineArgsParser lap) { return false; }

}
