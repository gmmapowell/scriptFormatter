package com.gmmapowell.script.modules.processors.movie;

import java.io.IOException;

import com.gmmapowell.script.processor.configured.ConfiguredState;

public class MovieMode {
	public enum Mode {
		COMMENT, SLUG1, SLUG2, NORMAL;
	}

	private Formatter formatter;
	private Mode currentMode = Mode.COMMENT;
	private StringBuilder slug;
	private StringBuilder text;
	private DramatisPersonae dp;

	public void configure(ConfiguredState state) {
		this.formatter = new Formatter(state, true);
		dp = state.global().requireState(MovieGlobals.class).dramatis();
	}
	
	public boolean is(Mode what) {
		return currentMode == what;
	}

	public void interior() {
		currentMode = Mode.SLUG1;
		slug = new StringBuilder("INT.  ");
	}

	public void exterior() {
		currentMode = Mode.SLUG1;
		slug = new StringBuilder("EXT.  ");
	}

	public void location(String s) {
		currentMode = Mode.SLUG2;
		slug.append(s);
		slug.append(" - ");
	}

	public void time(String s) throws IOException {
		currentMode = Mode.NORMAL;
		slug.append(s);
		formatter.slug(slug.toString());
		slug = null;
	}

	public void flush() throws IOException {
		if (text != null) {
			System.out.println("Must flush " + text);
			doFormattedFlush();
			text = null;
		}
	}

	public void appendText(String s) {
		if (text == null) {
			text = new StringBuilder();
		} else {
			text.append(" ");
		}
		text.append(s);
	}

	public boolean isSpeech(String s) {
		int idxC = s.indexOf(':');
		int idxS = s.indexOf(' ');
		return idxC != -1 && idxC < idxS;
	}

	private void doFormattedFlush() throws IOException {
		if (text == null) {
			// nothing to do
			return;
		}
		String line = text.toString();
		if (isSpeech(line)) {
			int idxC = line.indexOf(":");
			String spkr = line.substring(0, idxC);
			String name = dp.getSpeaker(spkr);
			if (name == null) {
				System.out.println("There is no dramatis entry for " + spkr);
				name = ":" + spkr + ":";
			}
			formatter.speaker(name);

			line = line.substring(idxC+1).trim();
			if (line.startsWith("(")) {
				int idxP = line.indexOf(")");
				formatter.direction(line.substring(0, idxP+1));
				line = line.substring(idxP+1).trim();
			}
			int idxS;
			while ((idxS = line.indexOf('[')) != -1) {
				int idxClose = line.indexOf(']', idxS);
				String before = line.substring(0, idxS).trim();
				if (before.length() > 0)
					formatter.speech(before);
				String direction = "(" + line.substring(idxS+1, idxClose).trim() + ")";
				if (direction.length() > 0)
					formatter.direction(direction);
				if (idxClose >= line.length())
					line = "";
				else
					line = line.substring(idxClose+1).trim();
			}
			// TODO: need to extract all the (...) and [...] bits
			if (line.length() > 0)
				formatter.speech(line);
			formatter.endSpeech();
		} else {
			formatter.scene(line);
		}
	}

	public void showTitle(String title) throws IOException {
		formatter.title(title);
	}

	public void fileDone() throws IOException {
		formatter.fileDone();
	}
}
