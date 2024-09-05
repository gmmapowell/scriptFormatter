package com.gmmapowell.script.modules.doc.toc;

import java.io.DataOutputStream;

import org.apache.pdfbox.pdmodel.PDPage;

public interface TOCEntry {

	void recordPage(PDPage meta, String name);

	void intForm(DataOutputStream os);

}
