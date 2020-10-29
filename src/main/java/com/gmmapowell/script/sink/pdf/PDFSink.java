package com.gmmapowell.script.sink.pdf;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.InvalidUsageException;

import com.gmmapowell.script.flow.AnchorOp;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.ReleaseFlow;
import com.gmmapowell.script.flow.Section;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.styles.StyleCatalog;
import com.gmmapowell.script.styles.page.FirstBookPageStyle;
import com.gmmapowell.script.styles.page.LeftBookPageStyle;
import com.gmmapowell.script.styles.page.RightBookPageStyle;
import com.gmmapowell.script.utils.Upload;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class PDFSink implements Sink {
	private final StyleCatalog styles;
	private final File output;
	private final boolean wantOpen;
	private final String upload;
	private final boolean debug;
	private final String sshid;
	private final List<Flow> flows = new ArrayList<>();
	private final PaperStock stock;

	public PDFSink(File root, StyleCatalog styles, String output, boolean wantOpen, String upload, boolean debug, String sshid, Map<String, String> options) throws IOException {
		this.styles = styles;
		this.debug = debug;
		this.sshid = sshid;
		File f = new File(output);
		if (f.isAbsolute())
			this.output = f;
		else
			this.output = new File(root, output);
		this.wantOpen = wantOpen;
		this.upload = upload;
		String ream = "single";
		float width = dim("210mm");
		float height = dim("297mm");
		int blksize = 32;
		if (options.containsKey("ream")) {
			ream  = options.remove("ream");
		}
		if (options.containsKey("width")) {
			width  = dim(options.remove("width"));
		}
		if (options.containsKey("height")) {
			height  = dim(options.remove("height"));
		}
		if (options.containsKey("blksize")) {
			blksize  = Integer.parseInt(options.remove("blksize"));
		}
		stock = new PaperStock(makeReam(ream, width, height, blksize), null, new FirstBookPageStyle(), new LeftBookPageStyle(), new RightBookPageStyle());
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

	private Ream makeReam(String ream, float width, float height, int blksize) {
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

	@Override
	public void flow(Flow flow) {
		this.flows.add(flow);
	}
	
	@Override
	public void render() throws IOException {
		stock.newDocument(styles);
		List<Flow> mainFlows = new ArrayList<>();
		for (Flow f : flows) {
			if (f.isMain()) {
				mainFlows.add(f);
			}
		}
		int i=0;
		Map<String, String> current = new TreeMap<>();
		Set<Cursor> sections = new TreeSet<>();
		PageCompositor page = null;
		boolean beginSection = false;
		forever:
		while (true) {
			for (Flow f : mainFlows) {
				if (f.sections.size() > i) {
					Section si = f.sections.get(i);
					current.put(f.name, si.format);
					sections.add(new Cursor(f.name, si));
					beginSection = true;
				}
			}
			i++;
			if (sections.isEmpty())
				break forever;
				
			List<Suspension> suspended = new ArrayList<>();
			Set<AnchorOp> records = new HashSet<>();
			while (!sections.isEmpty()) {
				if (page == null) {
					page = stock.getPage(current, beginSection);
					page.begin();
				}
				beginSection = false;
				Set<Cursor> active = new TreeSet<>(sections);
				whileActive:
				while (!active.isEmpty()) {
					for (Cursor c : active) { // try and populate each main section
						while (true) {
							StyledToken tok = c.next();
							if (tok == null) {
								sections.remove(c);
								active.remove(c);
								continue whileActive;
							}
//							System.out.println(tok);
							if (tok.it instanceof AnchorOp) {
								records.add((AnchorOp)tok.it);
								continue;
							}
							if (tok.it instanceof ReleaseFlow) {
								Cursor en = findFlow(suspended, sections, ((ReleaseFlow)tok.it).release());
								if (en == c) {
									throw new CantHappenException("can't enable the one you're suspending");
								}
								active.add(en);
								sections.add(en);
								continue whileActive;
							}
							Acceptance a = page.token(tok);
							if (a == null) {
								System.out.println("---- a == null, for " + tok);
								continue;
							}
							switch (a.status) {
							case PENDING: // it thinks it may accept it but it may end up having to reject it
								break;
							case PROCESSED: {// it has taken it and fully processed it
								for (AnchorOp anch : records) {
									anch.recordPage(page.meta(), page.currentPageName());
								}
								records.clear();
								break;
							}
							case BACKUP: // we are being asked to try again, probably new region
								c.backTo(a.lastAccepted);
								continue;
							case NOROOM: // we are done; the outlet is full
								c.backTo(a.lastAccepted);
								active.remove(c);
								records.clear();
								continue whileActive;
							case SUSPEND: // we cannot proceed until we have seen something from elsewhere
								suspended.add(new Suspension(c, a.lastAccepted));
								Cursor en = findFlow(suspended, sections, a.enable());
								if (en == c) {
									throw new CantHappenException("can't enable the one you're suspending");
								}
								active.add(en);
								sections.add(en);
								active.remove(c);
								sections.remove(c);
								continue whileActive;
							}
						}
					}
				}
				boolean advanced = page.nextRegions();
				if (!advanced) {
					page = null;
				}
			}
			if (!suspended.isEmpty())
				throw new CantHappenException("suspended is not empty: " + suspended);
		}
		stock.close(output);
	}

	private Cursor findFlow(List<Suspension> suspended, Set<Cursor> sections, String enable) {
		for (Suspension susp : suspended) {
			if (susp.isFlow(enable)) {
				suspended.remove(susp);
				return susp.cursor;
			}
		}
		for (Cursor c : sections) {
			if (c.isFlow(enable))
				return c;
		}
		throw new CantHappenException("could not enable flow " + enable + " because it did not exist");
	}

	@Override
	public void showFinal() {
		if (!wantOpen)
			return;
		try {
			if (debug)
				System.out.println("Opening " + output);
			Desktop.getDesktop().open(output);
		} catch (Exception e) {
			System.out.println("Failed to open " + output + " on desktop:\n  " + e.getMessage());
		}
	}
	
	@Override
	public void upload() throws JSchException, SftpException {
		if (upload != null) {
			new Upload(output, upload, sshid, true).send();
		}
	}
}
