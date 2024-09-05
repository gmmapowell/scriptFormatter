package com.gmmapowell.script.processor.prose;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.flow.Flow;

public class DocState extends AtState {
	public int chapter = 1;
	public int section;
	public boolean commentary;
	public boolean beginComment;
	public boolean inRefComment;
	public boolean wantSectionNumbering;
	public ScanMode scanMode = ScanMode.NONE;
	public String chapterStyle;

	public DocState(Map<String, Flow> flows) {
		super(flows);
	}

	@Override
	public String formatAs() {
		return "preformatted";
	}
	
	public void newfile(String file) {
		processingFile(file);
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

	public String location() {
		return (chapter-1) + "." + (section-1) + (commentary?"c":"");
	}

}
