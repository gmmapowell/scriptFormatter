package com.gmmapowell.script.sink.pdf;

import java.awt.Desktop;
import java.io.File;

import com.gmmapowell.script.sink.Sink;

public class PDFSink implements Sink {
	private final File output;
	private final boolean wantOpen;

	public PDFSink(File root, String output, boolean wantOpen) {
		File f = new File(output);
		if (f.isAbsolute())
			this.output = f;
		else
			this.output = new File(root, output);
		this.wantOpen = wantOpen;
	}

	@Override
	public void showFinal() {
		if (!wantOpen)
			return;
		try {
			Desktop.getDesktop().open(output);
		} catch (Exception e) {
			System.out.println("Failed to open " + output + " on desktop:\n  " + e.getMessage());
		}
	}

}
