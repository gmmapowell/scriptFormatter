package com.gmmapowell.script.loader.drive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.script.FilesToProcess;

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
		
		public Known(String id, String label, Status stat) {
			this.id = id;
			this.label = label;
			this.stat = stat;
		}
	}

	private final Map<String, Known> current = new LinkedHashMap<>();
	private final File downloads;
	private FileWriter appendTo;
	private boolean writtenExcluded;

	public static Index read(File indexFile, File downloads) throws IOException {
		Index index = new Index(downloads);
		try (FileReader fr = new FileReader(indexFile)) {
			index.readFrom(fr);
		} catch (FileNotFoundException ex) {
			System.out.println(indexFile + " not found; creating");
		}
		
		FileWriter fw = new FileWriter(indexFile, true);
		index.appendTo(fw);
		return index;
	}

	private Index(File downloads) {
		this.downloads = downloads;
	}
	
	public void readFrom(FileReader fr) throws IOException {
		LineNumberReader lnr = new LineNumberReader(fr);
		String s;
		while ((s = lnr.readLine()) != null) {
			s = s.trim();
			if (s.length() == 0 || s.startsWith("#"))
				continue;
			if (s.equals("--excluded--")) {
				writtenExcluded = true;
				continue;
			}
			int idx = s.indexOf(" ");
			Known n = new Known(s.substring(0, idx), s.substring(idx+1), writtenExcluded?Status.EXCLUDED:Status.INCLUDED);
			current.put(n.id, n);
		}
	}

	public void appendTo(FileWriter fw) {
		this.appendTo = fw;
	}

	public Status record(String id, Place place) throws IOException {
		if (current.containsKey(id)) {
			return current.get(id).stat;
		}
		if (!writtenExcluded) {
			appendTo.append("--excluded--\n");
			writtenExcluded = true;
		}
		appendTo.append(id);
		appendTo.append(" ");
		appendTo.append(place.name());
		appendTo.append("\n");
		return Status.RECORDED;
	}

	@Override
	public Iterable<File> included() {
		List<File> fs = new ArrayList<>();
		for (Known k : current.values()) {
			if (k.stat == Status.INCLUDED)
				fs.add(new File(downloads, k.label));
		}
		return fs;
	}
	
	public void generateWebeditFile(File webeditFile, String title) throws FileNotFoundException {
		try (PrintWriter pw = new PrintWriter(webeditFile)) {
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
