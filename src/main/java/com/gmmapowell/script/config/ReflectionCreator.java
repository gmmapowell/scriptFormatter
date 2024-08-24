package com.gmmapowell.script.config;

import org.zinutils.reflection.Reflection;

public class ReflectionCreator<T, Q> implements Creator<T, Q> {
	private final Class<T> clz;

	public ReflectionCreator(Class<T> clz) {
		this.clz = clz;
	}

	@Override
	public T create(Q quelle) {
		return (T) Reflection.create(clz, quelle);
	}

}
