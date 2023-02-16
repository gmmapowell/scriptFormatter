package com.gmmapowell.script.processor.prose;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.regex.Pattern;

import org.zinutils.exceptions.WrappedException;
import org.zinutils.system.ProcessOutReader;

public class GitShowProcessor extends Thread implements ProcessOutReader {
	private final Pattern filespec;
	private LineNumberReader input;

	public GitShowProcessor(String filespec) {
		this.filespec = Pattern.compile(filespec);
	}
	
	@Override
	public void echoStream(boolean doEcho) {
		
	}

	@Override
	public void read(InputStream inputStream) {
		input = new LineNumberReader(new InputStreamReader(inputStream));		
	}

	@Override
	public void run() {
		try {
			boolean copying = false;
			String s;
			while ((s = input.readLine()) != null) {
				if (s.startsWith("+++")) {
					copying = filespec.matcher(s).find();
					continue;
				}
				if (s.startsWith("diff --git ") || s.startsWith("\\ No newline at end of file")) {
					copying = false;
					continue;
				}
				if (copying) {
					if (s.startsWith("-") || (s.startsWith("@@") && s.endsWith("@@")))
						continue;
					s = s.substring(1).replace("\t", "  ");
					System.out.println(input.getLineNumber() + " >>> " + s);
				}
			}
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}
}
