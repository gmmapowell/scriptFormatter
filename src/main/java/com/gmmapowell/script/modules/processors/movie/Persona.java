package com.gmmapowell.script.modules.processors.movie;

public class Persona {
	private final String name;

	public Persona(String name) {
		this.name = name;
	}

	public void addComment(String trim) {
	}

	public String getName() {
		return name.toUpperCase();
	}
}
