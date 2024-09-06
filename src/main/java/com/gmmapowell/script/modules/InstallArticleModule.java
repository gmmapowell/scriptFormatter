package com.gmmapowell.script.modules;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ScriptConfig;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.modules.processors.article.ArticleCommand;
import com.gmmapowell.script.modules.processors.article.ArticleProcessorConfigListener;
import com.gmmapowell.script.modules.processors.doc.AtCommandHandler;
import com.gmmapowell.script.utils.Command;

public class InstallArticleModule implements ConfigListener {
	private final ReadConfigState state;
	private final ScriptConfig config;

	public InstallArticleModule(ReadConfigState state) {
		this.state = state;
		this.config = state.config;
	}

	@Override
	public ConfigListener dispatch(Command cmd) throws Exception {
		throw new ConfigException("InstallArticleModule cannot be configured right now");
	}

	@Override
	public void complete() throws Exception {
		state.registerProcessor("article", ArticleProcessorConfigListener.class);

		installAtCommands();
		installAmpCommands();
		installInlineCommands();
	}

	// @ commands
	private void installAtCommands() {
		// structure
		this.config.extensions().bindExtensionPoint(AtCommandHandler.class, ArticleCommand.class);
	}

	// & commands
	private void installAmpCommands() {
	}

	// & commands that appear in the line rather than at the start
	private void installInlineCommands() {
	}
}
