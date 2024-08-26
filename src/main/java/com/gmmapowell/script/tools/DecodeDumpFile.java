package com.gmmapowell.script.tools;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

import org.zinutils.exceptions.NotImplementedException;
import org.zinutils.utils.Hollerith;
import org.zinutils.utils.HollerithFormat;
import org.zinutils.utils.Justification;
import org.zinutils.utils.StringUtil;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.lfs.LocalFileSystem;
import com.gmmapowell.script.flow.FlowStandard;
import com.gmmapowell.script.modules.article.ArticleModule;
import com.gmmapowell.script.modules.doc.DocModule;
import com.gmmapowell.script.modules.manual.ManualModule;
import com.gmmapowell.script.modules.movie.MovieModule;
import com.gmmapowell.script.modules.presenter.PresenterModule;

public class DecodeDumpFile implements DumpDecoder {

	public static void main(String[] args) throws IOException {
		new DecodeDumpFile(args[0]).dump();
	}

	private final DataInputStream dis;
	private int lineNo = 1;
	private ByteBufCaptureStream bytebuf;
	private HollerithFormat fmt;
	
	public DecodeDumpFile(String path) {
		World w = new LocalFileSystem(null);
		Place f = w.placePath(path);
		this.bytebuf = new ByteBufCaptureStream(f.input());
		this.dis = new DataInputStream(bytebuf);
		this.fmt = new HollerithFormat();
		this.fmt.addField("lineNo").setWidth(6).setJustification(Justification.RIGHT);
		this.fmt.addPadding(1);
		this.fmt.addField("offset").setWidth(6).setJustification(Justification.RIGHT);
		this.fmt.addPadding(1);
		this.fmt.addField("bytes").setWidth(18).setJustification(Justification.LEFT);
		this.fmt.addPadding(1);
		this.fmt.addField("message");
	}

	private void dump() throws IOException {
		loadModules();
		while (showFlow())
			;
	}

	private void loadModules() throws IOException {
		short nmod = dis.readShort();
		showText("#modules: " + nmod);
	}
	
	private boolean showFlow() throws IOException {
		try {
			boolean b = dis.readBoolean();
			String name = dis.readUTF();
			// NOTE: "main" here should read "callback" but I can't change it until I have finished with regression testing ...
			showText("flow '" + name + "'" + (b ? " is main":""));
		} catch (EOFException ex) {
			// no more flows ...
			return false;
		}
		
		short ns = dis.readShort();
		showText("#sections: " + ns);
		for (int i=0;i<ns;i++) {
			showSection(i);
		}
		return true;
	}

	private void showSection(int sno) throws IOException {
		String f = dis.readUTF();
		showText("section " + sno + ": format '" + f + "'");
		
		short np = dis.readShort();
		showText("#paras: " + np);
		for (int i=0;i<np;i++) {
			showPara(i);
		}
	}

	private void showPara(int pno) throws IOException {
		short nf = dis.readShort();
		showText("para " + pno + ": " + nf + " formats");
		for (int i=0;i<nf;i++) {
			String fmt = dis.readUTF();
			showText("fmt " + i + ": '" + fmt + "'");
		}
		
		short ns = dis.readShort();
		showText("para " + pno + " has " + ns + " spans");
		for (int i=0;i<ns;i++) {
			showSpan(i);
		}
	}

	private void showSpan(int sno) throws IOException {
		short nf = dis.readShort();
		showText("span " + sno + ": " + nf + " formats");
		for (int i=0;i<nf;i++) {
			String fmt = dis.readUTF();
			showText("fmt " + i + ": '" + fmt + "'");
		}
		
		short ni = dis.readShort();
		showText("span " + sno + " has " + ni + " items");
		for (int i=0;i<ni;i++) {
			showSpanItem(i);
		}
	}

	private void showSpanItem(int si) throws IOException {
		byte module = dis.readByte();
		if (module == 0) {
			decodeBuiltin(si);
		} else {
			decodeModule(si, module);
		}
	}

	private void decodeBuiltin(int si) throws IOException {
		byte op = dis.readByte();
		switch (op) {
		case FlowStandard.TEXT: {
			String tx = dis.readUTF();
			showText("item " + si + " is text '" + tx + "'");
			break;
		}
		case FlowStandard.BREAKING_SPACE: {
			showText("item " + si + " is a breaking space");
			break;
		}
		
		case FlowStandard.LINK_OP: {
			showText("item " + si + " is a link");
			String lk = dis.readUTF();
			showText("  link: '" + lk + "'");
			String tx = dis.readUTF();
			showText("  text: '" + tx + "'");
			break;
		}
		
		case FlowStandard.YIELD: {
			showText("item " + si + " yields");
			String ytf = dis.readUTF();
			showText("  to flow: '" + ytf + "'");
			break;
		}

		case FlowStandard.RELEASE: {
			showText("item " + si + " releases");
			String rf = dis.readUTF();
			showText("  flow: '" + rf + "'");
			break;
		}
		
		case FlowStandard.SYNC_AFTER: {
			showText("item " + si + " syncs");
			String af = dis.readUTF();
			showText("  after flow: '" + af + "'");
			break;
		}
		
		case FlowStandard.IMAGE_OP: {
			String uri = dis.readUTF();
			showText("item " + si + " is an image: '" + uri + "'");
			break;
		}
		
		case FlowStandard.NESTED: {
			showSpan(0);
			break;
		}
		default: {
			showText("item " + si + " is built in but INVALID (" + op + ")");
			break;
		}
		}
	}

	private void decodeModule(int si, byte module) throws IOException {
		// TODO: it should go without saying that ultimately this needs to be configurable :-)
		
		switch (module) {
		case DocModule.ID: {
			DocModule.decode(this, dis, si);
			break;
		}
		case MovieModule.ID: {
			ManualModule.decode(this, dis, si);
			break;
		}
		case ArticleModule.ID: {
			ManualModule.decode(this, dis, si);
			break;
		}
		case ManualModule.ID: {
			ManualModule.decode(this, dis, si);
			break;
		}
		case PresenterModule.ID: {
			PresenterModule.decode(this, dis, si);
			break;
		}
		default: {
			throw new NotImplementedException("module id " + module);
		}
		}
	}

	public void showText(String msg) {
		Hollerith h = new Hollerith(fmt);
		h.set("lineNo", StringUtil.digits(lineNo, 6));
		h.set("offset", StringUtil.hex(bytebuf.offset() & 0xffffff, 6));
		h.set("bytes", formatBytes(bytebuf.buf()));
		h.set("message", msg);
		System.out.println(h.format());
		lineNo++;
	}

	private String formatBytes(byte[] buf) {
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<buf.length && i<6;i++) {
			if (i > 0)
				sb.append(" ");
			sb.append(StringUtil.hex(buf[i] & 0xff, 2));
		}
		if (buf.length > 6)
			sb.append("+");
		return sb.toString();
	}
}
