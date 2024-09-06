package com.gmmapowell.script.modules.processors.article;

import com.gmmapowell.script.config.GlobalModuleInstaller;
import com.gmmapowell.script.config.ScriptConfig;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.modules.processors.doc.AtCommandHandler;

public class InstallArticleModule implements GlobalModuleInstaller {
	private final ReadConfigState state;
	private final ScriptConfig config;

	public InstallArticleModule(ReadConfigState state) {
		this.state = state;
		this.config = state.config;
	}

	@Override
	public void install() {
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
