package com.gmmapowell.script.loader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

			@Override
			boolean download() {
				return false;
			}
		},
		INCLUDED {
			@Override
			String flag() {
				return "*";
			}

			@Override
			boolean download() {
				return true;
			}
		},
		EXCLUDED {
			@Override
			String flag() {
				return "-";
			}

			@Override
			boolean download() {
				return false;
			}
		};

		abstract String flag();
		abstract boolean download();
	}

	public class Known {
		String id;
		Date lm;
		String label;
		Status stat;

		public Known(String id, Date lm, String label, Status stat) {
			this.id = id;
			this.lm = lm;
			this.label = label;
			this.stat = stat;
		}
	}

	private final Map<String, Known> current = new LinkedHashMap<>();
	private final List<String> records = new ArrayList<>();
	private final Region downloads;
	private final Place indexFile;
	private boolean writtenExcluded;
	
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

	public static Index read(Place indexFile, Region downloads) throws IOException {
		Index index = new Index(downloads, indexFile);
		index.read();

		return index;
	}

	private Index(Region downloads, Place indexFile) {
		this.downloads = downloads;
		this.indexFile = indexFile;
	}

	public void read() throws IOException {
		if (!indexFile.exists())
			return;
		indexFile.lines(s -> {
			records.add(s);
			s = s.trim();
			if (s.length() == 0 || s.startsWith("#"))
				return;
			if (s.equals("--excluded--")) {
				writtenExcluded = true;
				return;
			} else if (s.equals("--downloaded--")) {
				records.remove(records.size()-1);
				return;
			} else if (s.equals("--download--")) {
				records.remove(records.size()-1);
				return;
			}

			int idx = s.indexOf(" ");
			int idx2 = s.indexOf(" ", idx+1);
			Date lm = null;
			String name = s.substring(idx + 1);
			if (idx2 != -1) {
				try {
					lm = sdf.parse(name);
					name = s.substring(idx2+1);
				} catch (ParseException ex) {
//					ex.printStackTrace();
				}
			}
			Known n = new Known(s.substring(0, idx), lm, name,
					writtenExcluded ? Status.EXCLUDED : Status.INCLUDED);
			if (!current.containsKey(n.id))
				current.put(n.id, n);
		});
	}

	public Status record(String id, String place, Date lastModified) throws IOException {
		if (current.containsKey(id)) {
			Known known = current.get(id);
			if (known.stat == Status.EXCLUDED) {
				updateRecord(id, buildRecord(id, null, place));
				return known.stat;
			}
			updateRecord(id, buildRecord(id, lastModified, place));
			if (known.lm == null || lastModified.after(known.lm))
				return Status.INCLUDED;
			else
				return Status.RECORDED;
		}
		if (!writtenExcluded) {
			records.add("--excluded--");
			writtenExcluded = true;
		}
		records.add(buildRecord(id, null, place));
		return Status.EXCLUDED;
	}

	private String buildRecord(String id, Date lastModified, String place) {
		StringBuilder appendTo = new StringBuilder();
		appendTo.append(id);
		if (lastModified != null) {
			appendTo.append(" ");
			appendTo.append(sdf.format(lastModified));
		}
		appendTo.append(" ");
		appendTo.append(place);
		return appendTo.toString();
	}
	private void updateRecord(String id, String s) {
		for (int i=0;i<records.size();i++) {
			if (records.get(i).startsWith(id + " ")) {
				records.set(i, s);
				break;
			}
		}
	}
	
	@Override
	public Iterable<LabelledPlace> included() {
		List<LabelledPlace> fs = new ArrayList<>();
		for (Known k : current.values()) {
			if (k.stat == Status.INCLUDED)
				fs.add(new LabelledPlace(k.label, downloads.place(k.label)));
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
					pw.println(
							"    <a href='https://docs.google.com/document/d/" + k.id + "'/edit>" + k.label + "</a>");
				}
			}
			pw.println("  </body>");
			pw.println("</html>");
		}
	}

	public void close() throws IOException {
		Writer w = indexFile.writer();
		for (String s : records) {
			w.write(s);
			w.write('\n');
		}
		w.close();
	}
}
