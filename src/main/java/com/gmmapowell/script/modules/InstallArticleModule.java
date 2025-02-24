package com.gmmapowell.script.modules;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.ScriptConfig;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.modules.processors.article.ArticleCommand;
import com.gmmapowell.script.modules.processors.article.ArticleProcessorConfigListener;
import com.gmmapowell.script.modules.processors.blog.BoldAmp;
import com.gmmapowell.script.modules.processors.blog.ImgAmp;
import com.gmmapowell.script.modules.processors.blog.ItalicAmp;
import com.gmmapowell.script.modules.processors.blog.LinkAmp;
import com.gmmapowell.script.modules.processors.blog.SupAmp;
import com.gmmapowell.script.modules.processors.blog.TTAmp;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.AtCommandHandler;
import com.gmmapowell.script.modules.processors.doc.DollarHandler;
import com.gmmapowell.script.modules.processors.doc.ForceSpaceHandler;
import com.gmmapowell.script.modules.processors.doc.NeedBreakAmp;
import com.gmmapowell.script.modules.processors.doc.SectionCommand;
import com.gmmapowell.script.modules.processors.doc.SpaceAmp;
import com.gmmapowell.script.processor.configured.InlineCommandHandler;
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
		this.config.extensions().bindExtensionPoint(AtCommandHandler.class, SectionCommand.class);
	}

	// & commands
	private void installAmpCommands() {
		this.config.extensions().bindExtensionPoint(AmpCommandHandler.class, ImgAmp.class);
		this.config.extensions().bindExtensionPoint(AmpCommandHandler.class, LinkAmp.class);
		this.config.extensions().bindExtensionPoint(AmpCommandHandler.class, SpaceAmp.class);
		this.config.extensions().bindExtensionPoint(AmpCommandHandler.class, NeedBreakAmp.class);

		this.config.extensions().bindExtensionPoint(AmpCommandHandler.class, BoldAmp.class);
		this.config.extensions().bindExtensionPoint(AmpCommandHandler.class, ItalicAmp.class);
		this.config.extensions().bindExtensionPoint(AmpCommandHandler.class, TTAmp.class);
	}

	// & commands that appear in the line rather than at the start
	private void installInlineCommands() {
		this.config.extensions().bindExtensionPoint(InlineCommandHandler.class, ForceSpaceHandler.class);
		this.config.extensions().bindExtensionPoint(InlineCommandHandler.class, SupAmp.class);
		this.config.extensions().bindExtensionPoint(InlineCommandHandler.class, DollarHandler.class);
	}
}
