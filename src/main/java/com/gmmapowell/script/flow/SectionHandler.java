package com.gmmapowell.script.flow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.zinutils.collections.CollectionUtils;
import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.sink.pdf.PageCompositor;
import com.gmmapowell.script.sink.pdf.Suspension;

public class SectionHandler implements CursorFeedback {
	private CursorClient cc;
	Set<AnchorOp> records;
	Set<Cursor> active;
	List<Suspension> suspended;
	private Set<Cursor> cursors;


	public SectionHandler(CursorClient cc) {
		this.cc = cc;
		this.suspended = new ArrayList<>();
		this.records = new HashSet<>();
	}
	
	public void beginSection(Set<Cursor> currentCursors) {
		this.cursors = currentCursors;
		cc.beginSection(cursors);
	}

	public void doSection() throws IOException {
		System.out.println("calling doSection");
		this.active = new TreeSet<>(cursors);
		while (!active.isEmpty()) {
			Cursor c = firstActive();
			StyledToken tok;
			while ((tok = c.next()) != null) {
				System.out.println(tok);
				if (tok.it instanceof AnchorOp) {
					records.add((AnchorOp)tok.it);
				} else if (tok.it instanceof ReleaseFlow) {
					Cursor en = findFlow(suspended, cursors, ((ReleaseFlow)tok.it).release());
					if (en == c) {
						throw new CantHappenException("can't enable the one you're suspending");
					}
					active.add(en);
					cursors.add(en);
				} else {
					if (!cc.processToken(this, tok))
						break;
				}
			}
			if (tok == null) {
				cursors.remove(c);
				active.remove(c);
			}
		}
	}
	
	public void endSection() throws IOException {
		cc.endSection();
	}
	

	@Override
	public void allProcessed(PageCompositor page) {
		for (AnchorOp anch : records) {
			anch.recordPage(page.meta(), page.currentPageName());
		}
		records.clear();
	}
	
	@Override
	public void backTo(StyledToken lastAccepted) {
		firstActive().backTo(lastAccepted);
	}

	@Override
	public void noRoom(StyledToken lastAccepted) {
		backTo(lastAccepted);
		active.remove(firstActive());
		records.clear();
	}

	@Override
	public void suspend(StyledToken lastAccepted, String enable) {
		Cursor c = firstActive();
		suspended.add(new Suspension(c, lastAccepted));
		Cursor en = findFlow(suspended, cursors, enable);
		if (en == c) {
			throw new CantHappenException("can't enable the one you're suspending");
		}
		active.add(en);
		cursors.add(en);
		active.remove(c);
		cursors.remove(c);
	}

	private Cursor firstActive() {
		return CollectionUtils.nth(active, 0);
	}
	
	private Cursor findFlow(List<Suspension> suspended, Set<Cursor> sections, String enable) {
		for (Suspension susp : suspended) {
			if (susp.isFlow(enable)) {
				suspended.remove(susp);
				return susp.cursor;
			}
		}
		for (Cursor c : sections) {
			if (c.isFlow(enable))
				return c;
		}
		throw new CantHappenException("could not enable flow " + enable + " because it did not exist");
	}
}
