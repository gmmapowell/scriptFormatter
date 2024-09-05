package com.gmmapowell.script.utils;

import java.util.ArrayList;
import java.util.List;

import com.gmmapowell.script.config.reader.ConfigListener;

public class NestedCommandDispatcher<T extends FileWithLocation> implements CommandDispatcher {
	public class DepthListener {
		final int depth;
		final NestedListener lsnr;
		
		public DepthListener(int depth, NestedListener lsnr) {
			this.depth = depth;
			this.lsnr = lsnr;
		}
	}

	private final List<DepthListener> lsnrs = new ArrayList<>();

	public NestedCommandDispatcher(T readConfigState, NestedListener top) {
		lsnrs.add(new DepthListener(-2, top));
	}

	@Override
	public void dispatch(Command cmd)  throws Exception {
		int k = cmd.depth();
		while (true) {
			DepthListener dl = lsnrs.get(0);
			if (k <= dl.depth) {
				if (dl.lsnr != null)
					dl.lsnr.complete();
				lsnrs.remove(0);
			} else
				break;
		}
		ConfigListener mayNest = lsnrs.get(0).lsnr.dispatch(cmd);
		lsnrs.add(0, new DepthListener(k, mayNest));
	}

	@Override
	public void complete() throws Exception {
		while (!lsnrs.isEmpty()) {
			NestedCommandDispatcher<T>.DepthListener dl = lsnrs.remove(0);
			if (dl.lsnr != null)
				dl.lsnr.complete();
		}
	}
}
