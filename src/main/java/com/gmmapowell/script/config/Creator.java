package com.gmmapowell.script.config;

public interface Creator<T, Q> {
	T create(Q quelle);
}
