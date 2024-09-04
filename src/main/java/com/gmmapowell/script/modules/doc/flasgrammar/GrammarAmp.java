package com.gmmapowell.script.modules.doc.flasgrammar;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;
import com.gmmapowell.script.utils.Command;
import com.gmmapowell.script.utils.LineArgsParser;

public class GrammarAmp implements AmpCommandHandler {
//	private final GlobalState global;
//	private final ConfiguredState state;
//	private final GrammarConfig ic;
//	private final Region samples;
//	private final List<AmpCommandHandler> removes = new ArrayList<>();

	public GrammarAmp(ScannerAmpState state) {
//		global = state.global();
//		this.state = state.state();
//		ic = global.requireState(IncluderConfig.class);
//		samples = ic.samples();
	}
	
	@Override
	public String name() {
		return "grammar";
	}

	@Override
	public boolean continuation(Command cont, LineArgsParser lap) {
		switch (cont.name()) {
		case "removeOption": 
//		case "select": 
//		case "indents":
//		case "stop": 
		{
			return true;
		}
		default:
			return false;
		}
	}
	
	@Override
	public void prepare(AmpCommand cmd) {
	}
	
	@Override
	public void invoke(AmpCommand cmd) {
		System.out.println("need to implement &grammar");
	}
}
