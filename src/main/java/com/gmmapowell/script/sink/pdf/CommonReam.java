package com.gmmapowell.script.sink.pdf;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import com.gmmapowell.script.styles.StyleCatalog;

public abstract class CommonReam implements Ream {
	protected PDDocument doc;
	protected StyleCatalog styles;

	@Override
	public void newDocument(StyleCatalog styles) throws IOException {
		this.styles = styles;
		doc = new PDDocument();
		loadFonts();
	}

	public void loadFonts() throws IOException {
		styles.font("monospace", (PDFont) PDType0Font.load(doc, this.getClass().getResourceAsStream("/fonts/MonospaceRegular.ttf")));
		styles.font("monospace-bold", (PDFont) PDType0Font.load(doc, this.getClass().getResourceAsStream("/fonts/MonospaceBold.ttf")));
		styles.font("monospace-oblique", (PDFont) PDType0Font.load(doc, this.getClass().getResourceAsStream("/fonts/MonospaceOblique.ttf")));
		styles.font("palatino", (PDFont) PDType0Font.load(doc, this.getClass().getResourceAsStream("/fonts/Palatino.ttf")));
		styles.font("palatino-bold", (PDFont) PDType0Font.load(doc, this.getClass().getResourceAsStream("/fonts/Palatino Bold.ttf")));
		styles.font("palatino-italic", (PDFont) PDType0Font.load(doc, this.getClass().getResourceAsStream("/fonts/Palatino Italic.ttf")));
		styles.font("palatino-bolditalic", (PDFont) PDType0Font.load(doc, this.getClass().getResourceAsStream("/fonts/Palatino Bold Italic.ttf")));
	}

	@Override
	public void close(File output) throws IOException {
		closeAllStreams();
		doc.save(output);
		doc.close();
		this.doc = null;
	}

	protected abstract void closeAllStreams() throws IOException;
}
