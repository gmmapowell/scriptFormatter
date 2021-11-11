package com.gmmapowell.script.sink.html;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;
import org.zinutils.utils.FileUtils;

import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.ImageOp;
import com.gmmapowell.script.flow.LinkOp;
import com.gmmapowell.script.flow.ParaBreak;
import com.gmmapowell.script.flow.Section;
import com.gmmapowell.script.flow.SectionTitle;
import com.gmmapowell.script.flow.TextSpanItem;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.sink.pdf.Cursor;
import com.gmmapowell.script.sink.pdf.StyledToken;

public class HTMLSink implements Sink {
	public enum Mode {
		START, NORMAL, LIST, BLOCKQUOTE
	}

	private PrintWriter writer;
	private StringWriter sw;
	private List<Flow> flows = new ArrayList<>();
	private boolean haveBreak = true;
	private final File storeInto;

	public HTMLSink(File root, String storeInto) throws IOException, GeneralSecurityException {
		this.storeInto = new File(root, storeInto);
	}

	@Override
	public void flow(Flow flow) {
		flows.add(flow);
	}

	@Override
	public void render() throws IOException {
		for (Flow f : flows) {
			String title = null;
			this.sw = new StringWriter();
			writer = new PrintWriter(sw);
			for (Section s : f.sections) {
				Cursor c = new Cursor(f.name, s);
				String last = "text";
				List<String> cf = new ArrayList<>();
				StyledToken tok;
				while ((tok = c.next()) != null) {
					System.out.println(tok);
					last = transition(cf, last, tok);
					boolean hadBreak = haveBreak;
					figureStyles(cf, tok.styles);
					cf = new ArrayList<>(tok.styles);
					if (tok.it instanceof TextSpanItem) {
						writer.print(entitify(((TextSpanItem)tok.it).text));
						haveBreak = false;
					} else if (tok.it instanceof BreakingSpace) {
						if (last.equals("blockquote"))
							writer.println("&nbsp;");
						else
							writer.print(" ");
					} else if (tok.it instanceof ParaBreak) {
						if (hadBreak) // ignore multiple consecutive BRKs
							continue;
						switch (last) {
						case "bullet":
							last = "needli";
							break;
						case "text":
							writer.print("<br/>");
							break;
						case "blockquote":
							writer.print("<br/>");
							break;
						case "h1":
						case "h2":
						case "h3":
							break; // it happens automatically
						default:
							throw new CantHappenException("cannot handle BRKPara in " + last);
						}
						writer.println();
						haveBreak = true;
					} else if (tok.it instanceof ImageOp) {
						writer.print("<img border='0' src=\'" + ((ImageOp)tok.it).uri + "' />");
					} else if (tok.it instanceof LinkOp) {
						LinkOp l = (LinkOp) tok.it;
						writer.print("<a href='" + l.lk + "'>");
						writer.print(l.tx);
						writer.print("</a>");
					} else if (tok.it instanceof SectionTitle) {
						title = ((SectionTitle)tok.it).title();
					} else
						throw new NotImplementedException();
				}
				transition(cf, last, "text");
			}
			writer.close();
			if (title == null)
				throw new CantHappenException("title was not defined");
			FileUtils.writeFile(new File(storeInto, title), sw.toString());
			FileUtils.cat(new File(storeInto, title));
		}
	}

	private String transition(List<String> cf, String last, StyledToken tok) {
		if (tok.styles.isEmpty())
			return last;
//		if (last.equals("text") && tok.styles.get(0).equals("text") && !haveBreak) {
//			writer.println("<br/>");
//			haveBreak = true;
//		}
		return transition(cf, last, tok.styles.get(0));
	}

	private String transition(List<String> cf, String last, String next) {
		if (next.equals(last))
			return last;

		if (last.equals("blockquote")) {
			writer.println("</span>");
			writer.println("</blockquote>");
		}
		if (last.equals("needli") && !next.equals("bullet"))
			writer.println("</ul>");
		if (last.startsWith("h")) {
			writer.println("</" + last + ">");
		}
		drawDownTo(cf, 1);
		if (next.startsWith("h")) {
			writer.print("<" + next + ">");
		}
		if (next.equals("bullet")) {
			if (!last.equals("needli"))
				writer.println("<ul>");
			writer.print("<li>");
		}
		if (next.equals("blockquote")) {
			writer.println("<blockquote class='tr_bq'>");
			writer.println("<span style='color: blue; font-family: &quot;courier new&quot;, &quot;courier&quot;, monospace; font-size: x-small;'>");
		}
		return next;
	}

	private String entitify(String text) {
		StringBuilder sb = new StringBuilder(text);
		int spaces = 0;
		while (spaces < sb.length() && Character.isWhitespace(sb.charAt(spaces)))
			spaces++;
		// it is sort of easier to work backwards
		for (int i=sb.length()-1;i>=0;i--) {
			if (i < spaces) {
				sb.replace(i, i+1, "&nbsp;");
			} else switch (sb.charAt(i)) {
			case '&': {
				sb.replace(i, i+1, "&amp;");
				break;
			}
			case '<': {
				sb.replace(i, i+1, "&lt;");
				break;
			}
			case '>': {
				sb.replace(i, i+1, "&gt;");
				break;
			}
			default: {
				break;
			}
			}
		}
		return sb.toString();
	}

	private void figureStyles(List<String> cf, List<String> styles) {
		if (styles != null) {
			drawDownTo(cf, styles.size());
			for (int i=1;i<styles.size();i++) {
				String sty = styles.get(i);
				if (cf.size() > i && cf.get(i).equals(sty))
					continue;
				else if (cf.size() > i) {
					drawDownTo(cf, i);
				}
				if ("link".equals(sty) || "endlink".equals(sty))
					; // don't print these
				else {
					writer.print("<" + mapStyle(sty) + ">");
					cf.add(sty);
				}
			}
		}
	}
	
	private void drawDownTo(List<String> cf, int to) {
		while (cf.size() > to) {
			writer.print("</" + mapStyle(cf.remove(cf.size()-1)) + ">");
		}
	}
	
	private String mapStyle(String sty) {
		switch (sty) {
		case "italic":
			return "i";
		case "bold":
			return "b";
		default:
			return sty;
		}
	}

	@Override
	public void showFinal() {
	}
	
	@Override
	public void upload() throws Exception {
	}
}
