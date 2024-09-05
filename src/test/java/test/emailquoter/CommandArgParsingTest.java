package test.emailquoter;

import static org.junit.Assert.*;

import org.junit.Test;

import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.utils.SBLineArgsParser;

public class CommandArgParsingTest {

	@Test
	public void aNumberIsEasyToParse() {
		ConfiguredState state = null;
		SBLineArgsParser<ConfiguredState> p = new SBLineArgsParser<>(state, "15");
		assertEquals("15", p.readArg());
		p.argsDone();
	}

	@Test
	public void aColonIsCountedAsTheSameArg() {
		ConfiguredState state = null;
		SBLineArgsParser<ConfiguredState> p = new SBLineArgsParser<>(state, "15:xy");
		assertEquals("15:xy", p.readArg());
		p.argsDone();
	}

	@Test
	public void aSpaceEndsAnArg() {
		ConfiguredState state = null;
		SBLineArgsParser<ConfiguredState> p = new SBLineArgsParser<>(state, "15 xy");
		assertEquals("15", p.readArg());
		assertEquals("xy", p.readArg());
		p.argsDone();
	}

	@Test
	public void aSpaceCanBeQuoted() {
		ConfiguredState state = null;
		SBLineArgsParser<ConfiguredState> p = new SBLineArgsParser<>(state, "'15 xy'");
		assertEquals("15 xy", p.readArg());
		p.argsDone();
	}

	@Test
	public void aSpaceCanBeDoubleQuoted() {
		ConfiguredState state = null;
		SBLineArgsParser<ConfiguredState> p = new SBLineArgsParser<>(state, "\"15 xy\"");
		assertEquals("15 xy", p.readArg());
		p.argsDone();
	}
}
