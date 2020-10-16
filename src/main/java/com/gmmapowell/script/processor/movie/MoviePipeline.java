package com.gmmapowell.script.processor.movie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.zinutils.exceptions.InvalidUsageException;

import com.gmmapowell.script.FilesToProcess;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.sink.pdf.BifoldReam;
import com.gmmapowell.script.sink.pdf.DoubleReam;
import com.gmmapowell.script.sink.pdf.PaperStock;
import com.gmmapowell.script.sink.pdf.Ream;
import com.gmmapowell.script.sink.pdf.SingleReam;
import com.gmmapowell.script.styles.page.MoviePageStyle;

public class MoviePipeline implements Processor {
	public enum Mode {
		COMMENT, SLUG1, SLUG2, NORMAL;
	}

	private final File dramatis;
	private final MovieState state;
	private final Sink sink;
	private final String title;
	private final boolean debug;
	private final Formatter formatter;
	private final String ream;
	private final float width, height;
	private final int blksize;


	public MoviePipeline(File root, ElementFactory ef, Sink outputTo, Map<String, String> options, boolean debug) throws ConfigException {
		this.debug = debug;
		this.state = new MovieState(new TreeMap<String, Flow>());
		this.state.flows.put("main", new Flow("main", true));
		this.formatter = new Formatter(state, debug);
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
		this.sink = outputTo;
		if (options.containsKey("ream")) {
			this.ream  = options.remove("ream");
		} else
			this.ream = "single";
		if (options.containsKey("width")) {
			this.width  = dim(options.remove("width"));
		} else
			this.width = dim("210mm");
		if (options.containsKey("height")) {
			this.height  = dim(options.remove("height"));
		} else
			this.height = dim("297mm");
		if (options.containsKey("blksize")) {
			this.blksize  = Integer.parseInt(options.remove("blksize"));
		} else
			this.blksize = 32;
	}

	@Override
	public void process(FilesToProcess files) throws IOException {
		DramatisPersonae dp;
		try {
			dp = new DramatisPersonae(dramatis);
		} catch (IOException e) {
			System.out.println("Error reading dramatis file: " + e.getMessage());
			return;
		}
		String showTitle = title;
		for (File f : files.included()) {
			if (debug)
				System.out.println("included " + f);
			try (LineNumberReader lnr = new LineNumberReader(new FileReader(f))) {
				processFile(dp, lnr, showTitle);
			} catch (FileNotFoundException ex) {
				System.out.println("Could not process " + f);
			} catch (IOException ex) {
				System.out.println("Error processing " + f + ": " + ex.getMessage());
			}
			showTitle = null;
		}
		for (Entry<String, Flow> e : state.flows.entrySet()) {
			sink.flow(e.getValue());
		}
		sink.render(new PaperStock(makeReam(), new MoviePageStyle(), new MoviePageStyle(), new MoviePageStyle(), new MoviePageStyle()));
	}

	private void processFile(DramatisPersonae dp, LineNumberReader lnr, String showTitle) throws IOException {
		// We have the ability to insert random notes which we want to skip
		// The start of the file is automatically in this mode
		// A new slugline automatically gets you out of it
		state.newSection("main", "section");
		if (showTitle != null)
			formatter.title(showTitle);
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
			} else {
				formatter.scene(text);
			}
			para.delete(0, para.length());
		}
	}

	private boolean isSpeech(String s) {
		int idxC = s.indexOf(':');
		int idxS = s.indexOf(' ');
		boolean isSpeech = idxC != -1 && idxC < idxS;
		return isSpeech;
	}

	private float dim(String value) {
		if (value == null || value.length() < 3)
			throw new InvalidUsageException("value must have units");
		String units = value.substring(value.length()-2);
		float n = Float.parseFloat(value.substring(0, value.length()-2));
		switch (units) {
		case "pt":
			return n;
		case "in":
			return n*72;
		case "mm":
			return n*72/25.4f;
		case "cm":
			return n*72/2.54f;
		default:
			throw new InvalidUsageException("do not understand unit " + units + ": try pt, in, mm, cm");
		}
	}

	private Ream makeReam() {
		switch (ream) {
		case "single":
			return new SingleReam(width, height);
		case "double":
			return new DoubleReam(width, height);
		case "bifold":
			return new BifoldReam(blksize, width, height);
		default:
			throw new InvalidUsageException("there is no ream " + ream);
		}
	}
}
