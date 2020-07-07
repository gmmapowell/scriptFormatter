package com.gmmapowell.script.kNodes;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class Galaxy<T extends KNodeItem> {
	public static final float CellSize = 7.0f;
	public static final float PlanetSize = 1.0f;
	public static final float MinDist = 10.0f;
	private final List<KNode<T>> sparse;

	public Galaxy(List<T> items) {
		this.sparse = new ArrayList<KNode<T>>();
		Random r = new Random();
		int ncells = figureCount(items.size());
		@SuppressWarnings("unchecked")
		KNode<T>[] occupation = new KNode[ncells*ncells*ncells];
		int idx = 0;
		for (T item : items)
			place(idx++, ncells, occupation, r, item);
		for (int i=0;i<occupation.length;i++) {
			if (occupation[i] == null)
				continue;
			occupation[i].locate(ncells, occupation, r);
		}
		for (int i=1;i<sparse.size();i++) {
			sparse.get(i-1).join(sparse.get(i));
		}
		System.out.println(Arrays.asList(occupation));
		System.out.println(sparse);
	}

	private void place(int idx, int ncells, KNode<T>[] occupation, Random r, T item) {
		int which;
		while (true) {
			which = r.nextInt(ncells*ncells*ncells);
			if (occupation[which] == null)
				break;
		}
		KNode<T> kn = new KNode<T>(idx, item, which, ncells);
		occupation[which] = kn;
		sparse.add(kn);
	}

	private int figureCount(int size) {
		int n=0;
		while (n*n*n < size)
			n++;
		return n;
	}

	public void asJson(Writer w) throws IOException {
		JsonFactory jf = new JsonFactory();
		JsonGenerator gen = jf.createGenerator(w);
		gen.writeStartArray();
		for (KNode<T> kn : sparse) {
			kn.asJson(gen);
		}
		gen.writeEndArray();
		gen.flush();
	}
}
