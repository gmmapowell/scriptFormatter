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
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.zinutils.collections.ListMap;
import org.zinutils.exceptions.InvalidUsageException;
import org.zinutils.exceptions.NotImplementedException;
import org.zinutils.reflection.Reflection;
import org.zinutils.utils.StringUtil;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.styles.simple.CompoundStyle;
import com.gmmapowell.script.styles.simple.SimpleStyle;

public class ConfigurableStyleCatalog extends FontCatalog implements StyleCatalog {
	private final Map<String, Style> catalog = new TreeMap<>();
	private final Style defaultStyle;
	private final Set<String> missed = new TreeSet<>();

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
		else if (type.equals("font"))
			installFont(debug, type, name, curr);
		else
			throw new NotImplementedException("cannot handle " + type);
	}

	private void buildStyle(boolean debug, String type, String name, ListMap<String, String> curr) {
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

	private void installFont(boolean debug, String type, String name, ListMap<String, String> curr) throws ConfigException {
		if (curr.contains("stream")) {
			String stream = curr.get("stream").get(0);
//			catalog.font(name, (PDFont) PDType0Font.load(doc, this.getClass().getResourceAsStream("/fonts/MonospaceRegular.ttf")));
		} else
			throw new ConfigException("no stream for font " + name);
	}

	private void set(SimpleStyle overrides, String prop, List<String> values) {
		Method m = figureSetMethod(overrides, prop);
		if (m == null)
			return;
		for (String s : values) {
			try {
				Class<?>[] args = m.getParameterTypes();
				if (args.length != 1)
					throw new NotImplementedException("#args != 1 but " + args.length);
				switch (args[0].getSimpleName()) {
				case "boolean":
					m.invoke(overrides, Boolean.parseBoolean(s));
					break;
				case "float":
				case "Float":
					m.invoke(overrides, Float.parseFloat(s));
					break;
				case "Justification":
					m.invoke(overrides, Justification.valueOf(s));
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

	private Method figureSetMethod(SimpleStyle overrides, String prop) {
		List<Method> ms= Reflection.methods(overrides, prop);
		if (ms.isEmpty())
			ms = Reflection.methods(overrides, "set" + StringUtil.capitalize(prop));
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
}
