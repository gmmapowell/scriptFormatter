package com.gmmapowell.script.flow;

import java.util.ArrayList;
import java.util.List;

public class Para {
	public final List<String> formats;
	public final List<HorizSpan> spans = new ArrayList<>();

	public Para(List<String> formats) {
		if (formats == null)
			this.formats = new ArrayList<>();
		else
			this.formats = new ArrayList<>(formats);
	}
}
