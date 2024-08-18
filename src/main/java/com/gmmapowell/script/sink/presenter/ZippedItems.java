package com.gmmapowell.script.sink.presenter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zinutils.exceptions.CantHappenException;

import com.gmmapowell.script.flow.SpanItem;

public class ZippedItems implements Iterable<ZipItem> {
	private final List<ZipItem> items;

	public ZippedItems(List<List<SpanItem>> ps, List<List<SpanItem>> ns) {
		if (ns.size() != ps.size())
			throw new CantHappenException("lists are of different lengths");
		items = new ArrayList<ZipItem>();
		for (int i=0;i<ps.size();i++) {
			if (ps.get(i).size() == 0 && ns.get(i).size() == 0)
				continue;
			items.add(new ZipItem(ps.get(i), ns.get(i)));
		}
	}

	@Override
	public Iterator<ZipItem> iterator() {
		return items.iterator();
	}
}
