package com.gmmapowell.script.modules.presenter;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.gmmapowell.script.flow.SpanItem;

public class AspectOp implements SpanItem {
	private final float x;
	private final float y;

	public AspectOp(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		return null;
	}

	@Override
	public void intForm(DataOutputStream os) throws IOException {
		os.write(PresenterModule.ID);
		os.write(PresenterModule.ASPECT_OP);
		os.writeFloat(x);
		os.writeFloat(y);
	}

}
