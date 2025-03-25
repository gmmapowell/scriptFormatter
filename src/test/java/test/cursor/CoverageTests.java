package test.cursor;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gmmapowell.script.flow.Cursor;
import com.gmmapowell.script.flow.Section;

public class CoverageTests {

	@Test
	public void testFlowName() {
		Section section = new Section("basic");
		Cursor c = new Cursor("main", section);
		assertEquals("main", c.flowName());
	}

	@Test
	public void testFormat() {
		Section section = new Section("basic");
		Cursor c = new Cursor("main", section);
		assertEquals("basic", c.format());
	}
}
