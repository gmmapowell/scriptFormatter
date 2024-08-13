package com.gmmapowell.script.flow;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HorizSpan {
	public final HorizSpan parent;
	public final List<String> formats;
	public final List<SpanItem> items = new ArrayList<>();

	public HorizSpan(HorizSpan parent, List<String> formats) {
		this.parent = parent;
		this.formats = new ArrayList<>(formats);
	}

	public void intForm(DataOutputStream os) throws IOException {
		os.writeShort(this.formats.size());
		for (String f : this.formats) {
			os.writeUTF(f);
		}
		os.writeShort(this.items.size());
		for (SpanItem n : this.items) {
			n.intForm(os);
		}
		os.flush();
	}
}
