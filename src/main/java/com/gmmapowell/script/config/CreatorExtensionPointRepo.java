package com.gmmapowell.script.config;

import java.util.Map;
import java.util.TreeMap;

import org.zinutils.collections.ListMap;
import org.zinutils.exceptions.CantHappenException;

public class CreatorExtensionPointRepo implements ExtensionPointRepo {
	private final ListMap<Class<?>, Creator<?, ?>> extensionPointCreators = new ListMap<>();

	@Override
	public <T extends NamedExtensionPoint, Q> Map<String, T> forPointByName(Class<T> clz, Q ctorArg) {
		Map<String, T> ret = new TreeMap<>();
		if (extensionPointCreators.contains(clz)) {
			for (@SuppressWarnings("rawtypes") Creator m : extensionPointCreators.get(clz)) {
				@SuppressWarnings("unchecked")
				T nep = (T) m.create(ctorArg);
				if (ret.containsKey(nep.name())) {
					throw new CantHappenException("duplicate extension point for " + nep.name());
				}
				ret.put(nep.name(), nep);
			}
		}
		return ret;
	}

	@Override
	public <T, Z extends T, Q> void bindExtensionPoint(Class<T> ep, Creator<Z, Q> impl) {
		extensionPointCreators.add(ep, impl);
	}

	@Override
	public <T, Z extends T> void bindExtensionPoint(Class<T> ep, Class<Z> impl) {
		bindExtensionPoint(ep, new ReflectionCreator<Z, Object>(impl));
	}
}
