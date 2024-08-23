package com.gmmapowell.script.config;

import java.util.Map;

public interface ExtensionPointRepo {

	<T extends NamedExtensionPoint, Q> Map<String, T> forPointByName(Class<T> clz, Q ctorArg);

}
