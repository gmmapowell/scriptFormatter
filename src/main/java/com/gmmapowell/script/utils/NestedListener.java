package com.gmmapowell.script.utils;

import com.gmmapowell.script.config.reader.ConfigListener;

public interface NestedListener {
	ConfigListener dispatch(Command cmd) throws Exception;
	void complete() throws Exception;
}
