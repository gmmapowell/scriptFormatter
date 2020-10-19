package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.pdfbox.pdmodel.PDPage;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.script.flow.LinkFromTOC;

public class TableOfContents {
	private final List<String> headings = new ArrayList<>();
	private final File tocfile;
	private File metafile;
	private final JSONObject meta = new JSONObject();
	private final JSONObject anchors;
	private final JSONObject heads;
	private final JSONArray toc;
	private final Map<String, PDPage> pages = new TreeMap<>();
	private List<LinkFromTOC> links;
	
	public TableOfContents(File tocfile, File metafile) {
		this.tocfile = tocfile;
		this.metafile = metafile;
		try {
			anchors = new JSONObject();
			meta.put("anchors", anchors);
			heads = new JSONObject();
			meta.put("headings", heads);
			toc = new JSONArray();
			meta.put("toc", toc);
		} catch (JSONException ex) {
			throw WrappedException.wrap(ex);
		}
	}

	public TOCEntry chapter(String anchor, String number, String title) {
		return heading("chapter", anchor, number, title);
	}

	public TOCEntry section(String anchor, String number, String title) {
		return heading("section", anchor, number, title);
	}

	public TOCEntry subsection(String anchor, String number, String title) {
		return heading("subsection", anchor, number, title);
	}
	
	public TOCEntry subsubsection(String anchor, String number, String title) {
		return heading("subsubsection", anchor, number, title);
	}
	
	private TOCEntry heading(String type, String anchor, String number, String title) {
		String header = (number == null ? "": number + " ") + title;
		headings.add(header);
		try {
			JSONObject h = new JSONObject();
			h.put("type", type);
			h.put("title", title);
			if (number != null) {
				h.put("number", number);
				heads.put(number, h);
				toc.put(number);
			} else {
				toc.put(h);
			}
			if (anchor != null) {
				h.put("anchor", anchor);
				anchors.put(anchor, h);
			}
			return new JSONTOCEntry(this, h);
		} catch (JSONException ex) {
			throw WrappedException.wrap(ex);
		}
	}

	public void recordPage(JSONObject entry, PDPage page, String name) {
		try {
			System.out.println("recording page " + name + " for " + entry);
			entry.put("page", name);
			if (entry.has("anchor")) {
				String anchor = entry.getString("anchor");
				pages.put(anchor, page);
				// TODO: notify anybody waiting
			}
			if (links != null && !links.isEmpty()) {
				LinkFromTOC next = links.remove(0);
				next.sendTo(page);
				System.out.println("binding " + next + " to " + page + " with " + name);
			} else {
				System.out.println("out of links");
			}
		} catch (JSONException e) {
			throw WrappedException.wrap(e);
		}
	}

	public void write() throws FileNotFoundException {
		if (tocfile != null) {
			try (PrintWriter pw = new PrintWriter(tocfile)) {
				for (String h : headings)
					pw.println(h);
			}
		}
		if (metafile != null) {
			try (PrintWriter pw = new PrintWriter(metafile)) {
				pw.print(meta);
			}
		}
	}

	public void links(List<LinkFromTOC> links) {
		this.links = new ArrayList<>(links);
	}
}
