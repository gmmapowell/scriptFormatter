package com.gmmapowell.script.modules.processors.doc;

import java.util.ArrayList;
import java.util.List;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ModuleConfigListener;
import com.gmmapowell.script.config.reader.NestedModuleCreator;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.elements.block.BlockishElementFactory;
import com.gmmapowell.script.processor.NewParaProcessor;
import com.gmmapowell.script.processor.configured.ConfiguredProcessor;
import com.gmmapowell.script.processor.configured.StandardLineProcessor;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.utils.Command;

public class DocProcessorConfigListener implements ConfigListener {
	private ReadConfigState state;
	private VarMap vars = new VarMap();
	private List<ModuleConfigListener> modules = new ArrayList<>();

	public DocProcessorConfigListener(ReadConfigState state) {
		this.state = state;
		
		// TODO: this should come from a module configuration somewhere
		this.state.config.bindExtensionPoint(AtCommandHandler.class, ChapterCommand.class);
	}
	
	@Override
	public ConfigListener dispatch(Command cmd) {
		switch (cmd.name()) {
		case "module": {
			ModuleConfigListener nmc = new NestedModuleCreator(state).module(cmd.line().readArg());
			modules.add(nmc);
			return nmc;
		}
		case "joinspace": 
		case "meta":
		case "scanmode":
		{
			vars.put(cmd.depth(), cmd.name(), cmd.line().readArg());
			return null;
		}
		default: {
			throw new NotImplementedException("doc processor does not have parameter " + cmd.name());
		}
		}
	}

	@Override
	public void complete() throws ConfigException {
		try {
			Sink sink = state.config.makeSink();
			ConfiguredProcessor proc = new ConfiguredProcessor(state.config, state.root, new BlockishElementFactory(), sink, vars, state.debug);
			proc.setDefaultHandler(StandardLineProcessor.class);
			proc.setBlankHandler(NewParaProcessor.class);
			proc.addScanner(AtSpotter.class);
			proc.addScanner(FieldSpotter.class);
			state.config.processor(proc);
			// TODO: this needs to come back in some form
//			for (ModuleConfigListener m : modules) {
//				m.activate(proc);
//			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ConfigException("Error creating DocProcessor: " + ex.getMessage());
		}
	}
}
