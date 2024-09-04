package com.gmmapowell.script.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.intf.FilesToProcess;

public class Index implements FilesToProcess {
	public enum Status {
		RECORDED {
			@Override
			String flag() {
				return "+";
			}
		}, INCLUDED {
			@Override
			String flag() {
				return "*";
			}
		}, EXCLUDED {
			@Override
			String flag() {
				return "-";
			}
		};

		abstract String flag();
	}

	public class Known {
		String id;
		String label;
		Status stat;
		boolean alreadyDownloaded;
		
		public Known(String id, String label, Status stat, boolean alreadyDownloaded) {
			this.id = id;
			this.label = label;
			this.stat = stat;
			this.alreadyDownloaded = alreadyDownloaded;
		}
	}

	private final Map<String, Known> current = new LinkedHashMap<>();
	private final Region downloads;
	private Writer appendTo;
	private boolean writtenExcluded;
	private boolean alreadyDownloaded;

	public static Index read(Place indexFile, Region downloads) throws IOException {
		Index index = new Index(downloads);
		index.readFrom(indexFile);
		
		Writer fw = indexFile.appender();
		index.appendTo(fw);
		return index;
	}

	private Index(Region downloads) {
		this.downloads = downloads;
	}
	
	public void readFrom(Place indexFile) throws IOException {
		if (!indexFile.exists())
			return;
		indexFile.lines(s -> {
			s = s.trim();
			if (s.length() == 0 || s.startsWith("#"))
				return;
			if (s.equals("--excluded--")) {
				writtenExcluded = true;
				return;
			} else if (s.equals("--downloaded--")) {
				alreadyDownloaded = true;
				return;
			} else if (s.equals("--download--")) {
				alreadyDownloaded = false;
				return;
			}

			int idx = s.indexOf(" ");
			Known n = new Known(s.substring(0, idx), s.substring(idx+1), writtenExcluded?Status.EXCLUDED:Status.INCLUDED, writtenExcluded || alreadyDownloaded);
			if (!current.containsKey(n.id))
				current.put(n.id, n);
		});
	}

	public void appendTo(Writer fw) {
		this.appendTo = fw;
	}

	public boolean record(String id, Place place) throws IOException {
		if (current.containsKey(id)) {
			return !current.get(id).alreadyDownloaded;
		}
		if (!writtenExcluded) {
			appendTo.append("--excluded--\n");
			writtenExcluded = true;
		}
		appendTo.append(id);
		appendTo.append(" ");
		appendTo.append(place.name());
		appendTo.append("\n");
		return !alreadyDownloaded && !writtenExcluded;
	}

	@Override
	public Iterable<Place> included() {
		List<Place> fs = new ArrayList<>();
		for (Known k : current.values()) {
			if (k.stat == Status.INCLUDED)
				fs.add(downloads.place(k.label));
		}
		return fs;
	}
	
	public void generateWebeditFile(Place webeditFile, String title) throws FileNotFoundException {
		try (PrintWriter pw = new PrintWriter(webeditFile.writer())) {
			pw.println("<html>");
			pw.println("  <head>");
			pw.println("    <title>Contents of " + title + "</title>");
			pw.println("    <style>");
			pw.println("      a { display: block; }");
			pw.println("    </style>");
			pw.println("  </head>");
			pw.println("  <body>");
			pw.println("    <h1>Contents of " + title + "</h1>");
			for (Known k : current.values()) {
				if (k.stat == Status.INCLUDED) {
					pw.println("    <a href='https://docs.google.com/document/d/" + k.id + "'/edit>" + k.label + "</a>");
				}
			}
			pw.println("  </body>");
			pw.println("</html>");
		}
	}

	public void close() throws IOException {
		appendTo.close();
	}
}
