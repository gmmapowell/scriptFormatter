package com.gmmapowell.script.modules.doc;

import java.io.DataInputStream;
import java.io.IOException;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.tools.DumpDecoder;

public class DocModule {

	public static final byte ID = 1;
	public static final byte ANCHOR_OP = 0x01;
	public static final byte LINK_FROM_TOC = 0x02;
	
	public static void decode(DumpDecoder decoder, DataInputStream dis, int si) throws IOException {
		byte c = dis.readByte();
		switch (c) {
		case ANCHOR_OP: {
			decoder.showText("anchor");
			break;
		}
		case LINK_FROM_TOC: {
			decoder.showText("link from toc");
			break;
		}
		default: {
			throw new NotImplementedException();
		}
		}
	}

}
