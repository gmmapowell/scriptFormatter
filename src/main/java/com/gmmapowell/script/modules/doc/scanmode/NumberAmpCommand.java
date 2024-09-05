package com.gmmapowell.script.modules.doc.scanmode;

import org.zinutils.exceptions.InvalidUsageException;

import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class NumberAmpCommand implements AmpCommandHandler {
	private final ConfiguredState sink;
	private final ScanmodeState state;
//	private final IncluderConfig ic;
//	private final Region samples;
//	private DoInclusion includer;

	public NumberAmpCommand(ScannerAmpState sas) {
		this.sink = sas.state();
		this.state = sas.global().requireState(ScanmodeState.class);
	}
	
	@Override
	public String name() {
		return "number";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		if (!state.activeNumbering())
			throw new InvalidUsageException("cannot use &number outside @Numbering...@/");
		sink.newPara(state.numberPara());
		sink.newSpan("bullet-sign");
		sink.text(state.currentNumber());
		sink.endSpan();
		sink.processText(cmd.args.asString().trim());
		sink.endPara();
	}
}
