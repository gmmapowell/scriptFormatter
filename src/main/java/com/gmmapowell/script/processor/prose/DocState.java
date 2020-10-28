package com.gmmapowell.script.processor.prose;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.flow.Flow;

public class DocState extends CurrentState {
	public enum ScanMode {
		NONE, OVERVIEW, DETAILS, CONCLUSION
	}

	public class NumberCount {
		private final String format;
		private int current;

		public NumberCount(String format, int startAt) {
			this.format = format;
			this.current = startAt;
		}
	}

	public DocCommand cmd;
	public InlineCommand inline;
	public int chapter = 1;
	public int section;
	public boolean commentary;
	public boolean beginComment;
	public boolean inRefComment;
	public boolean wantSectionNumbering;
	public boolean blockquote;
	private final List<NumberCount> numbering = new ArrayList<>();
	public ScanMode scanMode = ScanMode.NONE;
	public String chapterStyle;

	public DocState(Map<String, Flow> flows) {
		super(flows);
	}

	public void newfile(String file) {
		this.file = file;
		this.numbering.clear();
		this.scanMode = ScanMode.NONE;
	}

	public void reset() {
		cmd = null;
		section = 0;
		commentary = false;
		nextFnMkr = 1;
		nextFnText = 1;
		this.inRefComment = false;
	}

	@Override
	public void line(int lineNumber) {
		this.line = lineNumber;
	}

	@Override
	public String inputLocation() {
		return file + ":" + line;
	}
	
	public String location() {
		return (chapter-1) + "." + (section-1) + (commentary?"c":"");
	}

	public void pushNumbering(String format, int startAt) {
		numbering.add(new NumberCount(format, startAt));
	}

	public void popNumbering() {
		numbering.remove(numbering.size()-1);
	}

	public void resetNumbering() {
		chapter = 1;
		section = 0;
		commentary = false;
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
		String ret = Integer.toString(numbering.get(0).current) + ".";
		numbering.get(0).current++;
		return ret;
	}
}
