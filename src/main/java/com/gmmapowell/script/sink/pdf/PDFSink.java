package com.gmmapowell.script.sink.pdf;

import java.awt.Desktop;
import java.io.File;

import com.gmmapowell.script.sink.Sink;

public class PDFSink implements Sink {
	private final File output;

	public PDFSink(File root, String output) {
		File f = new File(output);
		if (f.isAbsolute())
			this.output = f;
		else
			this.output = new File(root, output);
	}

	@Override
	public void showFinal() {
		try {
			Desktop.getDesktop().open(output);
		} catch (Exception e) {
			System.out.println("Failed to open " + output + " on desktop:\n  " + e.getMessage());
		}
	}

}
