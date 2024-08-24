package com.gmmapowell.script.config;

import java.util.Map;

public interface ExtensionPointRepo {

	<T, Z extends T, Q> void bindExtensionPoint(Class<T> ep, Creator<Z, Q> impl);
	
	<T, Z extends T> void bindExtensionPoint(Class<T> ep, Class<Z> impl);

	<T extends NamedExtensionPoint, Q> Map<String, T> forPointByName(Class<T> clz, Q ctorArg);
}
