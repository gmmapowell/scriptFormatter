package com.gmmapowell.script.modules.movie;

import java.io.DataInputStream;
import java.io.IOException;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.tools.DumpDecoder;

public class MovieModule {
	public static final byte ID = 2;
	public static final byte BOXY_AD_BREAK = 0x01;
	
	public static void decode(DumpDecoder decoder, DataInputStream dis, int si) throws IOException {
		byte c = dis.readByte();
		switch (c) {
		case BOXY_AD_BREAK: {
			decoder.showText("boxy ad break");
			break;
		}
		default: {
			throw new NotImplementedException();
		}
		}
	}

}
