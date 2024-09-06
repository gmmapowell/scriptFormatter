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
		// Doing this properly may require another API (picker?)
		// See: https://bloggerdev.narkive.com/SC3HJ3UM/upload-images-using-blogger-api
		// For now, upload the image by hand and put the URL here
		//  Obvs you can also use any absolute URL
		String link = cmd.args.readString();
		state.op(new ImageOp(link));
		state.endPara();
		state.observeBlanks();
	}
}
