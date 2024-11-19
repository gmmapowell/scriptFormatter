package com.gmmapowell.script.modules;

import com.gmmapowell.script.config.ScriptConfig;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.modules.blog.uploader.DHUploader;
import com.gmmapowell.script.modules.processors.blog.ImgUploader;
import com.gmmapowell.script.utils.Command;

public class InstallDhuploaderModule implements ConfigListener {
	private final ScriptConfig config;

	public InstallDhuploaderModule(ReadConfigState state) {
//		this.state = state;
		this.config = state.config;
	}

	@Override
	public ConfigListener dispatch(Command cmd) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void complete() throws Exception {
		this.config.extensions().bindExtensionPoint(ImgUploader.class, DHUploader.class);
	}

}
