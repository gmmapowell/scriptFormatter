package com.gmmapowell.script.modules.processors.doc;

import com.gmmapowell.script.config.ScriptConfig;
import com.gmmapowell.script.config.reader.ReadConfigState;

public class InstallDocModule implements GlobalModuleInstaller {
	private final ReadConfigState state;
	private final ScriptConfig config;

	public InstallDocModule(ReadConfigState state) {
		this.state = state;
		this.config = state.config;
	}

	@Override
	public void install() {
		state.registerProcessor("doc", DocProcessorConfigListener.class);

		installAtCommands();
		installAmpCommands();
	}

	// @ commands
	private void installAtCommands() {
		// structure
		this.config.extensions().bindExtensionPoint(AtCommandHandler.class, ChapterCommand.class);
		this.config.extensions().bindExtensionPoint(AtCommandHandler.class, SectionCommand.class);
		this.config.extensions().bindExtensionPoint(AtCommandHandler.class, SubsectionCommand.class);
		
		// should commentary be in a separate module?
		this.config.extensions().bindExtensionPoint(AtCommandHandler.class, CommentaryCommand.class);
		
		// should footnote processing be in a separate module?
		this.config.extensions().bindExtensionPoint(AtCommandHandler.class, FootnoteCommand.class);
	}

	// & commands
	private void installAmpCommands() {
		this.config.extensions().bindExtensionPoint(AmpCommandHandler.class, FutureAmp.class);
		this.config.extensions().bindExtensionPoint(AmpCommandHandler.class, LinkAmp.class);
		this.config.extensions().bindExtensionPoint(AmpCommandHandler.class, OutrageAmp.class);
		this.config.extensions().bindExtensionPoint(AmpCommandHandler.class, ReviewAmp.class);
		this.config.extensions().bindExtensionPoint(AmpCommandHandler.class, SpaceAmp.class);
		this.config.extensions().bindExtensionPoint(AmpCommandHandler.class, TTAmp.class);
	}
}
