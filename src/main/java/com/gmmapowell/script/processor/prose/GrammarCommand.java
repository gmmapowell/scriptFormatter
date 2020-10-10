package com.gmmapowell.script.processor.prose;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.flasck.flas.grammar.Definition;
import org.flasck.flas.grammar.OrProduction;
import org.flasck.flas.grammar.Production;
import org.flasck.flas.grammar.ProductionVisitor;
import org.flasck.flas.grammar.SentenceProducer.UseNameForScoping;
import org.flasck.flas.grammar.TokenDefinition.Matcher;
import org.zinutils.exceptions.NotImplementedException;
import org.zinutils.exceptions.WrappedException;

import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.SpanBlock;
import com.gmmapowell.script.sink.Sink;

public class GrammarCommand implements ProductionVisitor, InlineCommand {
	private final ElementFactory ef;
	private final Production rule;
	private SpanBlock block;
	private Sink sink;
	private List<String> dontShow = new ArrayList<String>();
	private boolean justIndented;

	// I think we are also going to need ef and sink to process the (multiple) blocks we work through
	// We might even want to bring the rest of the block creation here
	public GrammarCommand(ElementFactory ef, Sink sink, Production rule) {
		this.ef = ef;
		this.sink = sink;
		this.rule = rule;
	}

	@Override
	public void execute(Sink sink, ElementFactory ef) throws IOException {
		this.block = ef.block("grammar");
		block.addSpan(ef.span("grammar-number", "(" + rule.number + ")"));
		block.addSpan(ef.span("grammar-name", rule.name));
		block.addSpan(ef.span("grammar-op", "::="));
		try {
			rule.visit(this);
			if (block != null)
				sink.block(block);
		} catch (NotImplementedException ex) {
			ex.printStackTrace(System.out);
		} catch (NullPointerException ex) {
			ex.printStackTrace(System.out);
		}
	}

	public void removeProd(String prod) {
		dontShow.add(prod);
	}

	@Override
	public void choices(OrProduction arg0, Object cxt, List<Definition> defns, List<Integer> probs, int arg3, boolean repeatVarName) {
		try {
			defns.get(0).visit(this);
			// NOTE: I don't think it's as hard as this makes out ... modularize and rerun the initial setup"
			if (block == null)
				throw new NotImplementedException("Revise the grammar to have the first case not be one you want to remove because it's hard to remove it");
			sink.block(block);
			for (int i=1;i<defns.size();i++) {
				block = ef.block("grammar");
				block.addSpan(ef.span("grammar-blank", ""));
				block.addSpan(ef.span("grammar-op", "|"));
				defns.get(i).visit(this);
				if (block != null)
					sink.block(block);
				block = null;
			}
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}

	@Override
	public void exdent() {
		// I think this is only relevant to the sentence producer
	}

	@Override
	public void futurePattern(String arg0, String arg1) {
	}

	@Override
	public boolean indent() {
		this.justIndented = true;
		try {
			if (block != null) {
				sink.block(block);
				block = ef.block("grammar");
			}
			block.addSpan(ef.span("grammar-op", ">>"));
			// always return true - this value is part of the SentenceProducer logic
			return true;
		} catch (IOException ex) {
			throw WrappedException.wrap(ex);
		}
	}

	@Override
	public void nestName(int arg0) {
	}

	@Override
	public void pushPart(String arg0, String arg1, boolean arg2) {
		throw new NotImplementedException();
	}

	@Override
	public void referTo(String prod, boolean resetToken) {
		if (dontShow.contains(prod))
			block = null;
		else if (block != null)
			block.addSpan(ef.span(null, " " + prod));
	}

	@Override
	public void token(String token, String arg1, UseNameForScoping arg2, List<Matcher> arg3, boolean repeatLast, boolean saveLast, String generator, boolean space) {
		if (block != null)
			block.addSpan(ef.span("bold", " " + token));
	}

	@Override
	public void visit(Definition child) {
		if (justIndented)
			handleIndentedList(">>!");
		child.visit(this);
	}

	@Override
	public int zeroOrMore(Definition child, boolean withEOL) {
		if (justIndented) {
			handleIndentedList(">>");
			child.visit(this);
		} else {
			child.visit(this);
			// I think "withEOL" is only relevant to the sentence producer
			if (block != null)
				block.addSpan(ef.span(null, "*"));
		}
		return 0;
	}

	@Override
	public void zeroOrOne(Definition child) {
		if (justIndented) {
			handleIndentedList(">>?");
			child.visit(this);
		} else {
			child.visit(this);
			if (block != null)
				block.addSpan(ef.span(null, "?"));
		}
	}

	@Override
	public int oneOrMore(Definition child, boolean withEOL) {
		if (justIndented) {
			handleIndentedList(">>>");
			child.visit(this);
		} else {
			child.visit(this);
			if (block != null)
				block.addSpan(ef.span(null, "+"));
		}
		return 0;
	}
	
	@Override
	public boolean indent(boolean force) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean complete(OrProduction prod, Object cxt, List<Definition> choices) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void exactly(int cnt, Definition child, boolean withEOL) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public OrProduction isOr(String child) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDictEntry(String var, String val) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDictValue(String var) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTopDictValue(String var) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearDictEntry(String var) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void condNotEqual(String var, String ne, Definition inner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void condNotSet(String var, Definition inner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pushCaseNumber() {
		// TODO Auto-generated method stub
		
	}

	private void handleIndentedList(String quant) {
		if (!justIndented)
			return;
		justIndented = false;
		block = ef.block("grammar");
		block.addSpan(ef.span("grammar-blank", ""));
		block.addSpan(ef.span("grammar-op", quant));
	}
}
