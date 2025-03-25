package test.cursor;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import com.gmmapowell.script.flow.Cursor;
import com.gmmapowell.script.flow.HorizSpan;
import com.gmmapowell.script.flow.Para;
import com.gmmapowell.script.flow.ParaBreak;
import com.gmmapowell.script.flow.Section;
import com.gmmapowell.script.flow.StyledToken;
import com.gmmapowell.script.flow.TextSpanItem;

public class CursorTest {

	@Test
	public void testNoTokensInAnEmptySection() {
		Section section = new Section("basic");
		Cursor c = new Cursor("main", section);
		StyledToken tok = c.next();
		System.out.println(tok);
		assertNull(tok);
	}

	@Test
	public void testNoTokensInASectionWithAnEmptyPara() {
		Section section = new Section("basic");
		section.paras.add(new Para(new ArrayList<>()));
		Cursor c = new Cursor("main", section);
		StyledToken tok = c.next();
		System.out.println(tok);
		assertNull(tok);
	}

	// NB: this is what the current code does, but it is not how I would design it today
	@Test
	public void testASectionWithAParaWithAnEmptySpanReturnsAParaBreak() {
		Section section = new Section("basic");
		Para para = new Para(new ArrayList<>());
		section.paras.add(para);
		HorizSpan span = new HorizSpan(null, new ArrayList<>());
		para.spans.add(span);
		Cursor c = new Cursor("main", section);
		StyledToken tok = c.next();
		System.out.println(tok);
		assertEquals(0, tok.styles.size());
		assertTrue(tok.it instanceof ParaBreak);
		tok = c.next();
		System.out.println(tok);
		assertNull(tok);
	}

	// NB: I could not find an instance of this code being used, but as it is at the moment, I chose to preserve it
	@Test
	public void testAParaWithBreakStyleAndNoSpansWithAFollowingParaWithSpansReturnsABreakToken() {
		Section section = new Section("basic");
		Para para = new Para(Arrays.asList("break"));
		section.paras.add(para);
		Para para2 = new Para(Arrays.asList());
		section.paras.add(para2);
		HorizSpan span = new HorizSpan(null, new ArrayList<>());
		para2.spans.add(span);
		HorizSpan span2 = new HorizSpan(null, new ArrayList<>());
		para2.spans.add(span2);
		Cursor c = new Cursor("main", section);
		StyledToken tok = c.next();
		System.out.println(tok);
		assertEquals(0, tok.styles.size());
		assertTrue(tok.it instanceof ParaBreak);
		tok = c.next();
		System.out.println(tok);
		assertNull(tok);
	}

	@Test
	public void testAParaWithATextTokenReturnsItAndAParaBreak() {
		Section section = new Section("basic");
		Para para = new Para(new ArrayList<>());
		section.paras.add(para);
		HorizSpan span = new HorizSpan(null, new ArrayList<>());
		para.spans.add(span);
		TextSpanItem tx = new TextSpanItem("hello");
		span.items.add(tx);
		Cursor c = new Cursor("main", section);
		StyledToken tok = c.next();
		System.out.println(tok);
		assertEquals(0, tok.styles.size());
		assertTrue(tok.it instanceof TextSpanItem);
		tok = c.next();
		System.out.println(tok);
		assertTrue(tok.it instanceof ParaBreak);
		tok = c.next();
		System.out.println(tok);
		assertNull(tok);
	}


	@Test
	public void testFormatsAreConcatenated() {
		Section section = new Section("basic");
		Para para = new Para(Arrays.asList("para"));
		section.paras.add(para);
		HorizSpan span = new HorizSpan(null, Arrays.asList("span"));
		para.spans.add(span);
		TextSpanItem tx = new TextSpanItem("hello");
		span.items.add(tx);
		Cursor c = new Cursor("main", section);
		StyledToken tok = c.next();
		System.out.println(tok);
		assertTrue(tok.it instanceof TextSpanItem);
		assertArrayEquals(new String[] {"para", "span"}, tok.styles.toArray());
		tok = c.next();
		System.out.println(tok);
		assertTrue(tok.it instanceof ParaBreak);
		tok = c.next();
		System.out.println(tok);
		assertNull(tok);
	}
}
