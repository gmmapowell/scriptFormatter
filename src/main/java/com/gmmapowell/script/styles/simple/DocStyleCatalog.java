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
			.beginNewPage(false)
			.showAtBottom(false)
			.setPreformatted(false)
			.setRequireAfter(0)
			.setAfterBlock(0)
			.setBeforeBlock(0)
			.setJustification(Justification.LEFT)
			.setLeftMargin(0f)
			.setLineSpacing(14f)
			.setRightMargin(0f)
			.setFont("courier")
			.setFontSize(12.0f)
			.setBaselineAdjust(0f)
			.setBold(false)
			.setItalic(false)
			.setUnderline(false)
			.build();
			
		catalog.put("chapter-title",
				new CompoundStyle(this,
						// want set new page
					create().setFont("palatino").beginNewPage(true).setFontSize(18f).setLineSpacing(22.0f).setBold(true).setBeforeBlock(0).setAfterBlock(12).build(),
					defaultStyle
			));

		catalog.put("section-title",
				new CompoundStyle(this,
					create().setFont("palatino").setFontSize(16f).setLineSpacing(20.0f).setBold(true).setBeforeBlock(18).setAfterBlock(12).setRequireAfter(108f).build(),
					defaultStyle
			));

		catalog.put("subsection-title",
				new CompoundStyle(this,
					create().setFont("palatino").setFontSize(15f).setLineSpacing(19.0f).setBold(true).setBeforeBlock(15).setAfterBlock(12).setRequireAfter(108f).build(),
					defaultStyle
			));

		catalog.put("text",
			new CompoundStyle(this,
				create().setFont("palatino").setBeforeBlock(6).setAfterBlock(6).build(),
				defaultStyle
		));

		catalog.put("refComment",
				new CompoundStyle(this,
					create().setFont("palatino").setFontSize(10f).setLeftMargin(36).setRightMargin(24).setBeforeBlock(6).setAfterBlock(6).build(),
					defaultStyle
			));

		catalog.put("beginRefComment",
				new CompoundStyle(this,
					create().setFont("palatino").setFontSize(10f).setFirstMargin(18f).setLeftMargin(36).setRightMargin(24).setBeforeBlock(12).setAfterBlock(6).build(),
					defaultStyle
			));

		catalog.put("endRefComment",
				new CompoundStyle(this,
					create().setFont("palatino").setFirstMargin(18f).setLeftMargin(36).setRightMargin(24).setAfterBlock(12).setAfterBlock(6).build(),
					defaultStyle
			));

		catalog.put("preformatted",
				new CompoundStyle(this,
					create().setFont("monospace").setFontSize(11f).setLineSpacing(12.5f).setPreformatted(true).build(),
					defaultStyle
			));

		catalog.put("grammar",
				new CompoundStyle(this,
					create().setFont("monospace").setFontSize(9f).setLineSpacing(12.5f).setPreformatted(true).setLeftMargin(192f).setFirstMargin(0f).build(),
					defaultStyle
			));

		catalog.put("grammar-number",
				create().setWidth(32.0f).setJustification(Justification.CENTER).build()
		);

		catalog.put("grammar-name",
				create().setBold(true).setWidth(96.0f).setJustification(Justification.RIGHT).build()
		);

		catalog.put("grammar-blank",
				create().setBold(true).setWidth(128.0f).setJustification(Justification.RIGHT).build()
		);
		
		catalog.put("grammar-op",
				create().setBold(true).setWidth(32.0f).setJustification(Justification.CENTER).build()
		);

		catalog.put("bullet",
				new CompoundStyle(this,
					create().setFont("palatino").setBeforeBlock(2).setAfterBlock(2).setFirstMargin(18f).setLeftMargin(32f).build(),
					defaultStyle
			));

		catalog.put("bullet2",
				new CompoundStyle(this,
					create().setFont("palatino").setBeforeBlock(2).setAfterBlock(2).setFirstMargin(36f).setLeftMargin(50f).build(),
					defaultStyle
			));

		catalog.put("footnote",
				new CompoundStyle(this,
					create().setFont("palatino").setFontSize(9.5f).setLineSpacing(11f).setBeforeBlock(2f).setFirstMargin(0).setLeftMargin(6).showAtBottom(true).build(),
					defaultStyle
			));

		catalog.put("bullet-sign",
				create().setWidth(14.0f).build()
		);

		catalog.put("comment-sign",
				create().setFont("monospace").setWidth(18.0f).build()
		);

		catalog.put("tt",
				create().setFont("courier").setAdjustFontSize(-1f).build()
		);

		catalog.put("link",
				create().setFont("courier").setAdjustFontSize(-1f).build()
		);

		catalog.put("endlink",
				create().setFont("courier").setAdjustFontSize(-1f).build()
		);

		catalog.put("footnote-number",
				create().setFontSize(8.0f).setBaselineAdjust(5.0f).build()
		);

		catalog.put("italic",
				create().setItalic(true).build()
		);

		catalog.put("bold",
				create().setBold(true).build()
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

	@Override
	public Style getOptional(String style) {
		return catalog.get(style);
	}
}
