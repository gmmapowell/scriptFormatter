package com.gmmapowell.script.modules.doc.includecode;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;
import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.gmmapowell.script.utils.Command;
import com.gmmapowell.script.utils.LineArgsParser;

public class IncludeAmp implements AmpCommandHandler {
	private final GlobalState global;
	private final ConfiguredState state;
	private final IncluderConfig ic;
	private DoInclusion includer;

	public IncludeAmp(ScannerAmpState state) {
		global = state.global();
		this.state = state.state();
		ic = global.requireState(IncluderConfig.class);
	}
	
	@Override
	public String name() {
		return "include";
	}

	@Override
	public boolean continuation(Command cont, LineArgsParser lap) {
		switch (cont.name()) {
		case "remove": 
		case "select": 
		case "indents":
		case "stop": {
			return true;
		}
		default:
			return false;
		}
	}
	
	@Override
	public void prepare(AmpCommand cmd) {
		Region samples = ic.samples();
		if (samples == null)
			throw new CantHappenException("samples region has not been specified");
		Region r = samples;
		String file = cmd.args.readString();
		Map<String, String> params = cmd.args.readParams("formatter");
		System.out.println("want to include " + file + " with " + params);
		if (file.indexOf('/') != -1) {
			File f = new File(file);
			r = GeoFSUtils.regionPath(null, samples, f.getParent());
			file = f.getName();
		}
		if (!r.hasPlace(file)) {
			throw new RuntimeException("there is no sample " + file + " in " + samples + " at " + state.inputLocation());
		}
		Place code = r.place(file);
		Formatter formatter;
		if (!params.containsKey("formatter"))
			 formatter = new BoringFormatter(state);
		else {
			switch (params.get("formatter")) {
			case "html":
				formatter = new HTMLFormatter(state);
				break;
			case "flas":
				formatter = new FLASFormatter(state);
				break;
			default:
				formatter = new BoringFormatter(state);
				break;
			}
		}
		includer = new DoInclusion(state, code, formatter);
	}
	
	@Override
	public void invoke(AmpCommand cmd) {
		try {
			includer.include();
		} catch (IOException ex) {
			throw WrappedException.wrap(ex);
		}
	}

	public DoInclusion includer() {
		return includer;
	}
}
