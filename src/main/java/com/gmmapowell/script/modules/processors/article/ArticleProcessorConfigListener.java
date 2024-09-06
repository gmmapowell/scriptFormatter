package com.gmmapowell.script.modules.processors.article;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.elements.block.BlockishElementFactory;
import com.gmmapowell.script.flow.FlowMap;
import com.gmmapowell.script.modules.processors.doc.AmpSpotter;
import com.gmmapowell.script.modules.processors.doc.AtBlankSpotter;
import com.gmmapowell.script.modules.processors.doc.AtSpotter;
import com.gmmapowell.script.modules.processors.doc.EndAtSpotter;
import com.gmmapowell.script.modules.processors.doc.FieldSpotter;
import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.processor.NewParaProcessor;
import com.gmmapowell.script.processor.configured.ConfiguredProcessor;
import com.gmmapowell.script.processor.configured.StandardLineProcessor;
import com.gmmapowell.script.utils.Command;

public class ArticleProcessorConfigListener implements ConfigListener {
	private ReadConfigState state;
	private VarMap vars = new VarMap();

	public ArticleProcessorConfigListener(ReadConfigState state) {
		this.state = state;
	}
	
	@Override
	public ConfigListener dispatch(Command cmd) {
//		switch (cmd.name()) {
//		case "param":
//		{
//			vars.put(cmd.depth(), cmd.name(), cmd.line().readArg());
//			return null;
//		}
//		default: {
			throw new NotImplementedException("article processor does not have any parameters");
//		}
//		}
	}

	@Override
	public void complete() throws ConfigException {
		try {
			GlobalState global = state.config.newGlobalState();
			ConfiguredProcessor proc = new ConfiguredProcessor(global, state.root, new BlockishElementFactory(), vars, state.debug);
			FlowMap flows = global.flows();
			flows.flow("main");
			proc.setDefaultHandler(StandardLineProcessor.class);
			proc.setBlankHandler(NewParaProcessor.class);
			proc.addScanner(AmpSpotter.class);
			proc.addScanner(AtSpotter.class);
			proc.addScanner(FieldSpotter.class);
			proc.addScanner(AtBlankSpotter.class);
			proc.addScanner(EndAtSpotter.class);
			state.config.processor(proc);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ConfigException("Error creating ArticleProcessor: " + ex.getMessage());
		}
	}
}
