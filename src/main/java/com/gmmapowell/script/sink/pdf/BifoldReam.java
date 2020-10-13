package com.gmmapowell.script.sink.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.styles.PageStyle;

public class BifoldReam extends CommonReam implements Ream {
	private final int blksize;
	private final float swid;
	private final float ht;
	private final PDRectangle size;
	private final List<PDPageContentStream> streams = new ArrayList<>();
	
	public BifoldReam(int blksize, float swid, float ht) {
		if (blksize % 4 != 0)
			throw new CantHappenException("bifold requires multiple of 4");
		this.blksize = blksize;
		this.swid = swid;
		this.ht = ht;
		size = new PDRectangle(swid * 2, ht);
	}
	
	@Override
	public PageCompositor newPage(PageStyle left, PageStyle right) throws IOException {
		PDPageContentStream stream;
		int mod;
		do {
			mod = pageNo % blksize;
			if (mod == 0) {
				closeAllStreams();
				streams.clear();
			}
			if (mod < blksize/2) { // add new pages
				PDPage page = new PDPage(size);
				doc.addPage(page);
				stream = new PDPageContentStream(doc, page);
				streams.add(stream);
			} else {
				stream = streams.get(blksize-mod-1);
			}
		} while (pageNo++ % 2 == 1 && left == null);
		SimplePageCompositor ret = new SimplePageCompositor(styles, stream, new PDRectangle((mod+1)%2*swid, 0, swid, ht), mod%2 == 1 ? left : right);
		return ret;
	}
	
	@Override
	protected void closeAllStreams() throws IOException {
		for (PDPageContentStream stream : streams)
			stream.close();
	}
}
