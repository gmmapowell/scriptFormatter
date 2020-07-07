package com.gmmapowell.script.kNodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Galaxy<T> {
	public static final float CellSize = 7.0f;
	public static final float PlanetSize = 1.0f;
	public static final float MinDist = 10.0f;
	private final List<T> items;
	private final List<KNode<T>> sparse;

	public Galaxy(List<T> items) {
		this.items = items;
		this.sparse = new ArrayList<KNode<T>>();
		Random r = new Random();
		int ncells = figureCount(items.size());
		@SuppressWarnings("unchecked")
		KNode<T>[] occupation = new KNode[ncells*ncells*ncells];
		for (T item : items)
			place(ncells, occupation, r, item);
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

	private void place(int ncells, KNode<T>[] occupation, Random r, T item) {
		int which;
		while (true) {
			which = r.nextInt(ncells*ncells*ncells);
			if (occupation[which] == null)
				break;
		}
		KNode<T> kn = new KNode<T>(item, which, ncells);
		occupation[which] = kn;
		sparse.add(kn);
	}

	private int figureCount(int size) {
		int n=0;
		while (n*n*n < size)
			n++;
		return n;
	}
}
