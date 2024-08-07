package test.emailquoter;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gmmapowell.script.modules.emailquoter.Citation;

public class CitationParsing {

	@Test
	public void aSingleNumberSetsFirstAndLast() {
		Citation c = Citation.parse("file", "1");
		assertEquals("file", c.getFile());
		assertEquals(1, c.getFirst());
		assertNull(c.getFromPhrase());
		assertEquals(1, c.getLast());
		assertNull(c.getToPhrase());
	}

	@Test
	public void twoNumbersSeparatedByADashSetFirstAndLast() {
		Citation c = Citation.parse("file", "1-3");
		assertEquals("file", c.getFile());
		assertEquals(1, c.getFirst());
		assertNull(c.getFromPhrase());
		assertEquals(3, c.getLast());
		assertNull(c.getToPhrase());
	}

	@Test
	public void aColonIntroducesAKeyPhrase() {
		Citation c = Citation.parse("file", "1:Quickly, there-3");
		assertEquals("file", c.getFile());
		assertEquals(1, c.getFirst());
		assertEquals("Quickly, there", c.getFromPhrase());
		assertEquals(3, c.getLast());
		assertNull(c.getToPhrase());
	}

	@Test
	public void aColonIntroducesAToPhraseAsWell() {
		Citation c = Citation.parse("file", "1:Quickly, there-3:hello.");
		assertEquals("file", c.getFile());
		assertEquals(1, c.getFirst());
		assertEquals("Quickly, there", c.getFromPhrase());
		assertEquals(3, c.getLast());
		assertEquals("hello.", c.getToPhrase());
	}
}
