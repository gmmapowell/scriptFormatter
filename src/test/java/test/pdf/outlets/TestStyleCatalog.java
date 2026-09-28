package test.pdf.outlets;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.sink.pdf.Stock;
import com.gmmapowell.script.styles.Style;
import com.gmmapowell.script.styles.StyleCatalog;

public class TestStyleCatalog implements StyleCatalog {
	public Map<String, Style> styles = new TreeMap<>();
	private Stock stock;

	public TestStyleCatalog() {
	}

	public TestStyleCatalog(Stock stock) {
		this.stock = stock;
	}

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
		if (stock == null) {
			throw new CantHappenException("cannot getStock() without stock provided");
		}
		return stock;
	}

	@Override
	public void loadFonts(PDDocument doc) throws IOException {
	}
}
