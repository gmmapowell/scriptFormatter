package com.gmmapowell.script.modules.presenter;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.gmmapowell.script.flow.SpanItem;

public class FormatOp implements SpanItem {
	public final String format;

	public FormatOp(String format) {
		this.format = format;
	}

	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void intForm(DataOutputStream os) throws IOException {
		os.write(PresenterModule.ID);
		os.write(PresenterModule.SLIDE_FORMAT_OP);
		os.writeUTF(format);
	}

}
