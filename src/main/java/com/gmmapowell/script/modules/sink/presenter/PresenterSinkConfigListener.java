package com.gmmapowell.script.modules.sink.presenter;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.sink.pdf.PDFSink;
import com.gmmapowell.script.sink.presenter.PresenterSink;
import com.gmmapowell.script.styles.ConfigurableStyleCatalog;
import com.gmmapowell.script.styles.StyleCatalog;
import com.gmmapowell.script.utils.Command;
import com.gmmapowell.script.utils.Utils;

public class PresenterSinkConfigListener implements ConfigListener {
	private ReadConfigState state;
	private VarMap vars = new VarMap();

	public PresenterSinkConfigListener(ReadConfigState state) {
		this.state = state;
	}
	
	@Override
	public ConfigListener dispatch(Command cmd) {
		switch (cmd.name()) {
		case "file": 
		case "meta":
		case "show":
		case "open":
		case "upload":
		{
			vars.put(cmd.depth(), cmd.name(), cmd.line().readArg());
			return null;
		}
		default: {
			throw new NotImplementedException(cmd.name());
		}
		}
	}

	@Override
	public void complete() throws ConfigException {
		String file = vars.remove("file");
		if (file == null)
			throw new ConfigException("output file was not defined");
		String meta = vars.remove("meta");
		if (meta == null)
			throw new ConfigException("meta file was not defined");
		String show = vars.remove("show");
		boolean wantShow = false;
		if ("true".equals(show))
			wantShow = true;
		String upload = vars.remove("upload");
		try {
			state.config.sink(new PresenterSink(state.root, file, meta, wantShow, upload, state.debug));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ConfigException("Error creating PresenterSink: " + ex.getMessage());
		}
	}

}
