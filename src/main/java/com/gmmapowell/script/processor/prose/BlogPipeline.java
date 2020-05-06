package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Map;

import com.gmmapowell.script.FilesToProcess;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.SpanBlock;
import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.processor.Processor;
import com.gmmapowell.script.sink.Sink;

public class BlogPipeline implements Processor {
	private final Sink sink;
	private final ElementFactory ef;

	public BlogPipeline(File root, ElementFactory ef, Sink sink, Map<String, String> options, boolean debug) throws ConfigException {
		this.ef = ef;
		this.sink = sink;
	}
	
	@Override
	public void process(FilesToProcess files) throws IOException {
		for (File x : files.included()) {
			sink.title(x.getName().replace(".txt", ""));
			SpanBlock curr = null;
			try (LineNumberReader lnr = new LineNumberReader(new FileReader(x))) {
				String s;
				while ((s = lnr.readLine()) != null) {
					s = s.trim();
					if (s.length() == 0) {
						if (curr != null) {
							sink.block(curr);
							curr = null;
						}
						continue;
					}
					if (s.startsWith("+")) {
						if (curr != null) {
							sink.block(curr);
						}
						curr = ef.block(headingLevel(s));
						ProcessingUtils.addSpans(ef, curr, s.substring(s.indexOf(" ")+1).trim());
						continue;
					} else if (s.startsWith("*")) {
						if (curr != null) {
							sink.block(curr);
						}
						curr = ef.block("bullet");
						curr.addSpan(ef.span(null, s.substring(s.indexOf(" ")+1).trim()));
						continue;
					} else {
						if (curr == null) {
							curr = ef.block("text");
						} else
							curr.addSpan(ef.span(null, " "));
						curr.addSpan(ef.span(null, s));
					}
				}
			}
			if (curr != null)
				sink.block(curr);
			sink.close();
		}
	}

	private String headingLevel(String s) {
		int level = 1;
		while (s.length() > level && s.charAt(level) == '+')
			level++;
		return "h" + level;
	}
}
