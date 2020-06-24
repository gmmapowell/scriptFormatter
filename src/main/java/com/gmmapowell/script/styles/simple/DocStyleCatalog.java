package com.gmmapowell.script.styles.simple;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.gmmapowell.script.styles.FontCatalog;
import com.gmmapowell.script.styles.Justification;
import com.gmmapowell.script.styles.Style;

public class DocStyleCatalog extends FontCatalog {
	private final Map<String, Style> catalog = new TreeMap<>();
	private final Style defaultStyle;
	private final Set<String> missed = new TreeSet<>();

	public DocStyleCatalog() {
		defaultStyle = create()
			.setAfterBlock(0)
			.setBeforeBlock(0)
			.setJustification(Justification.LEFT)
			.setLeftMargin(0f)
			.setLineSpacing(14f)
			.setRightMargin(0f)
			.setFont("courier")
			.setItalic(false)
			.setUnderline(false)
			.build();
			
		catalog.put("text",
			new CompoundStyle(this,
				create().setFont("palatino").build(),
				defaultStyle
		));

		catalog.put("bullet",
				new CompoundStyle(this,
					create().setFont("palatino").build(),
					defaultStyle
			));

		catalog.put("italic",
				create().setItalic(true).build()
		);
	}
	
	public StyleBuilder create() {
		return new SimpleStyle(this);
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

}
