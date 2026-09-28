package test.pdf.outlets;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.sink.pdf.PaperStock;
import com.gmmapowell.script.sink.pdf.Ream;
import com.gmmapowell.script.sink.pdf.SingleReam;
import com.gmmapowell.script.sink.pdf.Stock;
import com.gmmapowell.script.styles.PageStyle;
import com.gmmapowell.script.styles.Style;
import com.gmmapowell.script.styles.StyleCatalog;
import com.gmmapowell.script.styles.page.DefaultPageStyle;

public class TestStyleCatalog implements StyleCatalog {
	public Ream ream = new SingleReam(440, 640);
	public Map<String, Style> styles = new TreeMap<>();

	@Override
	public Style get(String style) {
		throw new NotImplementedException();
	}

	@Override
	public Style getOptional(String s) {
		return styles.get(s);
	}

	@Override
	public void font(String string, PDFont load) {
		throw new NotImplementedException();
	}

	@Override
	public PDFont getFont(String font, Boolean italic, Boolean bold) {
		return PDType1Font.COURIER;
	}

	@Override
	public Stock getStock(String stockName) throws ConfigException {
		PageStyle left = new DefaultPageStyle();
		return new PaperStock(ream, left, left, left, left);
	}

	@Override
	public void loadFonts(PDDocument doc) throws IOException {
	}
}
