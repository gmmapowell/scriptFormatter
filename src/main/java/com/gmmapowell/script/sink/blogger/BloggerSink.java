package com.gmmapowell.script.sink.blogger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map.Entry;

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
import com.google.api.services.blogger.BloggerRequestInitializer;
import com.google.api.services.blogger.BloggerScopes;
import com.google.api.services.blogger.model.Blog;
import com.google.api.services.blogger.model.Post;
import com.google.api.services.blogger.model.PostList;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class BloggerSink implements Sink {
	private File creds;

	public BloggerSink(File root, File creds) throws IOException, GeneralSecurityException {
		this.creds = creds;
		Credential c = getCredential();
		Blogger blogger = new Blogger.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), c)
            .setApplicationName("ScriptFormatter")
            .setBloggerRequestInitializer(new BloggerRequestInitializer())
			.build();
		Blogs conn = blogger.blogs();
		Blog blog = conn.getByUrl("https://ignorancemaybestrength.blogspot.com/").execute();
		Posts posts = blogger.posts();
		String npt = null;
		while (true) {
			PostList list = posts.list(blog.getId()).setPageToken(npt).execute();
			System.out.println("#items = " + list);
			for (Post e : list.getItems()) {
				System.out.println(e);
			}
			npt = list.getNextPageToken();
			System.out.println(npt);
			if (npt == null)
				break;
		}
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
	public void upload() throws JSchException, SftpException {
		// TODO Auto-generated method stub

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
