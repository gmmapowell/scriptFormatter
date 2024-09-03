package com.gmmapowell.script.config;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.zinutils.collections.ListMap;
import org.zinutils.exceptions.CantHappenException;

public class CreatorExtensionPointRepo implements ExtensionPointRepo {
	private final ExtensionPointRepo parent;
	private final ListMap<Class<?>, Creator<?, ?>> extensionPointCreators = new ListMap<>();

	public CreatorExtensionPointRepo(ExtensionPointRepo parent) {
		this.parent = parent;
		System.out.println("creating " + this + " with parent " + parent);
	}
	
	@Override
	public <T extends NamedExtensionPoint, Q> Map<String, T> forPointByName(Class<T> clz, Q ctorArg) {
		Map<String, T> ret;
		if (parent == null)
			ret = new TreeMap<>();
		else
			ret = parent.forPointByName(clz, ctorArg); 
		
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
	public <T extends ExtensionPoint, Q> Set<T> forPoint(Class<T> clz, Q ctorArg) {
		Set<T> ret;
		if (parent == null)
			ret = new HashSet<>();
		else
			ret = parent.forPoint(clz, ctorArg);
		
		if (extensionPointCreators.contains(clz)) {
			for (@SuppressWarnings("rawtypes") Creator m : extensionPointCreators.get(clz)) {
				@SuppressWarnings("unchecked")
				T nep = (T) m.create(ctorArg);
				ret.add(nep);
			}
		}
		return ret;
	}

	@Override
	public <T extends ExtensionPoint, Z extends T, Q> void bindExtensionPoint(Class<T> ep, Creator<Z, Q> impl) {
		extensionPointCreators.add(ep, impl);
	}

	@Override
	public <T extends ExtensionPoint, Z extends T> void bindExtensionPoint(Class<T> ep, Class<Z> impl) {
		bindExtensionPoint(ep, new ReflectionCreator<Z, Object>(impl));
	}
}
