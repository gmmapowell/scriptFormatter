package com.gmmapowell.script.modules.sink.pdf;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.config.ConfigException;
import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.reader.ConfigListener;
import com.gmmapowell.script.config.reader.ReadConfigState;
import com.gmmapowell.script.sink.pdf.PDFSink;
import com.gmmapowell.script.styles.ConfigurableStyleCatalog;
import com.gmmapowell.script.styles.StyleCatalog;
import com.gmmapowell.script.utils.Command;
import com.gmmapowell.script.utils.Utils;

public class PDFSinkConfigListener implements ConfigListener {
	private ReadConfigState state;
	private VarMap vars = new VarMap();

	public PDFSinkConfigListener(ReadConfigState state) {
		this.state = state;
	}
	
	@Override
	public ConfigListener dispatch(Command cmd) {
		switch (cmd.name()) {
		case "file": 
		case "dir":
		case "styles":
		case "stock":
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
		String dir = vars.remove("dir");
		if (file == null && dir == null)
			throw new ConfigException("output file or directory was not defined");
		String open = vars.remove("open");
		boolean wantOpen = false;
		if ("true".equals(open))
			wantOpen = true;
		String upload = vars.remove("upload");
		String styles = vars.remove("styles");
		if (styles == null)
			throw new ConfigException("style catalog was not defined");
		styles = Utils.subenvs(styles);
		StyleCatalog catalog;
		try {
			catalog = (StyleCatalog) Class.forName(styles).getConstructor().newInstance();
		} catch (Exception ex) {
			try {
				catalog = new ConfigurableStyleCatalog(state.root.place(styles), state.debug);
			} catch (Exception e2) {
				throw new ConfigException(e2.getMessage());
			}
		}
		try {
			state.config.sink(new PDFSink(state.root, catalog, file, dir, wantOpen, upload, state.debug, state.sshid, vars));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ConfigException("Error creating PDFSink: " + ex.getMessage());
		}
	}

}
