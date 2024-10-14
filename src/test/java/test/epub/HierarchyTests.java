package test.epub;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.zinutils.xml.XML;
import org.zinutils.xml.XMLElement;

import com.gmmapowell.script.sink.epub.Hierarchy;

public class HierarchyTests {
	XML xml = XML.createNS("1.0", "html", "http://www.w3.org/1999/xhtml");
	XMLElement body = xml.top().addElement("body");
	
	@Test
	public void simpleTextInChapterTitle() {
		Hierarchy h = new Hierarchy(Arrays.asList("chapter-title"));
		h.addText("The");
		h.addText(" ");
		h.addText("Title");
	
		h.flush(body);
		String out = body.serialize(false);
		assertEquals("<body><h1>The Title</h1></body>", out);
	}

	@Test
	public void simpleTextInPara() {
		Hierarchy h = new Hierarchy(Arrays.asList());
		h.addText("A");
		h.addText(" ");
		h.addText("simple");
		h.addText(" ");
		h.addText("para.");
	
		h.flush(body);
		String out = body.serialize(false);
		assertEquals("<body><p>A simple para.</p></body>", out);
	}

	@Test
	public void italicTextWholePara() {
		Hierarchy h = new Hierarchy(Arrays.asList("italic"));
		h.addText("An");
		h.addText(" ");
		h.addText("italic");
		h.addText(" ");
		h.addText("para.");
	
		h.flush(body);
		String out = body.serialize(false);
		assertEquals("<body><p><i>An italic para.</i></p></body>", out);
	}

	@Test
	public void boldTextWholePara() {
		Hierarchy h = new Hierarchy(Arrays.asList("bold"));
		h.addText("A");
		h.addText(" ");
		h.addText("bold");
		h.addText(" ");
		h.addText("para.");
	
		h.flush(body);
		String out = body.serialize(false);
		assertEquals("<body><p><b>A bold para.</b></p></body>", out);
	}
}
