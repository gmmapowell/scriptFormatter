package com.gmmapowell.script.presenter.nodes;

import java.util.ArrayList;
import java.util.List;

public class Presentation {
	public final String name;
	private final List<Slide> slides = new ArrayList<>();
	
	public Presentation(String name) {
		this.name = name;
	}

	public void add(Slide slide) {
		System.out.println("Adding slide " + slide.name());
		this.slides.add(slide);
	}
	
	@Override
	public String toString() {
		return "Presentation[" + slides.size() + "]";
	}

	public List<Slide> slides() {
		return slides;
	}
}
