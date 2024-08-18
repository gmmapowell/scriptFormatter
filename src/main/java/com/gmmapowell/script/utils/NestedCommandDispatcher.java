package com.gmmapowell.script.utils;

import java.util.ArrayList;
import java.util.List;

import org.zinutils.exceptions.CantHappenException;

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
	private ConfigListener pendingNest;

	public NestedCommandDispatcher(T readConfigState, NestedListener top) {
		lsnrs.add(new DepthListener(0, top));
	}

	public void dispatch(Command cmd) {
		int k = cmd.depth();
		if (k > lsnrs.get(0).depth) {
			if (pendingNest == null)
				throw new CantHappenException("there needs to be a pending nest for deeper scope"); // TODO: make this a proper error
			lsnrs.add(0, new DepthListener(k, pendingNest));
		}
		while (true) {
			DepthListener dl = lsnrs.get(0);
			if (k < dl.depth) {
				dl.lsnr.complete();
				lsnrs.remove(0);
			} else
				break;
		}
		if (k != lsnrs.get(0).depth) {
			throw new CantHappenException("inconsistent nesting"); // TODO: make this a proper error
		}
		pendingNest = lsnrs.get(0).lsnr.dispatch(cmd);
	}
}
