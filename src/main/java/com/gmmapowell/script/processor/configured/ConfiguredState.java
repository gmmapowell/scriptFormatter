package com.gmmapowell.script.processor.configured;

import java.util.ArrayList;
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
import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.modules.processors.doc.InlineDocCommandState;
import com.gmmapowell.script.processor.NoSuchCommandException;
import com.gmmapowell.script.processor.ParsingException;
import com.gmmapowell.script.utils.SBLocation;

public class ConfiguredState extends SBLocation {
	private final GlobalState global;
	private final Map<Class<?>, Object> configs = new HashMap<>();
	private final ExtensionPointRepo eprepo;
	public final FlowMap flows;
	protected Flow currFlow;
	private Section currSection;
	private Para currPara;
	private HorizSpan currSpan;
	private boolean ignoreBlanks = true;
	private Map<String, InlineCommandHandler> inlineCommands;
	private List<String> fmtStack = new ArrayList<String>();

	public ConfiguredState(GlobalState global, ExtensionPointRepo eprepo, FlowMap flows, Place x) {
		this.global = global;
		this.eprepo = eprepo;
		this.flows = flows;
		// TODO: while most of this should be here, the InlineCommandState should not be solid ...
		// Thus there should probably be a function passed in to create it...
		this.inlineCommands = eprepo.forPointByName(InlineCommandHandler.class, new InlineDocCommandState(this)); 
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

	public GlobalState global() {
		return global;
	}

	public ExtensionPointRepo extensions() {
		return eprepo;
	}

	/* All of these functions (which came from processing utils) feel fundamentally different
	 * from the ones that actually generate the flows.
	 * 
	 * I feel they should be in separate places in some sense, but both of them have something to do
	 * with "generating flows", so they have ended up together.
	 * 
	 * In particular, the ones to do with **literally** generating flows are truly valid across all
	 * input languages, but the text processing doesn't have to be.  Of course, on the other hand,
	 * I think both that it is and I want it to be for consistency reasons.
	 */
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
			} else if ((q = getCommand(tx, i, to)) >= 0) {
				if (i > from)
					text(tx.substring(from, i));
				processCommand(tx.substring(i+1, q));
				i = q;
				from = i+1;
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

	private int getCommand(String tx, int i, int to) {
		// not a command
		if (tx.charAt(i) != '&')
			return -1;
		// last on the line can't be a command
		if (++i >= to)
			return -1;
		// it's a double ... return the magic value "-2"
		if (tx.charAt(i) == '&')
			return -2;
		if (!Character.isLetterOrDigit(tx.charAt(i)))
			throw new ParsingException("cannot have just &: use && for a single & at " + inputLocation());

		while (i < to && Character.isLetterOrDigit(tx.charAt(i)))
			i++;

		return i;
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
	
	private void processCommand(String cmd) {
		InlineCommandHandler handler = inlineCommands.get(cmd);
		if (handler == null)
			throw new NoSuchCommandException(cmd, inputLocation());
		handler.invoke();
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
	
	public boolean inPara() {
		return this.currPara != null;
	}
	
	public void ensurePara() {
		if (currPara == null) {
			newPara("text");
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

	public void pushFormat(String fmt) {
		fmtStack.add(fmt);
	}

	public void popFormat(String fmt) {
		if (fmtStack.isEmpty())
			throw new CantHappenException("format stack is empty");
		String last = fmtStack.remove(fmtStack .size()-1);
		if (!fmt.equals(last))
			throw new CantHappenException("wanted to remove " + fmt + " but top was " + last);
	}
}
