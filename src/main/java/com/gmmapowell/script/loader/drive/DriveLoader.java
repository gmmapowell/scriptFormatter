package com.gmmapowell.script.loader.drive;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;

import com.gmmapowell.script.loader.Loader;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;

public class DriveLoader implements Loader {
	private final File creds;
	private final String index;

	public DriveLoader(String creds, String index) {
		this.creds = new File(creds);
		this.index = index;
	}

	@Override
	public void updateIndex() throws IOException, GeneralSecurityException {
		Credential cred = getCredential();
	}

	private Credential getCredential() throws IOException, GeneralSecurityException {
		GoogleClientSecrets secrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new FileReader(creds));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), secrets, DriveScopes.all())
                .setDataStoreFactory(new FileDataStoreFactory(new File(creds.getParentFile(), "google_scriptformatter_tokens")))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

}
