package com.gmmapowell.script.sink.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;
import org.zinutils.utils.FileUtils;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.flow.Cursor;
import com.gmmapowell.script.flow.Flow;
import com.gmmapowell.script.flow.ImageOp;
import com.gmmapowell.script.flow.LinkOp;
import com.gmmapowell.script.flow.NonBreakingSpace;
import com.gmmapowell.script.flow.ParaBreak;
import com.gmmapowell.script.flow.Section;
import com.gmmapowell.script.flow.SaveAs;
import com.gmmapowell.script.flow.StyledToken;
import com.gmmapowell.script.flow.TextSpanItem;
import com.gmmapowell.script.sink.Sink;

public class HTMLSink implements Sink {
	public enum Mode {
		START, NORMAL, LIST, BLOCKQUOTE
	}

	private PrintWriter writer;
	private StringWriter sw;
	private List<Flow> flows = new ArrayList<>();
	private final Region storeInto;

	public HTMLSink(Region root, String storeInto) throws IOException, GeneralSecurityException {
		this.storeInto = root.subregion(storeInto);
	}

	@Override
	public void prepare() throws Exception {
	}

	@Override
	public void flow(Flow flow) {
		flows.add(flow);
	}

	@Override
	public void render() throws IOException {
		for (Flow f : flows) {
			String saveAs = null;
			this.sw = new StringWriter();
			writer = new PrintWriter(sw);
			boolean haveBreak = true;
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
						if (haveBreak && !last.equals("blockquote")) {
							writer.print("<p>");
						}
						writer.print(entitify(((TextSpanItem)tok.it).text));
						haveBreak = false;
					} else if (tok.it instanceof BreakingSpace) {
						if (last.equals("blockquote"))
							writer.println("&nbsp;");
						else
							writer.print(" ");
					} else if (tok.it instanceof NonBreakingSpace) {
						writer.println("&nbsp;");
					} else if (tok.it instanceof ParaBreak) {
						if (hadBreak) // ignore multiple consecutive BRKs
							continue;
						switch (last) {
						case "bullet":
							writer.print("</p>");
							last = "needli";
							break;
						case "text":
							writer.print("</p>");
//							writer.print("<br/>");
							break;
						case "blockquote":
//							writer.print("</p>");
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
					} else if (tok.it instanceof SaveAs) {
						saveAs = ((SaveAs)tok.it).name();
					} else
						throw new NotImplementedException("cannot handle token " + tok.it);
				}
				transition(cf, last, "text");
			}
			writer.close();
			if (saveAs == null)
				throw new CantHappenException("saveAs was not defined");
			Place html = storeInto.ensureRegionAndPlace(saveAs + ".html");
			html.store(sw.toString());
			FileUtils.cat(GeoFSUtils.file(html));
		}
	}

	private String transition(List<String> cf, String last, StyledToken tok) {
		if (tok.styles.isEmpty())
			return last;
//		if (last.equals("text") && tok.styles.get(0).equals("text") && !haveBreak) {
//			writer.println("<br/>");
//			haveBreak = true;
//		}
		String moveTo = tok.styles.get(0);
		if ("section-title".equals(moveTo))
			moveTo = "h2";
		else if ("subsection-title".equals(moveTo))
			moveTo = "h3";
		return transition(cf, last, moveTo);
	}

	private String transition(List<String> cf, String last, String next) {
		if (next.equals(last))
			return last;

		if (last.equals("blockquote")) {
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
			writer.println("<blockquote class='article_blockquote'>");
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

	@Override
	public void finish() throws Exception {
	}
}
