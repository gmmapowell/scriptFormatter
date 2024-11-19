package com.gmmapowell.script.modules.processors.blog;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.gmmapowell.script.flow.ImageOp;
import com.gmmapowell.script.modules.processors.doc.AmpCommand;
import com.gmmapowell.script.modules.processors.doc.AmpCommandHandler;
import com.gmmapowell.script.modules.processors.doc.ScannerAmpState;
import com.gmmapowell.script.processor.configured.ConfiguredState;

public class ImgAmp implements AmpCommandHandler {
	private final ConfiguredState state;
	private final UploadAll uploader;

	public ImgAmp(ScannerAmpState state) {
		this.state = state.state();
		this.uploader = state.global().requireState(UploadAll.class);
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
		String img;
		try {
			String link = cmd.args.readString();
			if (link.startsWith("http:") || link.startsWith("https:")) {
				img = link;
			} else if (link.startsWith("lfs")) {
				Place p = state.global().getUniverse().placePath(link);
				img = uploader.upload(GeoFSUtils.file(p), p.name());
			} else
				throw new CantHappenException("cannot handle img link: " + link);
		} catch (Exception ex) {
			throw WrappedException.wrap(ex);
		}
		state.op(new ImageOp(img));
		state.endPara();
		state.observeBlanks();
	}
}
