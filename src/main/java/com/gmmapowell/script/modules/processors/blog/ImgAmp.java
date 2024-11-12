package com.gmmapowell.script.modules.processors.blog;

import com.gmmapowell.script.flow.ImageOp;
import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class ImgAmp implements AmpCommandHandler {
	private final ConfiguredState state;

	public ImgAmp(ScannerAmpState state) {
		this.state = state.state();
	}
	
	@Override
	public String name() {
		return "img";
	}

	@Override
	public void invoke(AmpCommand cmd) {
		state.newPara("text");
		state.newSpan();
		// You can, of course, link any public image
		// 
		// If you want to link anything else, we have a problem:
		//   * Blogger API really doesn't support uploading images, so let's load them from DH
		//   * We can upload to gmmapowell.com/blog-images/<name> and then link that here ...
		//
		// We could "automate" uploading local files by having an optional second argument which we upload
		String link = cmd.args.readString();
		state.op(new ImageOp(link));
		state.endPara();
		state.observeBlanks();
	}
}
