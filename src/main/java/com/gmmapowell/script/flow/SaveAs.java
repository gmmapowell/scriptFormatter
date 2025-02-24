package com.gmmapowell.script.flow;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.gmmapowell.script.modules.article.ArticleModule;

public class SaveAs implements SpanItem {
	private final String name;

	public SaveAs(String name) {
		this.name = name;
	}
	
	public String name() {
		return name;
	}

	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		return null;
	}

	@Override
	public void intForm(DataOutputStream os) throws IOException {
		os.write(ArticleModule.ID);
		os.write(ArticleModule.SAVE_AS);
	}

	@Override
	public String toString() {
		return "SaveAs[" + name + "]";
	}
}
