package com.gmmapowell.script.sink.blogger;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.lfs.LocalFileSystem;
import com.gmmapowell.geofs.simple.SimpleUniverse;
import com.gmmapowell.geofs.utils.GeoFSUtils;
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

public class CollectBlogs {
	private String blogUrl = "https://ignorancemaybestrength.blogspot.com/";
	private String blogId;
	private Posts posts;
	private Universe universe = new SimpleUniverse();
	private World lfs = new LocalFileSystem(universe);
	private final Place creds = GeoFSUtils.placePath(lfs, null, "~/.ssh/google_scriptformatter_creds.json");
	private final Region current = GeoFSUtils.regionPath(lfs, null, "~/Projects/CreativeWriting/Blogs/current");

	public static void main(String[] args) throws IOException, GeneralSecurityException {
		CollectBlogs collector = new CollectBlogs();
		collector.connect();
		collector.readFromBlogger();
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
		String npt = null;
		while (true) {
			PostList list = posts.list(blogId).setStatus(Arrays.asList("draft", "live")).setPageToken(npt).execute();
			for (Post e : list.getItems()) {
				if (!"LIVE".equals(e.getStatus()))
					continue;
				System.out.println(e.getId() + " " + e.getStatus() + " " + e.getTitle());
				Place x = current.ensurePlace(e.getTitle());
				System.out.println("save to " + x);
				x.store(e.getContent());
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
}
