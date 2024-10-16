package com.gmmapowell.script.flow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class FlowCursor {
	private final List<Flow> mainFlows = new ArrayList<>();

	public FlowCursor(List<Flow> flows) {
		for (Flow f : flows) {
			if (f.isMain()) {
				mainFlows.add(f);
			}
		}
	}

	public void run(CursorClient cc) throws IOException {
		for (int i=0;haveSection(i);i++) {
			new SectionHandler(cc).doSection(sectionsFor(i));
		}
	}

	private boolean haveSection(int i) {
		for (Flow f : mainFlows) {
			if (f.sections.size() > i)
				return true;
		}
		return false;
	}

	private Set<Cursor> sectionsFor(int i) {
		Set<Cursor> sections = new TreeSet<>();
		for (Flow f : mainFlows) {
			if (f.sections.size() > i) {
				sections.add(new Cursor(f.name, f.sections.get(i)));
			}
		}
		return sections;
	}
}
