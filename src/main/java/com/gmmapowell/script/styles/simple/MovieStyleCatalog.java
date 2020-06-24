package com.gmmapowell.script.styles.simple;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.gmmapowell.script.styles.FontCatalog;
import com.gmmapowell.script.styles.Justification;
import com.gmmapowell.script.styles.Style;

public class MovieStyleCatalog extends FontCatalog {
	private final Map<String, Style> catalog = new TreeMap<>();
	private final Style defaultStyle;
	private final Set<String> missed = new TreeSet<>();

	public MovieStyleCatalog() {
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
			
		catalog.put("title",
			new CompoundStyle(this,
				create().setAfterBlock(32f).setJustification(Justification.CENTER).setUnderline(true).build(),
				defaultStyle
		));

		catalog.put("slug",
			new CompoundStyle(this,
				create().setAfterBlock(14f).setBeforeBlock(14f).setUnderline(true).build(),
				defaultStyle
		));

		catalog.put("scene",
			new CompoundStyle(this,
				create().setAfterBlock(14f).setBeforeBlock(14f).build(),
				defaultStyle
		));

		catalog.put("speaker",
			new CompoundStyle(this,
				create().setBeforeBlock(14f).setJustification(Justification.CENTER).build(),
				defaultStyle
		));

		catalog.put("direction",
				new CompoundStyle(this,
					create().setFirstMargin(100f).setLeftMargin(108f).setRightMargin(108f).build(),
					defaultStyle
			));

		catalog.put("speech",
			new CompoundStyle(this,
				create().setLeftMargin(72f).setRightMargin(72f).build(),
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
