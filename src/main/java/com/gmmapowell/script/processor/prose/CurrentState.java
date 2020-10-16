package com.gmmapowell.script.processor.prose;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.HorizSpan;
import com.gmmapowell.script.flow.NestedSpan;
import com.gmmapowell.script.flow.Para;
import com.gmmapowell.script.flow.ReleaseFlow;
import com.gmmapowell.script.flow.Section;
import com.gmmapowell.script.flow.SpanItem;
import com.gmmapowell.script.flow.TextSpanItem;
import com.gmmapowell.script.processor.TextState;

public abstract class CurrentState implements TextState {
	public final Map<String, Flow> flows;
	protected String file;
	protected int line;
	protected Section currSection;
	protected Para currPara;
	protected HorizSpan currSpan;
	protected Flow currFlow;

	protected int nextFnMkr = 1;
	protected int nextFnText = 1;

	public CurrentState(Map<String, Flow> flows) {
		this.flows = flows;
	}

	public int nextFootnoteMarker() {
		return nextFnMkr++;
	}
	
	public int nextFootnoteText() {
		return nextFnText++;
	}

	public abstract void line(int lineNumber);
	public abstract String location();

	protected boolean trimLine() {
		return true;
	}
	
	public void newSection(String flow, String format) {
		currFlow = flows.get(flow);
		if (currFlow == null) {
			throw new CantHappenException("there is no flow " + flow);
		}
		currSection = new Section(format);
		currFlow.sections.add(currSection);
		currPara = null;
		currSpan = null;
	}
	
	public void switchToFlow(String flow) {
		currFlow = flows.get(flow);
		if (currFlow == null) {
			throw new CantHappenException("there is no flow " + flow);
		}
		if (currFlow.sections.isEmpty()) {
			throw new CantHappenException("no sections for flow " + flow);
		} else {
			currSection = currFlow.sections.get(currFlow.sections.size()-1);
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
		// Hack? This is to get us back on track after footnotes, but it might be better to make that more explicit.
		// I can see it derailing us in other situations.
		if (currFlow.name.equals("footnotes") && flows.containsKey("main")) {
			newSpan();
			op(new ReleaseFlow("main"));
			switchToFlow("main");
		}

		endSpan();
		currPara = null;
	}

	public void deletePara() {
		currSection.paras.remove(currPara);
		endPara();
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

	public boolean inSpan() {
		return this.currSpan != null;
	}
}
