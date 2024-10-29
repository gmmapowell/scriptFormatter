package com.gmmapowell.script.sink.pdf;

import java.awt.Desktop;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.flow.Cursor;
import com.gmmapowell.script.flow.CursorClient;
import com.gmmapowell.script.flow.CursorFeedback;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.FlowCursor;
import com.gmmapowell.script.flow.StyledToken;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.styles.StyleCatalog;
import com.gmmapowell.script.utils.Upload;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class PDFSink implements Sink, CursorClient {
	private final StyleCatalog styles;
	private final Place output;
	private final boolean wantOpen;
	private final String upload;
	private final boolean debug;
	private final String sshid;
	private final List<Flow> flows = new ArrayList<>();
	private final Stock stock;

	public PDFSink(Region root, StyleCatalog styles, String output, boolean wantOpen, String upload, boolean debug, String sshid, VarMap vars) throws IOException, ConfigException {
		if (styles == null)
			throw new ConfigException("must specify a style catalog");
		this.styles = styles;
		this.debug = debug;
		this.sshid = sshid;
		this.output = root.ensurePlacePath(output);
		this.wantOpen = wantOpen;
		this.upload = upload;
		String stockName = null;
		if (!vars.containsKey("stock")) {
			throw new ConfigException("must specify a stock to render to");
		}
		stockName = vars.remove("stock");
		stock = styles.getStock(stockName);
		if (stock == null)
			throw new ConfigException("there was no stock called " + stockName + " specified in the style catalog");
	}

	@Override
	public void prepare() throws Exception {
	}

	@Override
	public void flow(Flow flow) {
		this.flows.add(flow);
	}
	
	@Override
	public void render() throws IOException {
		stock.newDocument(styles);
		FlowCursor c = new FlowCursor(flows);
		c.run(this); // TODO: I feel that actually it should be a newly created object ...
		stock.close(output);
	}
	

	Map<String, String> current = new TreeMap<>();
	PageCompositor page = null;
	boolean newSection = false;

	@Override
	public void beginSection(Set<Cursor> cursors) {
		for (Cursor c : cursors) {
			current.put(c.flowName(), c.format());
		}
		this.newSection = true;
		this.page = null;
	}
	
	@Override
	public boolean processToken(CursorFeedback cursor, StyledToken tok) throws IOException {
		if (page == null) {
			page = stock.getPage(current, newSection);
			page.begin();
		}
		newSection = false;
		Acceptance a = page.token(tok);
		if (a == null) {
			System.out.println("---- a == null, for " + tok);
			return true;
		}
		switch (a.status) {
		case PENDING: // it thinks it may accept it but it may end up having to reject it
			return true;
		case PROCESSED: {// it has taken it and fully processed it
			cursor.allProcessed(page);
			return true;
		}
		case BACKUP: // we are being asked to try again, probably new region
			cursor.backTo(a.lastAccepted);
			return true; // it still uses the same cursor, so it can "carry on"
		case NOROOM: // we are done; the outlet is full
			if (!page.nextRegions())
				page = null;
			cursor.noRoom(a.lastAccepted);
			return false;
		case SUSPEND: // we cannot proceed until we have seen something from elsewhere
			cursor.suspend(a.lastAccepted, a.enable());
			return false;
		default:
			throw new CantHappenException("what is this? " + a.status);
		}
	}

	@Override
	public void endSection() {
	}

	@Override
	public void showFinal() {
		if (!wantOpen)
			return;
		try {
			if (debug)
				System.out.println("Opening " + output);
			Desktop.getDesktop().open(GeoFSUtils.file(output));
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

	@Override
	public void finish() throws Exception {
	}
}
