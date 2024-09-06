package com.gmmapowell.script.modules.processors.blog;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.elements.block.BlockishElementFactory;
import com.gmmapowell.script.flow.FlowMap;
import com.gmmapowell.script.modules.processors.doc.AmpSpotter;
import com.gmmapowell.script.modules.processors.doc.AtSpotter;
import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.processor.NewParaProcessor;
import com.gmmapowell.script.processor.configured.ConfiguredProcessor;
import com.gmmapowell.script.processor.configured.StandardLineProcessor;
import com.gmmapowell.script.processor.prose.BlogProcessor;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.utils.Command;

public class BlogProcessorConfigListener implements ConfigListener {
	private ReadConfigState state;
	private VarMap vars = new VarMap();

	public BlogProcessorConfigListener(ReadConfigState state) {
		this.state = state;
	}
	
	@Override
	public ConfigListener dispatch(Command cmd) {
//		switch (cmd.name()) {
//		default: {
			throw new NotImplementedException("blog processor does not have any parameters");
//		}
//		}
	}

	@Override
	public void complete() throws ConfigException {
		try {
			GlobalState global = state.config.newGlobalState();
			ConfiguredProcessor proc = new ConfiguredProcessor(global, state.root, new BlockishElementFactory(), vars, state.debug);
			proc.setDefaultHandler(StandardLineProcessor.class);
			proc.setBlankHandler(NewParaProcessor.class);
			proc.addScanner(AmpSpotter.class);
			proc.addScanner(HeadingSpotter.class);
			proc.addScanner(BulletSpotter.class);
			proc.lifecycleObserver(new BloggerLifecycleObserver(proc));
			state.config.processor(proc);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ConfigException("Error creating BloggerProcessor: " + ex.getMessage());
		}
	}
}
