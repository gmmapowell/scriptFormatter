package com.gmmapowell.script.flow;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.gmmapowell.script.modules.article.ArticleModule;

public class SectionTitle implements SpanItem {
	private final String title;

	public SectionTitle(String title) {
		this.title = title;
	}
	
	public String title() {
		return title;
	}

	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		return null;
	}

	@Override
	public void intForm(DataOutputStream os) throws IOException {
		os.write(ArticleModule.ID);
		os.write(ArticleModule.SECTION_TITLE);
	}

	@Override
	public String toString() {
		return "Section[" + title + "]";
	}
}
