package com.gmmapowell.script.sink.blogger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

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
import com.gmmapowell.script.flow.StyledToken;
import com.gmmapowell.script.flow.TextSpanItem;
import com.gmmapowell.script.sink.Sink;
import com.gmmapowell.script.sink.blogger.PostIndex.BlogEntry;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.blogger.Blogger;
import com.google.api.services.blogger.Blogger.Blogs;
import com.google.api.services.blogger.Blogger.Posts;
import com.google.api.services.blogger.BloggerScopes;
import com.google.api.services.blogger.model.Blog;
import com.google.api.services.blogger.model.Post;
import com.google.api.services.blogger.model.PostList;

public class BloggerSink implements Sink {
	private final String have_internet = System.getenv("HAVE_INTERNET");
	private final boolean CONFIGURE_WITH_INTERNET = have_internet == null || ("true".equals(have_internet));

	public enum Mode {
		START, NORMAL, LIST, BLOCKQUOTE
	}

	private final Place creds;
	private final String blogUrl;
	private final Place postsFile;
	private final PostIndex index;
	private PrintWriter writer;
	private String blogId;
	private Posts posts;
	private StringWriter sw;
	private List<Flow> flows = new ArrayList<>();
	private boolean haveBreak;
	private Place saveContentAs;
	private boolean localOnly;
	private Map<String, String> uploads = new TreeMap<>();

	public BloggerSink(Region root, Place cp, String blogUrl, Place pf, boolean localOnly, Place saveContentAs) throws IOException, GeneralSecurityException {
		this.creds = cp;
		this.blogUrl = blogUrl;
		this.postsFile = pf;
		if (CONFIGURE_WITH_INTERNET)
			this.localOnly = localOnly;
		else
			this.localOnly = true;
		this.saveContentAs = saveContentAs;
		this.index = new PostIndex();
	}
	
	public void prepare() throws Exception {
		index.readFrom(postsFile);
		if (!localOnly) {
			try {
				connect();
			} catch (TokenResponseException ex) {
				// clean token store and try again
				FileUtils.cleanDirectory(tokenStore());
				connect();
			}
			readFromBlogger();
		}
	}

	@Override
	public void flow(Flow flow) {
		flows.add(flow);
	}

	@Override
	public void render() throws IOException {
		for (Flow f : flows) {
//			System.out.println("Flow " + f.name);
			this.sw = new StringWriter();
			writer = new PrintWriter(sw);
//			int sno = 1;
			for (Section s : f.sections) {
//				System.out.println("Section " + (sno++) + ": " + s.format);
				Cursor c = new Cursor(f.name, s);
				String last = "text";
				List<String> cf = new ArrayList<>();
				StyledToken tok;
				while ((tok = c.next()) != null) {
//					System.out.println("  " + tok);
					last = transition(cf, last, tok);
					haveBreak = false;
					figureStyles(cf, tok.styles);
					cf = new ArrayList<>(tok.styles);
					if (tok.it instanceof TextSpanItem)
						writer.print(entitify(((TextSpanItem)tok.it).text));
					else if (tok.it instanceof BreakingSpace) {
//						if (last.equals("blockquote"))
//							writer.print("&nbsp;");
//						else
							writer.print(" ");
					} else if (tok.it instanceof NonBreakingSpace) {
						writer.print("&nbsp;");
					} else if (tok.it instanceof ParaBreak) {
						switch (last) {
						case "bullet":
							last = "needli";
							break;
						case "text":
						case "break":
						case "blockquote":
						case "preformatted":
						case "italic":
						case "bold":
						case "tt":
							writer.print("<br/>");
							break;
						case "h1":
						case "h2":
						case "h3":
						case "h4":
						case "fileinfo":
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
					} else
						throw new NotImplementedException(tok.it.toString());
				}
				transition(cf, last, "text", false);
			}
			writer.close();
			uploads.put(f.name, this.sw.toString());
			if (saveContentAs != null) {
				saveContentAs.store(sw.toString());
			}
		}
	}

	private String transition(List<String> cf, String last, StyledToken tok) {
		if (last.equals("text") && tok.styles.get(0).equals("text") && haveBreak)
			writer.println("<br/>");
		return transition(cf, last, tok.styles.get(0), tok.styles.contains("break"));
	}

	private String transition(List<String> cf, String last, String next, boolean butStartAgain) {
		if (next.equals(last) && !butStartAgain)
			return last;

		if (last.equals("blockquote") || last.equals("preformatted")) {
			writer.println("</span>");
			writer.println("</blockquote>");
		}
		if (last.equals("fileinfo")) {
			writer.print("</p>");
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
			writer.print("  <li>");
		}
		if (next.equals("blockquote") || next.equals("preformatted")) {
			writer.println("<blockquote class='tr_bq'>");
			writer.println("<span style='color: blue; font-family: &quot;courier new&quot;, &quot;courier&quot;, monospace; font-size: x-small; text-wrap: nowrap; overflow-x: scroll;'>");
		}
		if (next.equals("fileinfo")) {
			writer.print("<p style='margin-inline-start:40px; font-family: monospace; font-size: small; font-style:italic'>");
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
		case "superscript":
			return "sup";
		default:
			return sty;
		}
	}

	@Override
	public void upload() throws Exception {
		if (localOnly)
			return;
		for (Entry<String, String> e : uploads.entrySet()) {
			this.upload(e.getKey(), e.getValue());
		}
	}

	private void upload(String title, String content) throws IOException {
		BlogEntry idx = findInPosts(title);
		if (idx != null && idx.isLive) {
			System.out.println("Not uploading " + title + " as it is already live");
			return;
		}
		Post p = new Post();
		p.setTitle(title);
		p.setContent(content);
		// How do you say you don't want it to publish straight away?
//		p.setStatus("DRAFT");
		if (idx == null) {
			System.out.println("Create " + title);
			Post inserted = posts.insert(blogId, p).execute();
			// revert it immediately
			posts.revert(blogId, inserted.getId()).execute();
			index.have(inserted.getId(), "DRAFT", title);
		} else {
			if (localOnly) {
				System.out.println("local mode selected - not uploading");
			} else {
				System.out.println("Upload " + title + " to " + blogId + ":" + idx);
				posts.update(blogId, idx.key, p).execute();
			}
		}
	}

	@Override
	public void showFinal() {
	}

	private BlogEntry findInPosts(String title) {
		return index.find(title);
	}

	private void connect() throws IOException, GeneralSecurityException, TokenResponseException {
		Credential c = getCredential();
		Blogger blogger = new Blogger.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), c)
            .setApplicationName("ScriptFormatter")
			.build();
		Blogs conn = blogger.blogs();
		Blog blog = conn.getByUrl(blogUrl).execute();
		blogId = blog.getId();
		posts = blogger.posts();
	}
	
	private void readFromBlogger() throws IOException, GeneralSecurityException {
		index.appendTo(postsFile);

		String npt = null;
		while (true) {
			PostList list = posts.list(blogId).setStatus(Arrays.asList("draft", "live")).setPageToken(npt).execute();
			for (Post e : list.getItems()) {
				index.have(e.getId(), e.getStatus(), e.getTitle());
			}
			npt = list.getNextPageToken();
			if (npt == null)
				break;
		}
	}

	private Credential getCredential() throws IOException, GeneralSecurityException {
		System.out.println("Getting credential for Blogger");
		GoogleClientSecrets secrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), GeoFSUtils.fileReader(creds));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), secrets, BloggerScopes.all())
                .setDataStoreFactory(new FileDataStoreFactory(tokenStore()))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8807).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	private File tokenStore() {
		return new File(GeoFSUtils.file(creds.region()), "google_scriptformatter_blogger_tokens");

	}
	
	public void finish() throws Exception {
		index.close();
	}
}
