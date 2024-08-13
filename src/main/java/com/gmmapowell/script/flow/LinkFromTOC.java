package com.gmmapowell.script.flow;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.zinutils.exceptions.NotImplementedException;

public class LinkFromTOC implements SpanItem {
	public final String text;
	private final String comment;
	private PDAnnotationLink pending;

	public LinkFromTOC(String text, String comment) {
		this.text = text;
		this.comment = comment;
	}

	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		float width = 0;
		if (font != null) {
			try {
				width = font.getStringWidth(text)*sz/1000;
			} catch (IllegalArgumentException ex) {
			}
		}
		return new BoundingBox(0, 0, width, sz);
	}

	public void pendingTarget(PDAnnotationLink link) {
		this.pending = link;
	}

	public void sendTo(PDPage page) {
		PDActionGoTo topage = new PDActionGoTo();
		PDPageDestination dest = new PDPageXYZDestination();
		dest.setPage(page);
		topage.setDestination(dest);
		if (this.pending != null)
			this.pending.setAction(topage);
	}
	
	@Override
	public String toString() {
		return "LinkFromTOC[" + text + ":" + comment + "]";
	}

	@Override
	public void intForm(DataOutputStream os) throws IOException {
		throw new NotImplementedException();
	}
}
