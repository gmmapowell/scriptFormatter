package com.gmmapowell.script.modules.manual;

import java.io.DataInputStream;
import java.io.IOException;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.tools.DumpDecoder;

public class ManualModule {

	public static final byte ID = 4;
	public static final byte COMMENTARY_BREAK = 0x01;
	
	public static void decode(DumpDecoder decoder, DataInputStream dis, int si) throws IOException {
		byte c = dis.readByte();
		switch (c) {
		case COMMENTARY_BREAK: {
			decoder.showText("commentary break");
			break;
		}
		default: {
			throw new NotImplementedException();
		}
		}
	}

}
