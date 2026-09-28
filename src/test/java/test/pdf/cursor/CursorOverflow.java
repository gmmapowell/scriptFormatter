package test.pdf.cursor;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.doubled.RegionDouble;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.HorizSpan;
import com.gmmapowell.script.flow.Para;
import com.gmmapowell.script.flow.ParaBreak;
import com.gmmapowell.script.flow.Section;
import com.gmmapowell.script.flow.TextSpanItem;
import com.gmmapowell.script.sink.pdf.PDFSink;
import com.gmmapowell.script.styles.simple.SimpleStyle;

import test.pdf.outlets.TestStyleCatalog;

public class CursorOverflow {

	@Test
	public void testWeRevertAfterNoRoom() throws IOException, ConfigException {
		Region r = new RegionDouble();
		TestStyleCatalog styles = new TestStyleCatalog();
		SimpleStyle text = new SimpleStyle(styles);
		text.setFont("courier");
		text.setLineSpacing(16);
		text.setUnderline(false);
		styles.styles.put("text", text);
		
		VarMap vars = new VarMap();
		vars.put(1, "stock", "letter");
		PDFSink sink = new PDFSink(r, styles, "outfile", null, false, null, false, null, vars);
		Flow main = new Flow("main", true);
		Section main1 = new Section(null);
		Para mainPara1 = new Para(Arrays.asList("text"));
		HorizSpan mainPara1hz1 = new HorizSpan(null, null);
		mainPara1hz1.items.add(new TextSpanItem("hello, world"));
		mainPara1hz1.items.add(new ParaBreak());
		mainPara1.spans.add(mainPara1hz1);
		main1.paras.add(mainPara1);
		main.sections.add(main1);
		sink.flow(main);
		sink.render();
	}

}
