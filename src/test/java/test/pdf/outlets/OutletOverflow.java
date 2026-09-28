package test.pdf.outlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Arrays;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.Test;

import com.gmmapowell.script.flow.ParaBreak;
import com.gmmapowell.script.flow.StyledToken;
import com.gmmapowell.script.flow.TextSpanItem;
import com.gmmapowell.script.sink.pdf.Acceptability;
import com.gmmapowell.script.sink.pdf.Acceptance;
import com.gmmapowell.script.sink.pdf.Outlet;
import com.gmmapowell.script.styles.simple.SimpleStyle;

public class OutletOverflow {
	public static final float wid = 444;
	public static final float ht = 644;
	
	@Test
	public void testBoxIsRejectedOnOverflow() throws IOException {
		PDDocument doc = new PDDocument();
		PDRectangle size = new PDRectangle(wid, ht);

		PDPage meta = new PDPage(size);
		PDPageContentStream stream = new PDPageContentStream(doc, meta);

		TestStyleCatalog styles = new TestStyleCatalog();
		SimpleStyle bigblock = new SimpleStyle(styles);
		bigblock.setFont("courier");
		bigblock.setLineSpacing(490);
		bigblock.setUnderline(false);
		styles.styles.put("bigblock", bigblock);

		SimpleStyle text = new SimpleStyle(styles);
		text.setFont("courier");
		text.setLineSpacing(16);
		text.setUnderline(false);
		styles.styles.put("text", text);
		
		Outlet o1 = new Outlet(styles, null, null, stream, new PDRectangle(72, 72, wid-144, ht-144));
		System.out.println(o1);

		Acceptance acc = o1.place(new StyledToken("main", null, Arrays.asList("bigblock"), new TextSpanItem("hello, world")));
		assertNotNull(acc);
		assertEquals(Acceptability.PENDING, acc.status);
		assertNull(acc.lastAccepted);

		StyledToken brk = new StyledToken("main", null, Arrays.asList("text"), new ParaBreak());
		acc = o1.place(brk);
		assertNotNull(acc);
		assertEquals(Acceptability.PROCESSED, acc.status);
		assertEquals(brk, acc.lastAccepted);

		acc = o1.place(new StyledToken("main", null, Arrays.asList("text"), new TextSpanItem("goodbye, world")));
		assertNotNull(acc);
		assertEquals(Acceptability.PENDING, acc.status);
		assertEquals(brk, acc.lastAccepted);

		acc = o1.place(new StyledToken("main", null, Arrays.asList("text"), new ParaBreak()));
		assertNotNull(acc);
		assertEquals(Acceptability.NOROOM, acc.status);
		assertEquals(brk, acc.lastAccepted);
	}
}
