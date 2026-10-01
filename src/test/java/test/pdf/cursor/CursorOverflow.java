package test.pdf.cursor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.doubled.RegionDouble;
import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.HorizSpan;
import com.gmmapowell.script.flow.Para;
import com.gmmapowell.script.flow.Section;
import com.gmmapowell.script.flow.TextSpanItem;
import com.gmmapowell.script.sink.pdf.Acceptability;
import com.gmmapowell.script.sink.pdf.PDFSink;
import com.gmmapowell.script.sink.pdf.PageCompositor;
import com.gmmapowell.script.sink.pdf.Stock;
import com.gmmapowell.script.styles.simple.SimpleStyle;

import test.pdf.outlets.TestStyleCatalog;

public class CursorOverflow {
	public @Rule JUnitRuleMockery mockery = new JUnitRuleMockery();

	@SuppressWarnings("unchecked")
	@Test
	public void testWeRevertAfterNoRoom() throws IOException, ConfigException {
		Stock stock = mockery.mock(Stock.class);
		TestStyleCatalog styles = new TestStyleCatalog(stock);
		SimpleStyle text = new SimpleStyle(styles);
		text.setFont("courier");
		text.setLineSpacing(16);
		text.setUnderline(false);
		styles.styles.put("text", text);

		TextSpanItem tsi1 = new TextSpanItem("hello, world");
		TextSpanItem tsi2 = new TextSpanItem("hello, new page");
		TextSpanItem tsi3 = new TextSpanItem("goodbye, cruel world");

		PageCompositor pc = mockery.mock(PageCompositor.class, "page1");
		PageCompositor pc2 = mockery.mock(PageCompositor.class, "page2");

		AcceptMe apb = new AcceptMe(Acceptability.PROCESSED);
		mockery.checking(new Expectations() {{
			oneOf(stock).newDocument(styles);
			oneOf(stock).getPage(with(any(Map.class)), with(true)); will(returnValue(pc));
			oneOf(pc).begin();
			oneOf(pc).token(with(StyledTokenMatcher.token(tsi1))); will(new AcceptMe(Acceptability.PENDING));
			oneOf(pc).token(with(StyledTokenMatcher.parabreak)); will(apb);
			oneOf(pc).token(with(StyledTokenMatcher.token(tsi2))); will(new AcceptMe(Acceptability.PENDING));
			oneOf(pc).token(with(StyledTokenMatcher.token(tsi3))); will(new AcceptMe(Acceptability.PENDING));
			oneOf(pc).token(with(StyledTokenMatcher.parabreak)); will(new AcceptMe(Acceptability.NOROOM, apb));
			oneOf(pc).nextRegions(); will(returnValue(false));
			oneOf(stock).getPage(with(any(Map.class)), with(false)); will(returnValue(pc2));
			oneOf(pc2).begin();
			oneOf(pc2).token(with(StyledTokenMatcher.parabreak)); will(new AcceptMe(Acceptability.PENDING));
			oneOf(pc2).token(with(StyledTokenMatcher.token(tsi2))); will(new AcceptMe(Acceptability.PENDING));
			oneOf(pc2).token(with(StyledTokenMatcher.token(tsi3))); will(new AcceptMe(Acceptability.PENDING));
			oneOf(pc2).token(with(StyledTokenMatcher.parabreak)); will(new AcceptMe(Acceptability.PROCESSED));
			
			oneOf(stock).close(with(any(Place.class)));
		}});
		Region r = new RegionDouble();
		
		VarMap vars = new VarMap();
		vars.put(1, "stock", "letter");
		PDFSink sink = new PDFSink(r, styles, "outfile", null, false, null, false, null, vars);
		Flow main = new Flow("main", true);
		Section main1 = new Section(null);
		
		Para mainPara1 = new Para(Arrays.asList("text"));
		HorizSpan mainPara1hz1 = new HorizSpan(null, null);
		mainPara1hz1.items.add(tsi1);
		mainPara1.spans.add(mainPara1hz1);
		main1.paras.add(mainPara1);

		Para mainPara2 = new Para(Arrays.asList("text"));
		HorizSpan mainPara2hz1 = new HorizSpan(null, null);
		mainPara2hz1.items.add(tsi2);
		mainPara2hz1.items.add(tsi3);
		mainPara2.spans.add(mainPara2hz1);
		main1.paras.add(mainPara2);

		main.sections.add(main1);
		sink.flow(main);
		sink.render();
	}
}
