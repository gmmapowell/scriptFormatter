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
import com.gmmapowell.script.flow.FlowMap;
import com.gmmapowell.script.processor.NewParaProcessor;
import com.gmmapowell.script.processor.configured.ConfiguredProcessor;
import com.gmmapowell.script.processor.configured.StandardLineProcessor;
import com.gmmapowell.script.utils.Command;

public class DocProcessorConfigListener implements ConfigListener {
	private ReadConfigState state;
	private VarMap vars = new VarMap();
	private List<ModuleConfigListener> modules = new ArrayList<>();

	public DocProcessorConfigListener(ReadConfigState state) {
		this.state = state;
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
			FlowMap flows = state.config.flowMap();
			ConfiguredProcessor proc = new ConfiguredProcessor(state.config.extensions(), flows, state.root, new BlockishElementFactory(), vars, state.debug);
			flows.callbackFlow("header");
			flows.flow("main");
			flows.flow("footnotes");
			flows.callbackFlow("footer");
			proc.setDefaultHandler(StandardLineProcessor.class);
			proc.setBlankHandler(NewParaProcessor.class);
			proc.addScanner(AmpSpotter.class);
			proc.addScanner(AtSpotter.class);
			proc.addScanner(FieldSpotter.class);
			proc.addScanner(AtBlankSpotter.class);
			proc.addScanner(EndAtSpotter.class);
			state.config.processor(proc);
			for (ModuleConfigListener m : modules) {
				m.activate(proc);
			}
		} catch (RuntimeException ex) {
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ConfigException("Error creating DocProcessor: " + ex.getMessage());
		}
	}
}
