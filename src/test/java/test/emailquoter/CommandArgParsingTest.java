package test.emailquoter;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gmmapowell.script.processor.prose.DocState;
import com.gmmapowell.script.processor.prose.SBLineArgsParser;

public class CommandArgParsingTest {

	@Test
	public void aNumberIsEasyToParse() {
		DocState state = null;
		SBLineArgsParser<DocState> p = new SBLineArgsParser<>(state, "15");
		assertEquals("15", p.readArg());
		p.argsDone();
	}

	@Test
	public void aColonIsCountedAsTheSameArg() {
		DocState state = null;
		SBLineArgsParser<DocState> p = new SBLineArgsParser<>(state, "15:xy");
		assertEquals("15:xy", p.readArg());
		p.argsDone();
	}

	@Test
	public void aSpaceEndsAnArg() {
		DocState state = null;
		SBLineArgsParser<DocState> p = new SBLineArgsParser<>(state, "15 xy");
		assertEquals("15", p.readArg());
		assertEquals("xy", p.readArg());
		p.argsDone();
	}

	@Test
	public void aSpaceCanBeQuoted() {
		DocState state = null;
		SBLineArgsParser<DocState> p = new SBLineArgsParser<>(state, "'15 xy'");
		assertEquals("15 xy", p.readArg());
		p.argsDone();
	}

	@Test
	public void aSpaceCanBeDoubleQuoted() {
		DocState state = null;
		SBLineArgsParser<DocState> p = new SBLineArgsParser<>(state, "\"15 xy\"");
		assertEquals("15 xy", p.readArg());
		p.argsDone();
	}
}
