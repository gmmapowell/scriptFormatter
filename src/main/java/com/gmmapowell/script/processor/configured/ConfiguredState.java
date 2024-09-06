package com.gmmapowell.script.processor.configured;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zinutils.exceptions.NotImplementedException;
import org.zinutils.reflection.Reflection;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.script.config.ExtensionPointRepo;
import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.flow.SpanItem;
import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.modules.processors.doc.InlineDocCommandState;
import com.gmmapowell.script.processor.NoSuchCommandException;
import com.gmmapowell.script.processor.ParsingException;
import com.gmmapowell.script.utils.SBLocation;

public class ConfiguredState extends SBLocation {
	private final GlobalState global;
	private final Map<Class<?>, Object> configs = new HashMap<>();
	private final ExtensionPointRepo eprepo;
	protected final Fluency fluency;
	private final boolean joinspace;
	private boolean ignoreBlanks = true;
	private Map<String, InlineCommandHandler> inlineCommands;

	public ConfiguredState(GlobalState global, ExtensionPointRepo eprepo, Fluency fluency, boolean joinspace, Place x) {
		this.global = global;
		this.eprepo = eprepo;
		this.fluency = fluency;
		this.joinspace = joinspace;
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

	public void noCommandsText(String tx) {
		newSpan();
		try {
			int from = 0;
			for (int i=0;i<tx.length();i++) {
				if (Character.isWhitespace(tx.charAt(i))) {
					if (from < i) {
						text(tx.substring(from, i));
						op(new BreakingSpace());
					}
					from = i+1;
				}
			}
			if (from < tx.length()) {
				text(tx.substring(from));
			}
		} finally {
			endSpan();
		}
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

	public void processTextInSpan(String tx) {
		processPart(tx, 0, tx.length());
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
		fluency.switchToFlow(flow);
	}

	public void newSection(String flow, String format) {
		fluency.newSection(flow, format);
	}

	public boolean inPara() {
		return fluency.inPara();
	}

	public void ensurePara() {
		fluency.ensurePara();
	}

	public void newPara(List<String> formats) {
		fluency.newPara(formats);
	}

	public void newPara(String... formats) {
		fluency.newPara(formats);
	}

	public void endPara() {
		fluency.endPara();
	}

	public void abortPara() {
		fluency.abortPara();
	}

	public boolean inSpan() {
		return fluency.inSpan();
	}

	public void newSpan(List<String> formats) {
		fluency.newSpan(formats);
	}

	public void newSpan(String... formats) {
		fluency.newSpan(formats);
	}

	public void endSpan() {
		fluency.endSpan();
	}

	public void nestSpan(List<String> formats) {
		fluency.nestSpan(formats);
	}

	public void nestSpan(String... formats) {
		fluency.nestSpan(formats);
	}

	public boolean topSpanHas(String fmt) {
		return fluency.topSpanHas(fmt);
	}

	public void popSpan() {
		fluency.popSpan();
	}

	public void text(String tx) {
		fluency.text(tx);
	}

	public void op(SpanItem op) {
		fluency.op(op);
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

	public void pushFormat(String... fmt) {
		for (String f : fmt)
			fluency.pushFormat(f);
	}

	public void popFormat(String... fmt) {
		for (String f : fmt)
			fluency.popFormat(f);
	}

	public boolean joinspace() {
		return joinspace;
	}
}
