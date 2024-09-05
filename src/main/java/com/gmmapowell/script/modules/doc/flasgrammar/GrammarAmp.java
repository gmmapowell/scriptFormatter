package com.gmmapowell.script.modules.doc.flasgrammar;

import java.io.IOException;
import java.util.Map;

import org.flasck.flas.grammar.Grammar;
import org.flasck.flas.grammar.Production;
import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;
import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.utils.Command;
import com.gmmapowell.script.utils.LineArgsParser;

public class GrammarAmp implements AmpCommandHandler {
//	private final GlobalState global;
//	private final ConfiguredState state;
//	private final GrammarConfig ic;
//	private final Region samples;
//	private final List<AmpCommandHandler> removes = new ArrayList<>();

	private final boolean debug;
	private final ConfiguredState sink;
	private final Grammar grammar;
	private GrammarGenerator genGrammar;

	public GrammarAmp(ScannerAmpState state) {
		GlobalState global = state.global();
		this.debug = global.debug();
		this.sink = state.state();
		GrammarConfig ic = global.requireState(GrammarConfig.class);
		grammar = ic.grammar();
	}
	
	@Override
	public String name() {
		return "grammar";
	}

	@Override
	public boolean continuation(Command cont, LineArgsParser lap) {
		switch (cont.name()) {
		case "removeOption": 
		{
			return true;
		}
		default:
			return false;
		}
	}
	
	@Override
	public void prepare(AmpCommand cmd) {
		Map<String, String> params = cmd.args.readParams("rule");
		String ruleName = params.get("rule");
		if (ruleName == null) {
			genGrammar = new GrammarGenerator(grammar, sink);
		} else {
			if (debug)
				System.out.println("including grammar for production " + ruleName);
			try {
				Production rule = grammar.findRule(ruleName);
				genGrammar = new GrammarGenerator(rule, sink);
			} catch (RuntimeException ex) {
				System.out.println(sink.inputLocation() + ": " + ex.getMessage());
			}
		}
	}
	
	public GrammarGenerator grammar() {
		return genGrammar;
	}
	
	@Override
	public void invoke(AmpCommand cmd) {
		try {
			genGrammar.generate();
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}
}
