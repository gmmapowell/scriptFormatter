package com.gmmapowell.script.processor.prose;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.zinutils.collections.ListMap;
import org.zinutils.exceptions.InvalidUsageException;
import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.script.flow.LinkFromTOC;

public class TableOfContents {
	private List<LinkFromTOC> links;
	private final List<String> headings = new ArrayList<>();
	private final File tocfile;
	private File metafile;
	private final JSONObject meta = new JSONObject();
	private final JSONObject anchors;
	private final JSONObject heads;
	private final JSONArray toc;
	private final Map<String, PDPage> anchorPages = new TreeMap<>();
	private ListMap<String, PDAnnotationLink> anchorWaiting = new ListMap<>();
	
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
			String header = spaces(type) + (number == null ? "": number + " ") + title;
			if (anchor != null) {
				h.put("anchor", anchor);
				anchors.put(anchor, h);
				header = header + " [" + anchor + "]";
			}
			headings.add(header);
			return new JSONTOCEntry(this, h);
		} catch (JSONException ex) {
			throw WrappedException.wrap(ex);
		}
	}

	private String spaces(String type) {
		switch (type) {
		case "chapter":
			return "";
		case "section":
			return "  ";
		case "subsection":
			return "    ";
		default:
			return "      ";
		}
	}

	public void recordPage(JSONObject entry, PDPage page, String name) {
		try {
//			System.out.println("recording page " + name + " for " + entry);
			entry.put("page", name);
			if (entry.has("anchor")) {
				String anchor = entry.getString("anchor");
				if (anchorPages.containsKey(anchor))
					throw new InvalidUsageException("duplicate anchor: " + anchor);
				anchorPages.put(anchor, page);
				if (anchorWaiting.contains(anchor)) {
					for (PDAnnotationLink link : anchorWaiting.get(anchor))
						bindLink(link, page);
				}
			}
			if (links != null && !links.isEmpty()) {
				LinkFromTOC next = links.remove(0);
				next.sendTo(page);
//				System.out.println("binding " + next + " to " + page + " with " + name);
			} else {
				System.out.println("out of TOC links");
			}
		} catch (JSONException e) {
			throw WrappedException.wrap(e);
		}
	}

	public void links(List<LinkFromTOC> links) {
		this.links = new ArrayList<>(links);
	}

	public void refAnchor(String anchor, PDAnnotationLink link) {
		if (anchorPages.containsKey(anchor))
			bindLink(link, anchorPages.get(anchor));
		else
			anchorWaiting.add(anchor, link);
	}

	private void bindLink(PDAnnotationLink link, PDPage page) {
		PDActionGoTo topage = new PDActionGoTo();
		PDPageDestination dest = new PDPageXYZDestination();
		dest.setPage(page);
		topage.setDestination(dest);
		link.setAction(topage);
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
}
