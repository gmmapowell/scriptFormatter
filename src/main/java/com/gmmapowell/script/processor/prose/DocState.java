package com.gmmapowell.script.processor.prose;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.HorizSpan;
import com.gmmapowell.script.flow.NestedSpan;
import com.gmmapowell.script.flow.Para;
import com.gmmapowell.script.flow.Section;
import com.gmmapowell.script.flow.SpanItem;
import com.gmmapowell.script.flow.TextSpanItem;

public class DocState extends CurrentState {
	public final Map<String, Flow> flows = new TreeMap<>();
	public DocCommand cmd;
	public InlineCommand inline;
	public int chapter;
	public int section;
	public boolean commentary;
	public boolean beginComment;
	public boolean inRefComment;
	private String file;
	private int line;
	private Section currSection;
	private Para currPara;
	private HorizSpan currSpan;
	public boolean wantNumbering;

	public void reset(String file) {
		this.file = file;
		cmd = null;
		curr = null;
		section = 0;
		commentary = false;
		nextFnMkr = 1;
		nextFnText = 1;
	}

	@Override
	public void line(int lineNumber) {
		this.line = lineNumber;
	}

	public String inputLocation() {
		return file + ":" + line;
	}
	
	public String location() {
		return (chapter-1) + "." + (section-1) + (commentary?"c":"");
	}
	
	public void newSection(String flow, String format) {
		Flow f = flows.get(flow);
		if (f == null) {
			throw new CantHappenException("there is no flow " + flow);
		}
		currSection = new Section(format);
		f.sections.add(currSection);
		currPara = null;
		currSpan = null;
	}
	
	public void switchToFlow(String flow) {
		Flow f = flows.get(flow);
		if (f == null) {
			throw new CantHappenException("there is no flow " + flow);
		}
		if (f.sections.isEmpty()) {
			currSection = new Section(null);
			f.sections.add(currSection);
		} else {
			currSection = f.sections.get(f.sections.size()-1);
		}
	}
	
	public void newPara(List<String> formats) {
		if (currSection == null) {
			throw new CantHappenException("no current section");
		}
		currPara = new Para(formats);
		currSection.paras.add(currPara);
		currSpan = null;
	}

	public void newPara(String... formats) {
		newPara(Arrays.asList(formats));
	}

	public void endPara() {
		endSpan();
		currPara = null;
	}
	
	public void newSpan(List<String> formats) {
		if (currPara == null) {
			throw new CantHappenException("no current para");
		}
		if (currSpan != null && currSpan.parent != null) {
			throw new CantHappenException("closed nested span first");
		}
		currSpan = new HorizSpan(null, formats);
		currPara.spans.add(currSpan);
	}

	public void newSpan(String... formats) {
		newSpan(Arrays.asList(formats));
	}
	
	public void nestSpan(List<String> formats) {
		if (currSpan == null) {
			throw new CantHappenException("no current span to nest inside");
		}
		HorizSpan tmp;
		tmp = new HorizSpan(currSpan, formats);
		currSpan.items.add(new NestedSpan(tmp));
		currSpan = tmp;
	}

	public void nestSpan(String... formats) {
		nestSpan(Arrays.asList(formats));
	}
	
	public void text(String tx) {
		if (currSpan == null) {
			throw new CantHappenException("no current span");
		}
		currSpan.items.add(new TextSpanItem(tx));
	}
	
	public void op(SpanItem op) {
		if (currSpan == null) {
			throw new CantHappenException("no current span");
		}
		currSpan.items.add(op);
	}

	public void popSpan() {
		if (currSpan == null)
			throw new CantHappenException("no current span");
		else if (currSpan.parent == null)
			throw new CantHappenException("not a child span");
		currSpan = currSpan.parent;
	}

	public void endSpan() {
		if (currSpan == null) // it's OK to try ending a span that isn't open
			return;
		if (currSpan.parent != null)
			throw new CantHappenException("has parent; should use popSpan first");
		currSpan = null;
	}

	public boolean inPara() {
		return this.currPara != null;
	}
}
