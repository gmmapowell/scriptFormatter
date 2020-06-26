package com.gmmapowell.script.sink.blogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.gmmapowell.script.elements.Break;
import com.gmmapowell.script.elements.Block;
import com.gmmapowell.script.elements.Group;
import com.gmmapowell.script.elements.Span;
import com.gmmapowell.script.elements.block.TextBlock;
import com.gmmapowell.script.sink.Sink;
import com.google.api.client.auth.oauth2.Credential;
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
	public enum Mode {
		START, NORMAL, LIST, BLOCKQUOTE
	}

	private final File creds;
	private final String blogUrl;
	private final File postsFile;
	private final PostIndex index;
	private String title;
	private PrintWriter writer;
	private Mode mode = Mode.START;
	private String blogId;
	private Posts posts;
	private StringWriter sw;

	public BloggerSink(File root, File creds, String blogUrl, File posts) throws IOException, GeneralSecurityException {
		this.creds = creds;
		this.blogUrl = blogUrl;
		this.postsFile = posts;
		index = readPosts();
		connect();
		readFromBlogger();
	}

	@Override
	public void title(String title) throws IOException {
		this.title = title;
		this.sw = new StringWriter();
		writer = new PrintWriter(sw);
	}

	@Override
	public void block(Block block) throws IOException {
		if (writeBlock(block))
			writer.println("<br/>");
	}

	@Override
	public void brk(Break ad) {
	}

	private boolean writeBlock(Block block) {
		if (block instanceof TextBlock)
			return writeTextBlock((TextBlock) block);
		else if (block instanceof Group)
			return writeGroup((Group)block);
		else
			throw new RuntimeException("What is " + block.getClass() + "?");
	}

	private boolean writeTextBlock(TextBlock block) {
		boolean wantBr = true;
		switch (block.getStyle()) {
		case "text": {
			if (mode == Mode.LIST)
				writer.println("</ul>");
			else if (mode == Mode.BLOCKQUOTE) {
				writer.println("</span>");
				writer.println("</blockquote>");
			} else if (mode == Mode.NORMAL)
				writer.println("<br/>");
			mode = Mode.NORMAL;
			break;
		}
		case "bullet": {
			if (mode != Mode.LIST)
				writer.println("<ul>");
			mode = Mode.LIST;
			writer.print("  <li>");
			break;
		}
		case "blockquote": {
			if (mode == Mode.LIST) {
				writer.println("</ul>");
			} else if (mode != Mode.BLOCKQUOTE) {
				writer.println("<blockquote class='tr_bq'>");
				writer.println("<span style='color: blue; font-family: &quot;courier new&quot;, &quot;courier&quot;, monospace; font-size: x-small;'>");
				mode = Mode.BLOCKQUOTE;
			}
			break;
		}
		default: {
			if (mode == Mode.LIST)
				writer.println("</ul>");
			if (block.getStyle().startsWith("h")) {
				wantBr = false;
				writer.print("<" + block.getStyle() + ">");
				mode = Mode.START;
			} else {
				System.out.println(block.getStyle());
				mode = Mode.NORMAL;
			}
			break;
		}
		}
		List<String> cf = new ArrayList<>();
		for (Span s : block) {
			List<String> styles = s.getStyles();
			if (styles.size() == 1) {
				switch(styles.get(0)) {
				case "link": {
					writer.print("<a href='" + s.getText() + "'>");
					continue;
				}
				case "endlink": {
					writer.print("</a>");
					continue;
				}
				}
			}
			drawDownTo(cf, styles.size());
			for (int i=0;i<styles.size();i++) {
				if (cf.size() > i && cf.get(i).equals(styles.get(i)))
					continue;
				else if (cf.size() > i) {
					drawDownTo(cf, i);
				}
				writer.print("<" + styles.get(i) + ">");
				cf.add(styles.get(i));
			}
			writer.print(entitify(s.getText()));
		}
		drawDownTo(cf, 0);
		if (!wantBr)
			writer.println("</" + block.getStyle() + ">");
		return wantBr;
	}

	private String entitify(String text) {
		StringBuilder sb = new StringBuilder(text);
		// it is sort of easier to work backwards
		for (int i=sb.length()-1;i>=0;i--) {
			switch (sb.charAt(i)) {
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

	private void drawDownTo(List<String> cf, int to) {
		while (cf.size() > to) {
			writer.print("</" + cf.remove(cf.size()-1) + ">");
		}
	}

	private boolean writeGroup(Group block) {
		return false;
	}

	@Override
	public void fileEnd() throws Exception {
		switch (mode) {
		case LIST:
			writer.println("</ul>");
			break;
		case BLOCKQUOTE:
			writer.println("</span>");
			writer.println("</blockquote>");
			break;
		default:
			break;
		}
		writer.close();

		if (title == null) {
			System.out.println("Cannot upload without a title");
			return;
		}
		String idx = findInPosts();
		Post p = new Post();
		p.setTitle(title);
		p.setContent(sw.toString());
		// How do you say you don't want it to publish straight away?
//		p.setStatus("DRAFT");
		if (idx == null) {
			System.out.println("Create " + title);
			Post inserted = posts.insert(blogId, p).execute();
			// revert it immediately
			posts.revert(blogId, inserted.getId()).execute();
			index.have(inserted.getId(), "DRAFT", title);
		} else {
			System.out.println("Upload to " + blogId + ":" + idx);
			posts.update(blogId, idx, p).execute();
		}
	}

	@Override
	public void close() throws IOException {
		index.close();
	}

	@Override
	public void showFinal() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void upload() throws Exception {
	}

	private PostIndex readPosts() throws IOException {
		PostIndex index = new PostIndex();
		try (FileReader fr = new FileReader(postsFile)) {
			index.readFrom(fr);
		} catch (FileNotFoundException ex) {
			System.out.println(postsFile + " not found; creating");
		}
		return index;
	}

	private String findInPosts() {
		return index.find(title);
	}

	private void connect() throws IOException, GeneralSecurityException {
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
		FileWriter fw = new FileWriter(postsFile, true);
		index.appendTo(fw);

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
		GoogleClientSecrets secrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new FileReader(creds));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), secrets, BloggerScopes.all())
                .setDataStoreFactory(new FileDataStoreFactory(new File(creds.getParentFile(), "google_scriptformatter_blogger_tokens")))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}
}
