package test.emailquoter;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gmmapowell.script.processor.prose.DocState;
import com.gmmapowell.script.processor.prose.SBLineArgsParser;

public class CommandArgParsingTest {

	@Test
	public void test() {
		DocState state = null;
		SBLineArgsParser<DocState> p = new SBLineArgsParser<>(state, "15");
		assertEquals("15", p.readArg());
		p.argsDone();
	}

}
