package com.gmmapowell.script.sink.pdf;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.gmmapowell.script.styles.PageStyle;

public class DoubleReam extends CommonReam implements Ream {
	private final float swid;
	private final float ht;
	private final PDRectangle size;
	private PDPage meta;
	private PDPageContentStream stream;
	
	public DoubleReam(float swid, float ht) {
		this.swid = swid;
		this.ht = ht;
		size = new PDRectangle(swid * 2, ht);
	}
	
	@Override
	public PageCompositor newPage(PageStyle left, PageStyle right) throws IOException {
		if (pageNo % 2 == 0) {
			if (stream != null)
				stream.close();
			meta = new PDPage(size);
			doc.addPage(meta);
			stream = new PDPageContentStream(doc, meta);
		}
		if (pageNo % 2 == 0 && left == null)
			pageNo++;
		SimplePageCompositor ret = new SimplePageCompositor(this, styles, meta, stream, Integer.toString(pageNo), new PDRectangle(pageNo%2*swid, 0, swid, ht), pageNo%2 == 0 ? left : right);
		pageNo++;
		return ret;
	}
	
	@Override
	protected void closeAllStreams() throws IOException {
		if (stream != null)
			stream.close();
	}
}
