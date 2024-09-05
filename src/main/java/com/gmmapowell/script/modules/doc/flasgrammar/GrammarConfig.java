package com.gmmapowell.script.modules.doc.flasgrammar;

import org.flasck.flas.grammar.Grammar;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.utils.GeoFSUtils;

public class GrammarConfig {
	private Grammar grammar;

	public void setGrammar(Place grammar) {
		System.out.println("grammar at " + grammar);
		this.grammar = Grammar.from(GeoFSUtils.readXML(grammar));
	}

	public Grammar grammar() {
		return grammar;
	}

}
