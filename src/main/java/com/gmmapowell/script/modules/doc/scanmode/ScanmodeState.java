package com.gmmapowell.script.modules.doc.scanmode;

import java.util.ArrayList;
import java.util.List;

import org.zinutils.exceptions.NotImplementedException;

public class ScanmodeState {
	public class NumberCount {
		private final String format;
		private int current;

		public NumberCount(String format, int startAt) {
			this.format = format;
			this.current = startAt;
		}
	}

	private ScanMode scanningFor = ScanMode.NONE;
	private ScanMode currentlyIn = ScanMode.NONE;
	private final List<NumberCount> numbering = new ArrayList<>();

	public void scanningFor(ScanMode scanMode) {
		this.scanningFor = scanMode;
	}
	
	public boolean scanMode(ScanMode mode) {
		this.currentlyIn = mode;
		return scanningFor == ScanMode.DETAILS && (mode == ScanMode.OVERVIEW || mode == ScanMode.DETAILS);
	}

	public boolean ignoring() {
		return this.scanningFor == ScanMode.OVERVIEW && this.currentlyIn == ScanMode.DETAILS;
	}
	
	public void pushNumbering(String format, int startAt) {
		numbering.add(new NumberCount(format, startAt));
	}

	public void popNumbering() {
		numbering.remove(numbering.size()-1);
	}

	public boolean activeNumbering() {
		return !numbering.isEmpty();
	}

	public String numberPara() {
		if (numbering.size() == 1)
			return "number";
		else
			return "number" + numbering.size();
	}

	public String currentNumber() {
		// TODO: support nested numbering
		if (numbering.size() != 1)
			throw new NotImplementedException("multi-level numbering - use @Numbering multiple times");
		// TODO: support non-arabic numbering
		if (!"arabic".equals(numbering.get(0).format))
			throw new NotImplementedException("non-arabic numbering");
		String ret = Integer.toString(numbering.get(0).current) + ".";
		numbering.get(0).current++;
		return ret;
	}
}
