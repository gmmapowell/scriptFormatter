package com.gmmapowell.script.modules.processors.blog;

import java.util.ArrayList;
import java.util.List;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ModuleConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.elements.block.BlockishElementFactory;
import com.gmmapowell.script.modules.processors.blog.diagram.DiagramSpotter;
import com.gmmapowell.script.modules.processors.doc.AmpSpotter;
import com.gmmapowell.script.modules.processors.doc.GlobalState;
import com.gmmapowell.script.processor.NewParaProcessor;
import com.gmmapowell.script.processor.configured.ConfiguredProcessor;
import com.gmmapowell.script.processor.configured.StandardLineProcessor;
import com.gmmapowell.script.utils.Command;

public class BlogProcessorConfigListener implements ConfigListener {
	private ReadConfigState state;
	private VarMap vars = new VarMap();
	private List<ModuleConfigListener> modules = new ArrayList<>();

	public BlogProcessorConfigListener(ReadConfigState state) {
		this.state = state;
	}
	
	@Override
	public ConfigListener dispatch(Command cmd) {
		switch (cmd.name()) {
		case "module": {
			ModuleConfigListener nmc = state.module(cmd.line().readArg());
			modules.add(nmc);
			return nmc;
		}
		default: {
			throw new NotImplementedException("blog processor only supports loading modules");
		}
		}
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
			proc.addScanner(BlockquoteSpotter.class);
			proc.addScanner(DiagramSpotter.class);
			proc.lifecycleObserver(new BloggerLifecycleObserver(proc));
			state.config.processor(proc);
			for (ModuleConfigListener m : modules) {
				m.activate(proc);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ConfigException("Error creating BloggerProcessor: " + ex.getMessage());
		}
	}
}
