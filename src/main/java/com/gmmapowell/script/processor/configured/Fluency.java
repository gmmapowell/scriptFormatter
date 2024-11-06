package com.gmmapowell.script.processor.configured;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.HorizSpan;
import com.gmmapowell.script.flow.NestedSpan;
import com.gmmapowell.script.flow.Para;
import com.gmmapowell.script.flow.Section;
import com.gmmapowell.script.flow.SpanItem;
import com.gmmapowell.script.flow.TextSpanItem;
import com.gmmapowell.script.modules.processors.doc.GlobalState;

public class Fluency {
	private final GlobalState global;
	protected Flow currFlow;
	private Section currSection;
	private Para currPara;
	private HorizSpan currSpan;
	private List<String> fmtStack = new ArrayList<String>();

	public Fluency(GlobalState global) {
		this.global = global;
	}
	
	public void switchToFlow(String flow) {
		currFlow = global.flow(flow);
		if (currFlow == null) {
			throw new CantHappenException("there is no flow " + flow);
		}
		if (currFlow.sections.isEmpty()) {
			throw new CantHappenException("no sections for flow " + flow);
		} else {
			currSection = currFlow.sections.get(currFlow.sections.size()-1);
		}
		currPara = null;
		currSpan = null;
	}
	
	public void newSection(String flow, String format) {
		currFlow = global.flow(flow);
		currSection = new Section(format);
		currFlow.sections.add(currSection);
		currPara = null;
		currSpan = null;
	}
	
	public boolean inPara() {
		return this.currPara != null;
	}
	
	public void ensurePara() {
		if (currPara == null) {
			newPara();
		}
	}

	public void newPara(List<String> formats) {
		if (currSection == null) {
			throw new CantHappenException("no current section");
		}
		List<String> merged = new ArrayList<>(fmtStack);
		merged.addAll(formats);
		if (merged.isEmpty())
			merged.add("text");
		currPara = new Para(merged);
		currSection.paras.add(currPara);
		currSpan = null;
	}

	public void newPara(String... formats) {
		// TODO: should include current formats ...
		newPara(Arrays.asList(formats));
	}

	public void endPara() {
		endSpan();
		currPara = null;
	}

	public void abortPara() {
		if (currPara == null) {
			return;
		}
		currSection.paras.remove(currPara);
		currSpan = null;
		currPara = null;
	}

	public boolean inSpan() {
		return this.currSpan != null;
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
	
	public void endSpan() {
		if (currSpan == null) // it's OK to try ending a span that isn't open
			return;
		if (currSpan.parent != null)
			throw new CantHappenException("has parent; should use popSpan first");
		currSpan = null;
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

	public boolean topSpanHas(String fmt) {
		return currSpan != null && currSpan.parent != null && currSpan.formats.contains(fmt);
	}
	
	public void popSpan() {
		if (currSpan == null)
			throw new CantHappenException("no current span");
		else if (currSpan.parent == null)
			throw new CantHappenException("not a child span");
		currSpan = currSpan.parent;
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

	public void pushFormat(String fmt) {
		fmtStack.add(fmt);
	}
	
	public boolean hasFormat(String fmt) {
		return !fmtStack.isEmpty() && fmtStack.get(fmtStack.size()-1).equals(fmt);
	}

	public void popFormat(String fmt) {
		if (fmtStack.isEmpty())
			throw new CantHappenException("format stack is empty");
		String last = fmtStack.remove(fmtStack .size()-1);
		if (!fmt.equals(last))
			throw new CantHappenException("wanted to remove " + fmt + " but top was " + last);
	}

	public void ensureFlow(String flow) {
		global.flows().flow(flow);
		currFlow = global.flow(flow);
	}
}
