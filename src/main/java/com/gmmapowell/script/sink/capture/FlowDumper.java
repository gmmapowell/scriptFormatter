package com.gmmapowell.script.sink.capture;

import java.io.DataOutputStream;
import java.io.IOException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.HorizSpan;
import com.gmmapowell.script.flow.Para;
import com.gmmapowell.script.flow.Section;

public class FlowDumper {
	private DataOutputStream os;

	public FlowDumper(Place dumpTo) throws IOException {
		this.os = new DataOutputStream(dumpTo.stream());
		dumpModuleTable();
	}

	// Because the whole thing is extensible, it is necessary to dump what modules we think we have
	// and what opcode ranges they have been allocated.
	// As yet, we do not have this, but this is "forward compatible" in some sense
	private void dumpModuleTable() throws IOException {
		this.os.writeShort(0);	// the number of modules we have
	}

	public void dump(Flow flow) throws IOException {
		this.os.writeBoolean(flow.isMain());
		this.os.writeUTF(flow.name);
		this.os.writeShort(flow.sections.size());
		for (Section s : flow.sections) {
			dumpSection(s);
		}
	}

	private void dumpSection(Section s) throws IOException {
		this.os.writeUTF(s.format);
		this.os.writeShort(s.paras.size());
		for (Para p : s.paras) {
			dumpPara(p);
		}
	}

	private void dumpPara(Para p) throws IOException {
		this.os.writeShort(p.formats.size());
		for (String f : p.formats) {
			this.os.writeUTF(f);
		}
		this.os.writeShort(p.spans.size());
		for (HorizSpan s : p.spans) {
			s.intForm(this.os);
		}
	}

	public void close() throws IOException {
		this.os.close();
	}
}
