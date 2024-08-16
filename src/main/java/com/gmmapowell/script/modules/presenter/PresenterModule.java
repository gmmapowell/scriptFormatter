package com.gmmapowell.script.modules.presenter;

import java.io.DataInputStream;
import java.io.IOException;

import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.tools.DumpDecoder;

public class PresenterModule {

	public static final byte ID = 5;
	public static final byte SLIDE_FORMAT_OP = 0x01;
	public static final byte ASPECT_OP = 0x02;
	public static final byte BG_IMAGE_OP = 0x03;
	public static final byte BG_COLOR_OP = 0x04;
	public static final byte FIELD_OPTION_OP = 0x11;
	public static final byte FIELD_OPTION_INNER_OP = 0x12;
	public static final byte NEW_STEP = 0x21;
	
	public static void decode(DumpDecoder decoder, DataInputStream dis, int si) throws IOException {
		byte c = dis.readByte();
		switch (c) {
		case SLIDE_FORMAT_OP: {
			decoder.showText("format");
			String fmt = dis.readUTF();
			decoder.showText("  is: '" + fmt + "'");
			break;
		}
		case ASPECT_OP: {
			decoder.showText("aspect op");
			float x = dis.readFloat();
			float y = dis.readFloat();
			decoder.showText("  " + x + ", " + y);
			break;
		}
		case BG_IMAGE_OP: {
			decoder.showText("background image");
			String img = dis.readUTF();
			decoder.showText("  use file: '" + img + "'");
			break;
		}
		case BG_COLOR_OP: {
			decoder.showText("background color");
			String col = dis.readUTF();
			decoder.showText("  use color: '" + col + "'");
			break;
		}
		case FIELD_OPTION_OP: {
			decoder.showText("field option");
			String opt = dis.readUTF();
			decoder.showText("  option is: '" + opt + "'");
			String val = dis.readUTF();
			decoder.showText("     <- '" + val + "'");
			break;
		}
		case FIELD_OPTION_INNER_OP: {
			decoder.showText("field inner option");
			String opt = dis.readUTF();
			decoder.showText("  option is: '" + opt + "'");
			String val = dis.readUTF();
			decoder.showText("     <- '" + val + "'");
			break;
		}
		case NEW_STEP: {
			decoder.showText("next step");
			break;
		}
		default: {
			throw new NotImplementedException("code: " + c);
		}
		}
	}

}
