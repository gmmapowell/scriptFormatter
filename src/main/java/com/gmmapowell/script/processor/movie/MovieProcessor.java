package com.gmmapowell.script.processor.movie;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ProcessorConfig;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.intf.FilesToProcess;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.processor.prose.LineCommand;
import com.gmmapowell.script.sink.Sink;

public class MovieProcessor implements Processor, ProcessorConfig {
	public enum Mode {
		COMMENT, SLUG1, SLUG2, NORMAL;
	}

	private final Place dramatis;
	private final MovieState state;
	private final Sink sink;
	private final String title;
	private final boolean debug;
	private final Formatter formatter;

	public MovieProcessor(Region root, ElementFactory ef, Sink outputTo, VarMap options, boolean debug) throws ConfigException {
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
		this.dramatis = root.place(d);
		this.sink = outputTo;
	}

	@Override
	public void installCommand(String cmd, Class<? extends LineCommand> proc, Object cfg) throws ConfigException {
		throw new NotImplementedException();
	}

	@Override
	public void process(FilesToProcess places) throws IOException {
		DramatisPersonae dp;
		try {
			dp = new DramatisPersonae(dramatis);
		} catch (IOException e) {
			System.out.println("Error reading dramatis file: " + e.getMessage());
			return;
		}
		String showTitle = title;
		for (Place f : places.included()) {
			if (debug)
				System.out.println("included " + f);
			processFile(dp, f, showTitle);
			showTitle = null;
		}
		for (Entry<String, Flow> e : state.flows.entrySet()) {
			sink.flow(e.getValue());
		}
		sink.render();
	}

	private void processFile(DramatisPersonae dp, Place f, String showTitle) throws IOException {
		// We have the ability to insert random notes which we want to skip
		// The start of the file is automatically in this mode
		// A new slugline automatically gets you out of it
		state.newSection("main", "section");
		if (showTitle != null)
			formatter.title(showTitle);
		AtomicReference<Mode> mode = new AtomicReference<>(Mode.COMMENT);
		StringBuilder slug = new StringBuilder();
		StringBuilder para = new StringBuilder();
		f.lines(s -> {
			try {
				s = s.trim();
				if (s.startsWith("#"))
					return;
				if (s.length() == 0) {
					flush(dp, para);
					return;
				}
				if (isSlugLine(s)) {
					flush(dp, para);
					slug.delete(0, slug.length());
					slug.append(s);
					slug.append(".  ");
					mode.set(Mode.SLUG1);
					return;
				}
				switch (mode.get()) {
				case COMMENT:
					break;
				case SLUG1: {
					slug.append(s);
					slug.append(" - ");
					mode.set(Mode.SLUG2);
					break;
				}
				case SLUG2: {
					slug.append(s);
					mode.set(Mode.NORMAL);
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
			} catch (Exception ex) {
				System.out.println("Error processing " + f + ": " + ex.getMessage());
			}
		});
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
}
