package com.gmmapowell.script.styles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.zinutils.collections.ListMap;
import org.zinutils.exceptions.InvalidUsageException;
import org.zinutils.exceptions.NotImplementedException;
import org.zinutils.reflection.Reflection;
import org.zinutils.utils.StringUtil;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.sink.pdf.BifoldReam;
import com.gmmapowell.script.sink.pdf.DoubleReam;
import com.gmmapowell.script.sink.pdf.PaperStock;
import com.gmmapowell.script.sink.pdf.Ream;
import com.gmmapowell.script.sink.pdf.SingleReam;
import com.gmmapowell.script.sink.pdf.Stock;
import com.gmmapowell.script.styles.page.ConfigurablePageStyle;
import com.gmmapowell.script.styles.simple.CompoundStyle;
import com.gmmapowell.script.styles.simple.SimpleStyle;

public class ConfigurableStyleCatalog extends FontCatalog implements StyleCatalog {
	private final Map<String, Style> catalog = new TreeMap<>();
	private final Style defaultStyle;
	private final Set<String> missed = new TreeSet<>();
	private final Map<String, String> fontStreams = new TreeMap<>();
	private final Map<String, Stock> stocks = new TreeMap<>();
	private final Map<String, PageStyle> pages = new TreeMap<>();

	public ConfigurableStyleCatalog(File file, boolean debug) throws IOException, ConfigException {
		try (LineNumberReader lnr = new LineNumberReader(new FileReader(file))) {
			ListMap<String, String> curr = null; 
			String s;
			while ((s = lnr.readLine()) != null) {
				if (s.length() == 0 || s.startsWith("#"))
					continue;
				boolean nested = Character.isWhitespace(s.charAt(0));
				s = s.trim();
				if (s.length() == 0 || s.startsWith("#"))
					continue;
				String key, value;
				{
					int idx = s.indexOf(' ');
					if (idx == -1) {
						key = s;
						value = null;
					} else { 
						key = s.substring(0, idx);
						value = s.substring(idx+1).trim();
					}
				}
				if (value == null)
					throw new InvalidUsageException("command " + key + " must have a value");
				if (!nested) {
					if (curr != null) {
						// finish off the existing one
						build(debug, curr);
					}
					curr = new ListMap<>();
					curr.add("_type", key);
					curr.add("_name", value);
				} else if (curr == null) {
					System.out.println(lnr.getLineNumber() + ": must have style to nest inside: " + s);
				} else
					curr.add(key, value);
			}
			if (curr != null)
				build(debug, curr);
			if (catalog.containsKey("default"))
				defaultStyle = catalog.get("default");
			else
				throw new ConfigException("no default style was defined in " + file);
		} catch (FileNotFoundException ex) {
			throw new ConfigException("Could not open " + file);
		}
	}

	private void build(boolean debug, ListMap<String, String> curr) throws ConfigException {
		String type = curr.get("_type").get(0);
		String name = curr.get("_name").get(0);
		if (type.equals("para") || type.equals("text"))
			buildStyle(debug, type, name, curr);
		else if (type.equals("pagestyle"))
			pageStyle(debug, type, name, curr);
		else if (type.equals("stock"))
			stock(debug, type, name, curr);
		else if (type.equals("font"))
			recordFont(debug, type, name, curr);
		else
			throw new NotImplementedException("cannot handle " + type);
	}

	private void buildStyle(boolean debug, String type, String name, ListMap<String, String> curr) throws ConfigException {
		SimpleStyle overrides = new SimpleStyle(this);
		if (debug)
			System.out.println("building new style " + name);
		for (String s : curr.keySet()) {
			if (s.startsWith("_") || s.equals("inherit"))
				continue;
			set(overrides, s, curr.get(s));
		}
		Style s = overrides;
		if (curr.contains("inherit")) {
			for (String ih : curr.get("inherit")) {
				Style parent = this.catalog.get(ih);
				if (parent == null) {
					System.out.println(name + " cannot inherit from non-existent " + ih);
					continue;
				}
				s = new CompoundStyle(this, s, parent);
			}
		}
		if (this.catalog.containsKey(name)) {
			System.out.println("duplicate format: " + name);
			return;
		}
		this.catalog.put(name, s);
	}

	private void recordFont(boolean debug, String type, String name, ListMap<String, String> curr) throws ConfigException {
		if (curr.contains("stream")) {
			String stream = curr.get("stream").get(0);
			fontStreams.put(name, stream);
//			catalog.font(name, (PDFont) PDType0Font.load(doc, this.getClass().getResourceAsStream("/fonts/MonospaceRegular.ttf")));
		} else
			throw new ConfigException("no stream for font " + name);
	}

	private void pageStyle(boolean debug, String type, String name, ListMap<String, String> curr) throws ConfigException {
		if (pages.containsKey(name))
			throw new ConfigException("duplicate page style " + name);
		ConfigurablePageStyle ps = new ConfigurablePageStyle();
		pages.put(name, ps);
		for (String s : curr.keySet()) {
			if (s.startsWith("_"))
				continue;
			set(ps, s, curr.get(s));
		}
	}

	private void stock(boolean debug, String type, String name, ListMap<String, String> curr) throws ConfigException {
		if (stocks.containsKey(name))
			throw new ConfigException("duplicate stock " + name);
		ConfigurablePageStyle dps = new ConfigurablePageStyle();
		if (!curr.contains("pagestyle"))
			throw new ConfigException("stock must have a default pagestyle");
		for (String s : curr.get("pagestyle")) {
			if (!pages.containsKey(s))
				throw new ConfigException("there is no page style " + s);
			dps.applyAll(pages.get(s));
		}
		ConfigurablePageStyle fl = null, fr = null, left = new ConfigurablePageStyle().applyAll(dps), right = new ConfigurablePageStyle().applyAll(dps);
		if (curr.contains("left"))
			left.applyAll(pages.get(curr.get("left").get(0)));
		if (curr.contains("right"))
			right.applyAll(pages.get(curr.get("right").get(0)));
		if (curr.contains("firstLeft"))
			fl = new ConfigurablePageStyle().applyAll(left).applyAll(pages.get(curr.get("firstLeft").get(0)));
		if (curr.contains("firstRight"))
			fr = new ConfigurablePageStyle().applyAll(right).applyAll(pages.get(curr.get("firstRight").get(0)));
		stocks.put(name, new PaperStock(makeReam(curr), fl, fr, left, right));
	}
	
	private void set(Object overrides, String prop, List<String> values) throws ConfigException {
		Method m = figureSetMethod(overrides, prop);
		if (m == null)
			return;
		for (String s : values) {
			try {
				Class<?>[] args = m.getParameterTypes();
				if (args.length != 1)
					throw new NotImplementedException("#args != 1 but " + args.length + " for " + prop + " = " + s);
				switch (args[0].getSimpleName()) {
				case "boolean":
					m.invoke(overrides, Boolean.parseBoolean(s));
					break;
				case "float":
				case "Float":
					try {
						m.invoke(overrides, Float.parseFloat(s));
					} catch (Exception ex) {
						m.invoke(overrides, dim(s));
					}
					break;
				case "Justification":
					m.invoke(overrides, Justification.valueOf(s));
					break;
				case "PDFont":
					if (s.equals("courier"))
						m.invoke(overrides, PDType1Font.COURIER);
					else
						throw new ConfigException("Need to handle fonts");
					break;
				case "String":
					m.invoke(overrides, s);
					break;
				default:
					System.out.println("cannot handle proerty " + prop + " with type " + args[0].getSimpleName());
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				System.out.println("Cannot set style property " + prop + " with " + s);
			}
		}
	}

	private Method figureSetMethod(Object overrides, String prop) {
		List<Method> ms = Reflection.methods(overrides, prop);
		ms.addAll(Reflection.methods(overrides, "set" + StringUtil.capitalize(prop)));
		if (ms.isEmpty()) {
			System.out.println("there is no style property " + prop);
			return null;
		}
		if (ms.size() == 1)
			return ms.get(0);
		for (Method m : ms)
			if (m.getParameterTypes().length == 1)
				return m;
		System.out.println("there is no style property " + prop + " with one arg");
		return null;
	}

	@Override
	public Style get(String style) {
		if (!catalog.containsKey(style)) {
			if (!missed.contains(style)) {
				System.out.println("There is no style " + style);
				missed.add(style);
			}
			return defaultStyle;
		}
		return catalog.get(style);
	}

	@Override
	public Style getOptional(String style) {
		return catalog.get(style);
	}

	@Override
	public Stock getStock(String stockName) throws ConfigException {
		if (!stocks.containsKey(stockName))
			throw new ConfigException("there is no stock " + stockName + " defined");
		return stocks.get(stockName);
	}

	private float dim(String value) {
		if (value == null || value.length() < 3)
			throw new InvalidUsageException("value must have units");
		String units = value.substring(value.length()-2);
		float n = Float.parseFloat(value.substring(0, value.length()-2));
		switch (units) {
		case "pt":
			return n;
		case "in":
			return n*72;
		case "mm":
			return n*72/25.4f;
		case "cm":
			return n*72/2.54f;
		default:
			throw new InvalidUsageException("do not understand unit " + units + ": try pt, in, mm, cm");
		}
	}

	private Ream makeReam(ListMap<String, String> curr) {
		String ream = curr.get("ream").get(0);
		float w = dim(curr.get("width").get(0));
		float h = dim(curr.get("height").get(0));
		int b = 1;
		if (curr.contains("blksize"))
			b = Integer.parseInt(curr.get("blksize").get(0));
		return makeReam(ream, w, h, b);
	}

	private Ream makeReam(String ream, float width, float height, int blksize) {
		switch (ream) {
		case "single":
			return new SingleReam(width, height);
		case "double":
			return new DoubleReam(width, height);
		case "bifold":
			return new BifoldReam(blksize, width, height);
		default:
			throw new InvalidUsageException("there is no ream " + ream);
		}
	}

	
	public void loadFonts(PDDocument doc) throws IOException {
		for (Entry<String, String> e : fontStreams.entrySet()) {
			super.font(e.getKey(), (PDFont) PDType0Font.load(doc, this.getClass().getResourceAsStream(e.getValue())));
		}
	}

}
