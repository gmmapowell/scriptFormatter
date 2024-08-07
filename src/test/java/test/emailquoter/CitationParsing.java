package test.emailquoter;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gmmapowell.script.modules.emailquoter.Citation;

public class CitationParsing {

	@Test
	public void test() {
		Citation c = Citation.parse("file", "1");
		assertEquals("file", c.getFile());
		assertEquals(1, c.getFirst());
		assertEquals(1, c.getLast());
	}

}
