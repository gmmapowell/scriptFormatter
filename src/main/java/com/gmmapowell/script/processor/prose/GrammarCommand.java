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

import com.gmmapowell.script.flow.NonBreakingSpace;

public class GrammarCommand implements ProductionVisitor, InlineCommand {
	private final Production rule;
	private Object block;
	private List<String> dontShow = new ArrayList<String>();
	private boolean justIndented;
	private DocState state;

	public GrammarCommand(Production rule, DocState state) {
		this.rule = rule;
		this.state = state;
	}

	@Override
	public void execute() throws IOException {
		block = "hello";
		state.newPara("grammar");
		state.newSpan("grammar-number");
		state.text("(" + rule.number + ")");
		state.newSpan("grammar-name");
		state.text(rule.name);
		state.newSpan("grammar-op");
		state.text("::=");
		state.newSpan();
		try {
			rule.visit(this);
			state.endPara();
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
		defns.get(0).visit(this);
		for (int i=1;i<defns.size();i++) {
			block = "hello";
			state.newPara("grammar");
			state.newSpan("grammar-blank");
			state.op(new NonBreakingSpace());
			state.newSpan("grammar-op");
			state.text("|");
			state.newSpan();
			defns.get(i).visit(this);
			if (block == null)
				state.deletePara();
			block = null;
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
		state.newPara("grammar");
		state.newSpan("grammar-op");
		state.text(">>");
		state.newSpan();
		// always return true - this value is part of the SentenceProducer logic
		return true;
	}

	@Override
	public void nestName(int arg0) {
		System.out.println("this is for the >>! cases");
	}

	@Override
	public void pushPart(String arg0, String arg1, boolean arg2) {
		throw new NotImplementedException();
	}

	@Override
	public void referTo(String prod, boolean resetToken) {
		if (dontShow.contains(prod))
			block = null;
		else if (block != null) {
			state.op(new NonBreakingSpace());
			state.text(prod);
		}
	}

	@Override
	public void token(String token, String arg1, UseNameForScoping arg2, List<Matcher> arg3, boolean repeatLast, boolean saveLast, String generator, boolean space) {
		if (block != null) {
			state.nestSpan("bold");
			state.op(new NonBreakingSpace());
			state.text(token);
			state.popSpan();
		}
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
			if (block != null) {
				state.text("*");
			}
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
				state.text("?");
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
				state.text("+");
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
		state.newPara("grammar");
		state.newSpan("grammar-blank");
		state.newSpan("grammar-op");
		state.text(quant);
	}
}
