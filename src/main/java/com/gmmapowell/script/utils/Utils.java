package com.gmmapowell.script.utils;

import org.zinutils.exceptions.UtilException;

public class Utils {

	public static String subenvs(String value) {
		while (value.contains("${")) {
			int idx = value.indexOf("${")+2;
			int idx2 = value.indexOf("}", idx);
			if (idx2 == -1)
				throw new UtilException("Malformed reference: " + value);
			String key = value.substring(idx, idx2);
			String var = System.getenv(key);
			if (var == null)
				throw new UtilException("there is no env var " + key);
			value = value.replaceAll("\\$\\{"+key+"\\}", var);
		}
		return value;
	}

}
