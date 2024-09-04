package com.gmmapowell.script.modules.doc.flasgrammar;

import com.gmmapowell.geofs.Place;

public class GrammarConfig {
	private Place grammar;

	public void setGrammar(Place grammar) {
		System.out.println("grammar at " + grammar);
		this.grammar = grammar;
	}

	public Place samples() {
		return grammar;
	}

}
