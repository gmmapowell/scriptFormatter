package com.gmmapowell.script.modules.presenter;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.gmmapowell.script.flow.SpanItem;

public class BgColorOp implements SpanItem {
	private final String color;

	public BgColorOp(String color) {
		this.color = color;
	}

	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void intForm(DataOutputStream os) throws IOException {
		os.write(PresenterModule.ID);
		os.write(PresenterModule.BG_COLOR_OP);
		os.writeUTF(color);
	}

}
