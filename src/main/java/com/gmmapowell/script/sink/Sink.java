package com.gmmapowell.script.sink;

import java.io.IOException;

import com.gmmapowell.script.elements.Block;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public interface Sink {
	void block(Block block) throws IOException;
	void close() throws IOException;
	void showFinal();
	void upload() throws JSchException, SftpException;
}
