package com.gmmapowell.script.utils;

import java.util.Date;

import com.jcraft.jsch.Logger;

public class JSCHLogger implements Logger {
	@Override
	public boolean isEnabled(int level) {
		return true;
	}

	@Override
	public void log(int level, String message) {
		System.out.println(new Date() + ": L" + level + ": "+ message);
	}
}
