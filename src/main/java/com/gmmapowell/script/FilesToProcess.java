package com.gmmapowell.script;

import java.io.File;

public interface FilesToProcess {

	String title();
	Iterable<File> included();

}
