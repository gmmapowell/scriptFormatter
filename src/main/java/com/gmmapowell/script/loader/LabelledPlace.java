package com.gmmapowell.script.loader;

import com.gmmapowell.geofs.Place;

public class LabelledPlace {
	public final String label;
	public final Place place;
	
	public LabelledPlace(String label, Place place) {
		this.label = label;
		this.place = place;
	}
}
