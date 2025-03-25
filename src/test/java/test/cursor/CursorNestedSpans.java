package test.cursor;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import com.gmmapowell.script.flow.Cursor;
import com.gmmapowell.script.flow.HorizSpan;
import com.gmmapowell.script.flow.NestedSpan;
import com.gmmapowell.script.flow.Para;
import com.gmmapowell.script.flow.ParaBreak;
import com.gmmapowell.script.flow.Section;
import com.gmmapowell.script.flow.StyledToken;
import com.gmmapowell.script.flow.TextSpanItem;

public class CursorNestedSpans {

	// NB: this is what the current code does, but it is not how I would design it today
	@Test
	public void testASingleNestedSpanWithNoContentStillReturnsJustAParaBreak() {
		Section section = new Section("basic");
		Para para = new Para(new ArrayList<>());
		section.paras.add(para);
		HorizSpan span = new HorizSpan(null, new ArrayList<>());
		para.spans.add(span);
		HorizSpan hs = new HorizSpan(null, new ArrayList<>());
		NestedSpan ns = new NestedSpan(hs);
		span.items.add(ns);
		Cursor c = new Cursor("main", section);
		StyledToken tok = c.next();
		assertEquals(0, tok.styles.size());
		assertTrue(tok.it instanceof ParaBreak);
		tok = c.next();
		assertNull(tok);
	}

	@Test
	public void testASingleNestedSpanIncludesAllTheStyles() {
		Section section = new Section("basic");
		Para para = new Para(Arrays.asList("para"));
		section.paras.add(para);
		HorizSpan span = new HorizSpan(null, Arrays.asList("span"));
		para.spans.add(span);
		HorizSpan hs = new HorizSpan(null, Arrays.asList("nested"));
		NestedSpan ns = new NestedSpan(hs);
		span.items.add(ns);
		TextSpanItem tx = new TextSpanItem("hello");
		hs.items.add(tx);
		Cursor c = new Cursor("main", section);
		StyledToken tok = c.next();
		assertTrue(tok.it instanceof TextSpanItem);
		assertArrayEquals(new String[] {"para", "span", "nested"}, tok.styles.toArray());
		tok = c.next();
		assertTrue(tok.it instanceof ParaBreak);
		tok = c.next();
		assertNull(tok);
	}

	@Test
	public void testTwoConsecutiveNestedSpansIncludeAllTheirRespectiveStyles() {
		Section section = new Section("basic");
		Para para = new Para(Arrays.asList("para"));
		section.paras.add(para);
		HorizSpan span = new HorizSpan(null, Arrays.asList("span"));
		para.spans.add(span);
		{
			HorizSpan hs = new HorizSpan(null, Arrays.asList("nested"));
			NestedSpan ns = new NestedSpan(hs);
			span.items.add(ns);
			TextSpanItem tx = new TextSpanItem("hello");
			hs.items.add(tx);
		}
		{
			TextSpanItem tx = new TextSpanItem("there");
			span.items.add(tx);
		}
		{
			HorizSpan hs = new HorizSpan(null, Arrays.asList("second"));
			NestedSpan ns = new NestedSpan(hs);
			span.items.add(ns);
			TextSpanItem tx = new TextSpanItem("world");
			hs.items.add(tx);
		}
		Cursor c = new Cursor("main", section);
		StyledToken tok = c.next();
		assertTrue(tok.it instanceof TextSpanItem);
		assertEquals("hello", ((TextSpanItem)tok.it).text);
		assertArrayEquals(new String[] {"para", "span", "nested"}, tok.styles.toArray());
		tok = c.next();
		assertTrue(tok.it instanceof TextSpanItem);
		assertEquals("there", ((TextSpanItem)tok.it).text);
		assertArrayEquals(new String[] {"para", "span"}, tok.styles.toArray());
		tok = c.next();
		assertTrue(tok.it instanceof TextSpanItem);
		assertEquals("world", ((TextSpanItem)tok.it).text);
		assertArrayEquals(new String[] {"para", "span", "second"}, tok.styles.toArray());
		tok = c.next();
		assertTrue(tok.it instanceof ParaBreak);
		tok = c.next();
		assertNull(tok);
	}


	@Test
	public void testTwoConsecutiveNestedSpansInAnAlreadyNestedSpanIncludeAllTheirRespectiveStyles() {
		Section section = new Section("basic");
		Para para = new Para(Arrays.asList("para"));
		section.paras.add(para);
		HorizSpan top = new HorizSpan(null, Arrays.asList("top"));
		para.spans.add(top);
		{
			HorizSpan span = new HorizSpan(null, Arrays.asList("span"));
			NestedSpan wrapper = new NestedSpan(span);
			top.items.add(wrapper);
			{
				HorizSpan hs = new HorizSpan(null, Arrays.asList("nested"));
				NestedSpan ns = new NestedSpan(hs);
				span.items.add(ns);
				TextSpanItem tx = new TextSpanItem("hello");
				hs.items.add(tx);
			}
			{
				TextSpanItem tx = new TextSpanItem("there");
				span.items.add(tx);
			}
			{
				HorizSpan hs = new HorizSpan(null, Arrays.asList("second"));
				NestedSpan ns = new NestedSpan(hs);
				span.items.add(ns);
				TextSpanItem tx = new TextSpanItem("world");
				hs.items.add(tx);
			}
		}
		Cursor c = new Cursor("main", section);
		StyledToken tok = c.next();
		assertTrue(tok.it instanceof TextSpanItem);
		assertEquals("hello", ((TextSpanItem)tok.it).text);
		assertArrayEquals(new String[] {"para", "top", "span", "nested"}, tok.styles.toArray());
		tok = c.next();
		assertTrue(tok.it instanceof TextSpanItem);
		assertEquals("there", ((TextSpanItem)tok.it).text);
		assertArrayEquals(new String[] {"para", "top", "span"}, tok.styles.toArray());
		tok = c.next();
		System.out.println("have " + tok.it);
		assertTrue(tok.it instanceof TextSpanItem);
		assertEquals("world", ((TextSpanItem)tok.it).text);
		assertArrayEquals(new String[] {"para", "top", "span", "second"}, tok.styles.toArray());
		tok = c.next();
		assertTrue(tok.it instanceof ParaBreak);
		tok = c.next();
		assertNull(tok);
	}
}
