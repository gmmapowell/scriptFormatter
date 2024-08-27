package com.gmmapowell.script.processor.configured;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;
import org.zinutils.reflection.Reflection;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.script.config.ExtensionPointRepo;
import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.FlowMap;
import com.gmmapowell.script.flow.HorizSpan;
import com.gmmapowell.script.flow.NestedSpan;
import com.gmmapowell.script.flow.Para;
import com.gmmapowell.script.flow.Section;
import com.gmmapowell.script.flow.SpanItem;
import com.gmmapowell.script.flow.TextSpanItem;
import com.gmmapowell.script.utils.SBLocation;

public class ConfiguredState extends SBLocation {
	private final Map<Class<?>, Object> configs = new HashMap<>();
	private final ExtensionPointRepo eprepo;
	public final FlowMap flows;
	protected Flow currFlow;
	private Section currSection;
	private Para currPara;
	private HorizSpan currSpan;
	private boolean ignoreBlanks = true;

	public ConfiguredState(ExtensionPointRepo eprepo, FlowMap flows, Place x) {
		this.eprepo = eprepo;
		this.flows = flows;
		super.processingFile(x.name());
	}

	@SuppressWarnings("unchecked")
	public <T> T require(Class<T> clz) {
		if (configs.containsKey(clz))
			return (T) configs.get(clz);
		T obj = (T) Reflection.create(clz);
		configs.put(clz, obj);
		return obj;
	}

	public ExtensionPointRepo extensions() {
		return eprepo;
	}

	public void processText(String tx) {
		newSpan();
		try {
			processPart(tx, 0, tx.length());
		} finally {
			endSpan();
		}
	}

	public void processPart(String tx, int i, int to) {
		int from = i;
		for (;i<to;i++) {
			int q;
			if ((q = findRange(tx, i, to)) >= 0) {
				if (i > from)
					text(tx.substring(from, i));
				makeSpan(tx.charAt(i));
				try {
					processPart(tx, i+1, q);
				} finally {
					popSpan();
				}
				i = q;
				from = i+1;
			} else if (q == -2) { // a double character; throw it away
				text(tx.substring(from, i+1));
				from = ++i + 1;
				
// TODO: some version of this needs to be modularized
//			} else if ((q = getCommand(st, tx, i, to)) >= 0) {
//				if (i > from)
//					st.text(tx.substring(from, i));
//				processCommand(tx.substring(i+1, q));
//				i = q;
//				from = i+1;
			} else if (q == -2) { // a double &; throw it away
				text(tx.substring(from, i+1));
				from = ++i + 1;
			} else if (Character.isWhitespace(tx.charAt(i))) {
				if (i > from)
					text(tx.substring(from, i));
				op(new BreakingSpace());
				from = i+1;
			}
		}
		if (to > from)
			text(tx.substring(from, to));
	}

	private int findRange(String tx, int i, int to) {
		// last on the line can't be doubled
		if (i+1 >= to)
			return -1;
		// is it a special char?
		char c = tx.charAt(i);
		if (c != '_' && c != '*' && c != '$')
			return -1;
		// it's a double ... return the magic value "-2"
		if (tx.charAt(i+1) == c)
			return -2;
		// find the matching one
		while (++i < to) {
			if (tx.charAt(i) == c)
				return i;
		}
		return -1;
	}

	private void makeSpan(char c) {
		switch (c) {
		case '_':
			nestSpan("italic");
			return;
		case '*':
			nestSpan("bold");
			return;
		case '$':
			nestSpan("tt");
			return;
		default:
			throw new NotImplementedException("figure out what to do with " + c);
		}
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
	
	public void newSection(String flow, String format) {
		currFlow = flows.get(flow);
		currSection = new Section(format);
		currFlow.sections.add(currSection);
		currPara = null;
		currSpan = null;
	}
	
	public void ensurePara() {
		if (currPara == null) {
			// TODO: this needs to be more complicated, taking into account any "all-paras-right-now-formats"
			newPara();
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

	public void ignoreNextBlanks() {
		this.ignoreBlanks = true;
	}

	public void observeBlanks() {
		this.ignoreBlanks = false;
	}

	public boolean ignoringBlanks() {
		return this.ignoreBlanks;
	}
}
