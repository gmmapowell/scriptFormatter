package com.gmmapowell.script.utils;

public interface CommandDispatcher {

	void dispatch(Command cmd) throws Exception;

	void complete() throws Exception;

}
