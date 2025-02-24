package com.gmmapowell.script.modules.article;

import java.io.DataInputStream;
import java.io.IOException;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.tools.DumpDecoder;

public class ArticleModule {
	public static final byte ID = 3;
	public static final byte SAVE_AS = 0x01;
	
	public static void decode(DumpDecoder decoder, DataInputStream dis, int si) throws IOException {
		byte c = dis.readByte();
		switch (c) {
		case SAVE_AS: {
			String name = dis.readUTF();
			decoder.showText("save as: '" + name + "'");
			break;
		}
		default: {
			throw new NotImplementedException();
		}
		}
	}
}
