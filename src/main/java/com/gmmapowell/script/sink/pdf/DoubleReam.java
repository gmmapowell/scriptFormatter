package com.gmmapowell.script.sink.pdf;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import com.gmmapowell.script.styles.PageStyle;

public class DoubleReam extends CommonReam implements Ream {
	private int cnt = 0;
	private final float swid;
	private final float ht;
	private final PDRectangle size;
	private PDPageContentStream stream;
	
	public DoubleReam(float swid, float ht) {
		this.swid = swid;
		this.ht = ht;
		size = new PDRectangle(swid * 2, ht);
	}
	
	@Override
	public PageCompositor newPage(PageStyle left, PageStyle right) throws IOException {
		if (cnt % 2 == 0) {
			if (stream != null)
				stream.close();
			PDPage page = new PDPage(size);
			doc.addPage(page);
			stream = new PDPageContentStream(doc, page);
		}
		if (cnt % 2 == 0 && left == null)
			cnt++;
		SimplePageCompositor ret = new SimplePageCompositor(styles, stream, new PDRectangle(cnt%2*swid, 0, swid, ht), cnt%2 == 0 ? left : right);
		cnt++;
		return ret;
	}
	
	@Override
	protected void closeAllStreams() throws IOException {
		if (stream != null)
			stream.close();
	}
}
