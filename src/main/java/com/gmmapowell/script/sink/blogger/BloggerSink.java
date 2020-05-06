package com.gmmapowell.script.sink.blogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import com.gmmapowell.script.elements.Block;
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
	private final File creds;
	private final String blogUrl;
	private final File posts;
	private final PostIndex index;
	private String title;

	public BloggerSink(File root, File creds, String blogUrl, File posts) throws IOException {
		this.creds = creds;
		this.blogUrl = blogUrl;
		this.posts = posts;
		index = readPosts();
	}

	@Override
	public void title(String title) throws IOException {
		this.title = title;
	}

	@Override
	public void block(Block block) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void showFinal() {
		// TODO Auto-generated method stub

	}

	@Override
	public void upload() throws Exception {
		if (title == null) {
			System.out.println("Cannot upload without a title");
			return;
		}
		String idx = findInPosts();
		if (idx == null) {
			readAll();
			idx = findInPosts();
		}
		if (idx == null) {
			System.out.println("Create " + title);
		}
	}

	private PostIndex readPosts() throws IOException {
		PostIndex index = new PostIndex();
		try (FileReader fr = new FileReader(posts)) {
			index.readFrom(fr);
		} catch (FileNotFoundException ex) {
			System.out.println(posts + " not found; creating");
		}
		return index;
	}

	private String findInPosts() {
		return index.find(title);
	}

	private void readAll() throws IOException, GeneralSecurityException {
		try (FileWriter fw = new FileWriter(posts, true)) {
			index.appendTo(fw);
	
			Credential c = getCredential();
			Blogger blogger = new Blogger.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), c)
	            .setApplicationName("ScriptFormatter")
				.build();
			Blogs conn = blogger.blogs();
			Blog blog = conn.getByUrl(blogUrl).execute();
			Posts posts = blogger.posts();
			String npt = null;
			while (true) {
				PostList list = posts.list(blog.getId()).setStatus(Arrays.asList("draft", "live")).setPageToken(npt).execute();
				for (Post e : list.getItems()) {
					index.have(e.getId(), e.getStatus(), e.getTitle());
				}
				npt = list.getNextPageToken();
				if (npt == null)
					break;
			}
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
