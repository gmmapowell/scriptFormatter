package com.gmmapowell.script.processor.movie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Map;

import com.gmmapowell.script.FilesToProcess;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.Sink;

public class MoviePipeline implements Processor {
	public enum Mode {
		COMMENT, SLUG1, SLUG2, NORMAL;
	}

	private final File dramatis;
	private final String title;
	private final boolean debug;
	private final Formatter formatter;

	public MoviePipeline(File root, ElementFactory ef, Sink outputTo, Map<String, String> options, boolean debug) throws ConfigException {
		this.debug = debug;
		this.formatter = new Formatter(ef, outputTo, debug);
		this.title = options.remove("title");
		if (title == null)
			throw new ConfigException("There is no definition of title");
		String d = options.remove("dramatis");
		if (d == null)
			throw new ConfigException("There is no definition of dramatis");
		File df = new File(d);
		if (df.isAbsolute())
			this.dramatis = df;
		else
			this.dramatis = new File(root, d);
	}

	@Override
	public void process(FilesToProcess files) {
		DramatisPersonae dp;
		try {
			dp = new DramatisPersonae(dramatis);
		} catch (IOException e) {
			System.out.println("Error reading dramatis file: " + e.getMessage());
			return;
		}
		try {
			formatter.title(title);
		} catch (IOException ex) {
			System.out.println("Error writing title: " + ex.getMessage());
		}
		for (File f : files.included()) {
			if (debug)
				System.out.println("included " + f);
			try (LineNumberReader lnr = new LineNumberReader(new FileReader(f))) {
				processFile(dp, lnr);
			} catch (FileNotFoundException ex) {
				System.out.println("Could not process " + f);
			} catch (IOException ex) {
				System.out.println("Error processing " + f + ": " + ex.getMessage());
			}
		}
		try {
			formatter.close();
		} catch (IOException ex) {
			System.out.println("Error closing output document: " + ex.getMessage());
		}
	}

	private void processFile(DramatisPersonae dp, LineNumberReader lnr) throws IOException {
		// We have the ability to insert random notes which we want to skip
		// The start of the file is automatically in this mode
		// A new slugline automatically gets you out of it
		Mode mode = Mode.COMMENT;
		StringBuilder slug = new StringBuilder();
		StringBuilder para = new StringBuilder();
		String s;
		while ((s = lnr.readLine()) != null) {
			s = s.trim();
			if (s.startsWith("#"))
				continue;
			if (s.length() == 0) {
				flush(dp, para);
				continue;
			}
			if (isSlugLine(s)) {
				flush(dp, para);
				slug.delete(0, slug.length());
				slug.append(s);
				slug.append(".  ");
				mode = Mode.SLUG1;
				continue;
			}
			switch (mode) {
			case COMMENT:
				break;
			case SLUG1: {
				slug.append(s);
				slug.append(" - ");
				mode = Mode.SLUG2;
				break;
			}
			case SLUG2: {
				slug.append(s);
				mode = Mode.NORMAL;
				formatter.slug(slug.toString());
				break;
			}
			case NORMAL: {
				if (isSpeech(s)) {
					flush(dp, para);
				}
				para.append(s);
				para.append(' ');
				break;
			}
			default:
				System.out.println(mode + " ?? - " + s);
				break;
			}
		}
		flush(dp, para);
		formatter.fileDone();
	}

	private boolean isSlugLine(String s) {
		return s.equals("INT") || s.equals("EXT");
	}

	private void flush(DramatisPersonae dp, StringBuilder para) throws IOException {
		if (para.length() > 0) {
			String text = para.toString();
			if (isSpeech(text)) {
				int idxC = text.indexOf(":");
				String spkr = text.substring(0, idxC);
				String name = dp.getSpeaker(spkr);
				if (name == null) {
					System.out.println("There is no dramatis entry for " + spkr);
					name = ":" + spkr + ":";
				}
				formatter.speaker(name);

				text = text.substring(idxC+1).trim();
				if (text.startsWith("(")) {
					int idxP = text.indexOf(")");
					formatter.direction(text.substring(0, idxP+1));
					text = text.substring(idxP+1).trim();
				}
				int idxS;
				while ((idxS = text.indexOf('[')) != -1) {
					int idxClose = text.indexOf(']', idxS);
					String before = text.substring(0, idxS).trim();
					if (before.length() > 0)
						formatter.speech(before);
					String direction = "(" + text.substring(idxS+1, idxClose).trim() + ")";
					if (direction.length() > 0)
						formatter.direction(direction);
					if (idxClose >= text.length())
						text = "";
					else
						text = text.substring(idxClose+1).trim();
				}
				// TODO: need to extract all the (...) and [...] bits
				if (text.length() > 0)
					formatter.speech(text);
				formatter.endSpeech();
			} else
				formatter.scene(text);
			para.delete(0, para.length());
		}
	}

	private boolean isSpeech(String s) {
		int idxC = s.indexOf(':');
		int idxS = s.indexOf(' ');
		boolean isSpeech = idxC != -1 && idxC < idxS;
		return isSpeech;
	}
}
