package com.gmmapowell.script.sink.pdf;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.gmmapowell.script.styles.PageStyle;

public class SingleReam extends CommonReam implements Ream {
	private final float wid;
	private final float ht;
	private final PDRectangle size;
	private PDPageContentStream stream;
	
	public SingleReam(float wid, float ht) {
		this.wid = wid;
		this.ht = ht;
		size = new PDRectangle(wid, ht);
	}
	
	@Override
	public PageCompositor newPage(PageStyle left, PageStyle right) throws IOException {
		if (stream != null)
			stream.close();
		PDPage meta = new PDPage(size);
		doc.addPage(meta);
		stream = new PDPageContentStream(doc, meta);
		SimplePageCompositor ret = new SimplePageCompositor(this, styles, meta, stream, new PDRectangle(0, 0, wid, ht), right);
		pageNo++;
		return ret;
	}
	
	@Override
	protected void closeAllStreams() throws IOException {
		if (stream != null)
			stream.close();
	}
}
