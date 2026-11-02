package com.gmmapowell.script.flow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class FlowCursor {
	private final List<Flow> mainFlows = new ArrayList<>();
	private final boolean separately;
	private List<Flow> configuredFlows;

	public FlowCursor(List<Flow> flows, boolean separately) {
		this.separately = separately;
		for (Flow f : flows) {
			if (f.isMain()) {
				mainFlows.add(f);
			}
		}
	}	

	public void run(CursorClient cc) throws IOException {
		if (separately) {
			runSeparateFlows(cc);
		} else {
			runContinuousFlow(cc);
		}
	}
	
	private void runSeparateFlows(CursorClient cc) throws IOException {
		for (Flow f : mainFlows) {
			this.configuredFlows = buildTmpFlows(f);
			runFlows(cc);
		}
	}

	private void runContinuousFlow(CursorClient cc) throws IOException {
		this.configuredFlows = new ArrayList<>(this.mainFlows);
		runFlows(cc);
	}

	private List<Flow> buildTmpFlows(Flow f) {
		List<Flow> ret = new ArrayList<>();
		for (Flow k : mainFlows) {
			if (!k.isMain())
				ret.add(k);
			else if (k == f)
				ret.add(k.renamedTo("main"));
		}
		return ret;
	}

	public void runFlows(CursorClient cc) throws IOException {
		for (int i=0;haveSection(i);i++) {
			Set<Cursor> sections = sectionsFor(i);
			SectionHandler sh = new SectionHandler(cc);
			sh.beginSection(sections);
			while (!sections.isEmpty()) {
				sh.doSection();
			}
			sh.endSection();
		}
	}

	private boolean haveSection(int i) {
		for (Flow f : configuredFlows) {
			if (f.sections.size() > i)
				return true;
		}
		return false;
	}

	private Set<Cursor> sectionsFor(int i) {
		Set<Cursor> sections = new TreeSet<>();
		for (Flow f : configuredFlows) {
			if (f.sections.size() > i) {
				sections.add(new Cursor(f.name, f.sections.get(i)));
			}
		}
		return sections;
	}
}
