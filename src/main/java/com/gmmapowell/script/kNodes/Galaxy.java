package com.gmmapowell.script.kNodes;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class Galaxy<T extends KNodeItem> {
	public static final float CellSize = 7.0f;
	public static final float PlanetSize = 1.0f;
	public static final float MinDist = 10.0f;
	private final List<KNode<T>> sparse;
	private int ncells;

	public Galaxy(JSONObject meta, List<T> items) {
		this.sparse = new ArrayList<KNode<T>>();
		Random r = new Random();
		ncells = figureCount(items.size());
		JSONObject slides = null;
		try {
			if (meta != null) {
				if (meta.getInt("ncells") != ncells) { // if there are too many cells, we cannot continue with the current placement
					meta = null;
				} else if (meta.has("slides")) {
					slides = meta.getJSONObject("slides");
				}
			}
		} catch (JSONException ex) {
			meta = null;
		}
		@SuppressWarnings("unchecked")
		KNode<T>[] occupation = new KNode[ncells*ncells*ncells];
		int idx = 0;
		for (T item : items) {
			String name = item.name();
			JSONObject mi = null;
			if (slides != null && slides.has(name)) {
				try {
					mi = slides.getJSONObject(name);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			place(idx++, ncells, occupation, r, item, mi);
		}
		for (int i=0;i<occupation.length;i++) {
			if (occupation[i] == null)
				continue;
			occupation[i].locate(ncells, occupation, r);
		}
		for (int i=1;i<sparse.size();i++) {
			sparse.get(i-1).join(sparse.get(i));
		}
	}

	private void place(int idx, int ncells, KNode<T>[] occupation, Random r, T item, JSONObject mi) {
		int which = -1;
		try {
			if (mi != null && mi.has("which")) {
				which = mi.getInt("which");
			}
		} catch (JSONException ex) {
			//
		}
		while (which == -1) {
			int w = r.nextInt(ncells*ncells*ncells);
			if (occupation[w] == null)
				which = w;
		}
		KNode<T> kn = new KNode<T>(item, which, ncells, mi);
		occupation[which] = kn;
		sparse.add(kn);
	}

	private int figureCount(int size) {
		int n=0;
		while (n*n*n < size)
			n++;
		return n;
	}

	public void asJson(Writer writer) throws IOException {
		JsonFactory jf = new JsonFactory();
		JsonGenerator gen = jf.createGenerator(writer);
		gen.writeStartObject();
		gen.writeFieldName("slides");
		gen.writeStartArray();
		for (KNode<T> kn : sparse) {
			kn.asJson(gen);
		}
		gen.writeEndArray();
		gen.writeFieldName("tunnels");
		gen.writeStartArray();
		for (KNode<T> kn : sparse) {
			kn.tunnels(gen);
		}
		gen.writeEndArray();
		gen.writeEndObject();
		gen.flush();
	}

	public void writeMeta(Writer writer) throws IOException {
		JsonFactory jf = new JsonFactory();
		JsonGenerator gen = jf.createGenerator(writer);
		gen.writeStartObject();
		gen.writeNumberField("ncells", ncells);
		gen.writeFieldName("slides");
		gen.writeStartObject();
		for (KNode<T> kn : sparse) {
			kn.meta(gen);
		}
		gen.writeEndObject();
		gen.writeEndObject();
		gen.flush();
	}
}
