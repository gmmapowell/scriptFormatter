package com.gmmapowell.script.modules.emailquoter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;

public class MailPara {
	private final Region root;
	private final Map<String, List<SnapLine>> snapsInfo = new TreeMap<>();
	
	public MailPara(Region root, Place snapsTxt) throws FileNotFoundException, IOException {
		this.root = root;
		parseSnaps(snapsTxt);
	}

	private void parseSnaps(Place snapsTxt) throws FileNotFoundException, IOException {
		AtomicReference<List<SnapLine>> curr = new AtomicReference<>(null);
		snapsTxt.lines(s -> {
			s = s.trim();
			if (s.length() > 2 && s.charAt(0) == '[') {
				String tag = s.substring(1, s.length()-1);
				int idx = tag.indexOf(' ');
				if (idx != -1)
					tag = tag.substring(0, idx);
				ArrayList<SnapLine> nl = new ArrayList<>();
				curr.set(nl);
				snapsInfo.put(tag, nl);
			} else {
				if (s.length() == 0)
					curr.get().add(new SnapPara());
				else if (s.length() == 1) 
					curr.get().add(new SnapUser(snapper(s)));
				else
					curr.get().add(new SnapText(s));
			}
		});
	}

	private String snapper(String s) {
		switch (s) {
		case "M": return "Maggy:";
		case "G": return "Gareth:";
		case "E": return "Ellen:";
		case "Z": return "Zach:";
		case "*": return "Caption:";
		default: throw new RuntimeException("who is " + s + "?");
		}
	}
	
	public void quoteEmail(Citation c, Consumer<String> lsnr) throws IOException {
		Region region = findTextFileFor(root, c.file);
		if (region == null) 
			throw new RuntimeException("did not find message " + c.file);
//		System.out.println("You are thinking of " + f);
		showLines(region, c.first, c.last, lsnr);
	}

	public void showSnaps(SnapList snaps, Consumer<SnapLine> lsnr) {
		for (String k : snaps.list) {
			List<SnapLine> text = snapsInfo.get(k);
			if (text == null)
				throw new RuntimeException("there is no snap entry for key " + k);
			showSnap(lsnr, k, text);
		}
	}

	private Region findTextFileFor(Region indir, String called) {
		// TODO: there should be an explicit "findRegion" or "filterRegion"
		AtomicReference<Region> found = new AtomicReference<>();
		indir.regions(r -> {
			if (found.get() != null)
				return;
			if (r.name().startsWith("msg-" + called) && r.hasPlace("text")) {
				found.set(r);
			} else {
				Region maybe = findTextFileFor(r, called);
				if (maybe != null)
					found.set(maybe);
			}
		});
		return found.get();
	}

	private void showLines(Region region, int first, int last, Consumer<String> lsnr) throws FileNotFoundException, IOException {
		Place place = region.place("text");
		lsnr.accept("[" + asDate(region.name()) + "]");
		lsnr.accept("");
		place.lines((n,l) -> {
			if (n < first || n > last)
				return;
			lsnr.accept(l);
		});
	}

	private String asDate(String name) {
		String ret = name.replace("msg-", "").substring(0, 12);
		return ret.substring(0, 4) + "-" + ret.substring(4,6) + "-" + ret.substring(6,8) + "_" + ret.substring(8,10) + ":" + ret.substring(10,12);
	}

	private void showSnap(Consumer<SnapLine> lsnr, String ref, List<SnapLine> text) {
		lsnr.accept(new SnapRef(ref));
		for (SnapLine s : text) {
			lsnr.accept(s);
		}
	}
}
