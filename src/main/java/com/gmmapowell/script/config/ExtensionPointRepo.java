package com.gmmapowell.script.config;

import java.util.Map;
import java.util.Set;

public interface ExtensionPointRepo {

	<T extends ExtensionPoint, Z extends T, Q> void bindExtensionPoint(Class<T> ep, Creator<Z, Q> impl);
	
	<T extends ExtensionPoint, Z extends T> void bindExtensionPoint(Class<T> ep, Class<Z> impl);

	<T extends NamedExtensionPoint, Q> Map<String, T> forPointByName(Class<T> clz, Q ctorArg);

	<T extends ExtensionPoint, Q> Set<T> forPoint(Class<T> clz, Q ctorArg);
}
