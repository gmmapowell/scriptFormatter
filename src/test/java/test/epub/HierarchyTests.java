package test.epub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

	@Test
	public void nestItalicInAPara() {
		Hierarchy h = new Hierarchy(Arrays.asList());
		h.addText("An");
		h.addText(" ");
		h = h.push(Arrays.asList("italic"));
		h.addText("italic");
		h = h.extractParentWithSome(Arrays.asList());
		h.addText(" ");
		h.addText("para.");
	
		h.flush(body);
		String out = body.serialize(false);
		assertEquals("<body><p>An <i>italic</i> para.</p></body>", out);
	}

	@Test
	public void nestBoldAndItalicInAPara() {
		Hierarchy h = new Hierarchy(Arrays.asList());
		h.addText("A");
		h.addText(" ");
		h = h.push(Arrays.asList("bold", "italic"));
		h.addText("bold-italic");
		h = h.extractParentWithSome(Arrays.asList());
		h.addText(" ");
		h.addText("para.");
	
		h.flush(body);
		String out = body.serialize(false);
		assertEquals("<body><p>A <b><i>bold-italic</i></b> para.</p></body>", out);
	}

	@Test
	public void emptyHasExactlyEmpty() {
		Hierarchy h = new Hierarchy(Arrays.asList());
		assertTrue(h.hasExactly(Arrays.asList()));
	}

	@Test
	public void italicHasExactlyItalic() {
		Hierarchy h = new Hierarchy(Arrays.asList("italic"));
		assertTrue(h.hasExactly(Arrays.asList("italic")));
	}

	@Test
	public void bolditalicHasExactlyBoldItalic() {
		Hierarchy h = new Hierarchy(Arrays.asList("bold", "italic"));
		assertTrue(h.hasExactly(Arrays.asList("bold", "italic")));
	}

	@Test
	public void bolditalicHasExactlyItalicBold() {
		Hierarchy h = new Hierarchy(Arrays.asList("bold", "italic"));
		assertTrue(h.hasExactly(Arrays.asList("italic", "bold")));
	}

	@Test
	public void emptyDoesNotHaveExactlyItalic() {
		Hierarchy h = new Hierarchy(Arrays.asList());
		assertFalse(h.hasExactly(Arrays.asList("italic")));
	}

	@Test
	public void italicDoesNotHaveExactlyEmpty() {
		Hierarchy h = new Hierarchy(Arrays.asList("italic"));
		assertFalse(h.hasExactly(Arrays.asList()));
	}

	@Test
	public void italicDoesNotHaveExactlyBold() {
		Hierarchy h = new Hierarchy(Arrays.asList("italic"));
		assertFalse(h.hasExactly(Arrays.asList("bold")));
	}

	@Test
	public void italicDoesNotHaveExactlyBoldItalic() {
		Hierarchy h = new Hierarchy(Arrays.asList("italic"));
		assertFalse(h.hasExactly(Arrays.asList("bold", "italic")));
	}

	@Test
	public void emptyHasItselfForEmpty() {
		Hierarchy h = new Hierarchy(Arrays.asList());
		assertEquals(h, h.extractParentWithSome(Arrays.asList()));
	}

	@Test
	public void emptyItalicHasEmptyParentWhenItalicRemoved() {
		Hierarchy h = new Hierarchy(Arrays.asList());
		Hierarchy h1 = h.push(Arrays.asList("italic"));
		assertEquals(h, h1.extractParentWithSome(Arrays.asList()));
	}

	@Test
	public void boldHasItselfAsParentForBold() {
		Hierarchy h = new Hierarchy(Arrays.asList("bold"));
		assertEquals(h, h.extractParentWithSome(Arrays.asList("bold")));
	}

	@Test
	public void boldHasItselfAsParentForBoldItalic() {
		Hierarchy h = new Hierarchy(Arrays.asList("bold"));
		assertEquals(h, h.extractParentWithSome(Arrays.asList("bold", "italic")));
	}

	@Test
	public void boldThenItalicHasBoldParentWhenItalicRemoved() {
		Hierarchy h = new Hierarchy(Arrays.asList("bold"));
		Hierarchy h1 = h.push(Arrays.asList("bold", "italic"));
		assertEquals(h, h1.extractParentWithSome(Arrays.asList("bold")));
	}

	@Test
	public void emptyItalicHasEmptyParentWhenLookingForBold() {
		Hierarchy h = new Hierarchy(Arrays.asList());
		Hierarchy h1 = h.push(Arrays.asList("italic"));
		assertEquals(h, h1.extractParentWithSome(Arrays.asList("bold")));
	}

	@Test
	public void boldItalicMakesBoldParentWhenItalicRemoved() {
		Hierarchy h = new Hierarchy(Arrays.asList("bold", "italic"));
		Hierarchy h1 = h.extractParentWithSome(Arrays.asList("bold"));
		assertNotNull(h1);
		assertTrue(h1.hasExactly(Arrays.asList("bold")));
		assertTrue(h.hasExactly(Arrays.asList("italic")));
	}

	@Test
	public void boldItalicMakesBoldParentWhenItalicRemovedEvenWithEmptyRoot() {
		Hierarchy he = new Hierarchy(Arrays.asList());
		Hierarchy h = he.push(Arrays.asList("bold", "italic"));
		Hierarchy h1 = h.extractParentWithSome(Arrays.asList("bold"));
		assertNotNull(h1);
		assertTrue(h1.hasExactly(Arrays.asList("bold")));
		assertTrue(h.hasExactly(Arrays.asList("italic")));
	}

	@Test
	public void italicMakesEmptyParentWhenItalicRemoved() {
		Hierarchy h = new Hierarchy(Arrays.asList("italic"));
		Hierarchy h1 = h.extractParentWithSome(Arrays.asList("bold"));
		assertNotNull(h1);
		assertTrue(h1.hasExactly(Arrays.asList()));
	}
	
	// TODO: bolditalic with empty parent can give you a "bold" parent with this just being "italic" 
}
