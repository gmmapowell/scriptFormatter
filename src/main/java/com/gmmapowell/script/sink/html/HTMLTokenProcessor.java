package com.gmmapowell.script.sink.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.flow.AnchorOp;
import com.gmmapowell.script.flow.BreakingSpace;
import com.gmmapowell.script.flow.Cursor;
import com.gmmapowell.script.flow.CursorClient;
import com.gmmapowell.script.flow.CursorFeedback;
import com.gmmapowell.script.flow.ImageOp;
import com.gmmapowell.script.flow.LinkFromTOC;
import com.gmmapowell.script.flow.LinkOp;
import com.gmmapowell.script.flow.NonBreakingSpace;
import com.gmmapowell.script.flow.NothingOp;
import com.gmmapowell.script.flow.ParaBreak;
import com.gmmapowell.script.flow.ReleaseFlow;
import com.gmmapowell.script.flow.SaveAs;
import com.gmmapowell.script.flow.StyledToken;
import com.gmmapowell.script.flow.SyncAfterFlow;
import com.gmmapowell.script.flow.TextSpanItem;
import com.gmmapowell.script.flow.YieldToFlow;
import com.gmmapowell.script.modules.sink.github.IndexPageCreator;

public class HTMLTokenProcessor implements CursorClient {
	private final Region storeInto;
	private StringWriter sw;
	private PrintWriter writer;
	private String saveAs = null;
	private boolean haveBreak = true;
	private String last = "text";
	private List<String> cf = new ArrayList<>();
	private Place pre;
	private Place post;
	private IndexPageCreator ipg;
	
	public HTMLTokenProcessor(Region storeInto, Place pre, Place post, IndexPageCreator ipg) {
		this.storeInto = storeInto;
		this.pre = pre;
		this.post = post;
		this.ipg = ipg;
	}

	@Override
	public void beginSection(Set<Cursor> cursors) {
		this.sw = new StringWriter();
		this.writer = new PrintWriter(sw);
		this.write(pre);
		haveBreak = true;
		last = "text";
		saveAs = null;
	}

	@Override
	public boolean processToken(CursorFeedback cursor, StyledToken tok) throws IOException {
		System.out.println("TOK: " + tok);
		last = transition(cf, last, tok);
		boolean hadBreak = haveBreak;
		figureStyles(cf, tok.styles);
		cf = new ArrayList<>(tok.styles);
		if (ipg != null) {
			ipg.processToken(tok);
		}
		if (tok.it instanceof TextSpanItem) {
			if (haveBreak) {
				if (!last.equals("blockquote") && !last.equals("as-div")) {
					writer.print("<p>");
				}
			}
			writer.print(entitify(((TextSpanItem) tok.it).text));
			haveBreak = false;
		} else if (tok.it instanceof NothingOp) {
		} else if (tok.it instanceof BreakingSpace) {
			if (last.equals("blockquote"))
				writer.println("&nbsp;");
			else
				writer.print(" ");
		} else if (tok.it instanceof NonBreakingSpace) {
			writer.println("&nbsp;");
		} else if (tok.it instanceof ParaBreak) {
			if (hadBreak) // ignore multiple consecutive BRKs
				return true;
			switch (last) {
			case "bullet":
				writer.print("</p>");
				last = "needli";
				break;
			case "text":
				writer.print("</p>");
//				writer.print("<br/>");
				break;
			case "blockquote":
//				writer.print("</p>");
				writer.print("<br/>");
				break;
			case "h1":
			case "h2":
			case "h3":
			case "as-div":
				break; // it happens automatically
				
			case "chapter-title":
				System.out.println(last);
				break;
			case "footnote":
				System.out.println(last);
				break;
			case "locate-place":
				System.out.println(last);
				break;
			case "locate-date":
				System.out.println(last);
				break;
			case "poetry":
				System.out.println(last);
				break;
			default:
				throw new CantHappenException("cannot handle BRKPara in " + last);
			}
			writer.println();
			haveBreak = true;
		} else if (tok.it instanceof ImageOp) {
			writer.print("<img border='0' src=\'" + ((ImageOp) tok.it).uri + "' />");
		} else if (tok.it instanceof LinkOp) {
			LinkOp l = (LinkOp) tok.it;
			writer.print("<a href='" + l.lk + "'>");
			writer.print(l.tx);
			writer.print("</a>");
		} else if (tok.it instanceof SaveAs) {
			saveAs = ((SaveAs) tok.it).name();
		} else if (tok.it instanceof YieldToFlow || tok.it instanceof ReleaseFlow || tok.it instanceof SyncAfterFlow) {
			System.out.println("cannot handle token " + tok.it);
		} else if (tok.it instanceof AnchorOp || tok.it instanceof LinkFromTOC) {
			System.out.println("cannot handle token " + tok.it);
		} else if (tok.it.toString().equals("SectionBreak")) {
			System.out.println("cannot handle token " + tok.it);
		} else
			throw new NotImplementedException("cannot handle token " + tok.it);
		return true;
	}

	@Override
	public void endSection() throws IOException {
		transition(cf, last, "text");
		this.write(post);
		writer.close();
		if (saveAs != null) {
			if (saveAs.endsWith(".txt")) {
				saveAs = saveAs.replaceFirst(".txt$", "");
			}
			Place html = storeInto.ensureRegionAndPlace(saveAs + ".html");
			if (ipg != null) {
				ipg.haveFile(html.name());
			}
			html.store(sw.toString());
//			FileUtils.cat(GeoFSUtils.file(html));
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
		for (int i = sb.length() - 1; i >= 0; i--) {
			if (i < spaces) {
				sb.replace(i, i + 1, "&nbsp;");
			} else
				switch (sb.charAt(i)) {
				case '&': {
					sb.replace(i, i + 1, "&amp;");
					break;
				}
				case '<': {
					sb.replace(i, i + 1, "&lt;");
					break;
				}
				case '>': {
					sb.replace(i, i + 1, "&gt;");
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
			for (int i = 1; i < styles.size(); i++) {
				String sty = styles.get(i);
				if (cf.size() > i && cf.get(i).equals(sty))
					continue;
				else if (cf.size() > i) {
					drawDownTo(cf, i);
				}
				if ("link".equals(sty) || "endlink".equals(sty))
					; // don't print these
				else {
					writer.print("<" + mapStyle(sty, true) + ">");
					cf.add(sty);
				}
			}
		}
	}

	private void drawDownTo(List<String> cf, int to) {
		while (cf.size() > to) {
			writer.print("</" + mapStyle(cf.remove(cf.size() - 1), false) + ">");
		}
	}

	private String mapStyle(String sty, boolean open) {
		if (sty.startsWith("div-")) {
			if (!open) {
				return "div";
			}
			String classes = sty.replace("div-", "").replace("-", " ");
			if (classes != "") {
				return "div class='" + classes + "'";
			}
			return "div";
		}
		switch (sty) {
		case "italic":
			return "i";
		case "bold":
			return "b";
		default:
			return sty;
		}
	}

	public void write(Place from) {
		if (from == null) {
			return;
		}
		from.writeTo(writer);
	}
}
