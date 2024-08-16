package com.gmmapowell.script.modules.presenter;

import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.gmmapowell.script.flow.SpanItem;

public class FieldOp implements SpanItem {
	public final String name;
	public final String sval;

	public FieldOp(String name, String sval) {
		this.name = name;
		this.sval = sval;
	}

	@Override
	public BoundingBox bbox(PDFont font, float sz) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void intForm(DataOutputStream os) throws IOException {
		os.write(PresenterModule.ID);
		os.write(PresenterModule.FIELD_OPTION_OP);
		os.writeUTF(name);
		os.writeUTF(sval);
	}

}
