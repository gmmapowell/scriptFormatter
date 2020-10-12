package com.gmmapowell.script.flow;

import java.util.ArrayList;
import java.util.List;

public class Section {
	public final String format; 
	public final List<Para> paras = new ArrayList<>();
	
	public Section(String format) {
		this.format = format;
	}
}
