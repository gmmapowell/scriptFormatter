package com.gmmapowell.script.kNodes;

public class Link<T extends KNodeItem> {

	public final KNode<T> from;
	public final KNode<T> to;

	public Link(KNode<T> from, KNode<T> to) {
		this.from = from;
		this.to = to;
	}

}
