package com.gmmapowell.script.modules.processors.blog.diagram;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ziniki.server.TDAServer;
import org.ziniki.server.di.DehydratedHandler;
import org.ziniki.server.di.Instantiatable;
import org.ziniki.server.di.Instantiator;
import org.ziniki.server.grizzly.GrizzlyTDAServer;
import org.ziniki.server.path.SimplePathTree;
import org.ziniki.servlet.basic.FileResponseProcessor;
import org.ziniki.servlet.tda.RequestProcessor;
import org.zinutils.exceptions.CantHappenException;
import org.zinutils.exceptions.WrappedException;
import org.zinutils.utils.FileUtils;

import com.gmmapowell.script.flow.ImageOp;
import com.gmmapowell.script.modules.processors.blog.UploadAll;
import com.gmmapowell.script.processor.configured.ConfiguredState;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class DiagramState {
	private UploadAll uploader;
	private TDAServer server = null;
	private List<String> data = null;
	private boolean args = false;
	private Map<String, String> argValues = null;
	private ConfiguredState state;
	
	public void provideUploaders(UploadAll uploader) {
		this.uploader = uploader;
	}
	
	public boolean isActive() {
		return data != null;
	}
	
	public void start(ConfiguredState state, String webPath) {
		this.state = state;
		ensureServer(webPath);
		argValues = new TreeMap<>();
		data = new ArrayList<>();
		args = true;
		System.out.println("start diagram");
	}

	public void add(String s) {
		if (s.trim().length() == 0) {
			args = false;
			System.out.println();
		} else if (args) {
			System.out.println("diagram arg");
			int idx = s.indexOf("=");
			if (idx == -1)
				throw new CantHappenException("invalid diagram arg: " + s);
			String an = s.substring(0, idx).trim();
			String av = s.substring(idx+1).trim();
			this.argValues.put(an, av);
		} else {
			System.out.println("diagram data");
			data.add(s);
		}
	}
	
	public void draw() {
		System.out.println("server is on port " + server.getPort());
		try (Playwright pw = Playwright.create()) {
			BrowserType chromium = pw.chromium();
			Browser browser = chromium.launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(50));
			Page page = browser.newPage();
			page.navigate("http://localhost:" + server.getPort());
			page.locator(".text-input").fill(makeInput());
			page.locator(".toolbar-update").click();
			if (!argValues.containsKey("tab")) {
				throw new CantHappenException("diagram must have property 'tab'");
			}
			if (!argValues.containsKey("file")) {
				throw new CantHappenException("diagram must have property 'file'");
			}
			page.getByText(argValues.get("tab"), new Page.GetByTextOptions().setExact(true)).click();
			byte[] bs = page.locator(".diagram-tab.tab-body.selected-tab .diagram").screenshot();
			System.out.println("have screenshot: " + bs.length);
			// TODO: make this a true tmp file
			File tmpFile = new File("/Users/gareth/tmp/file.png");
			FileUtils.writeFile(tmpFile, bs);
			browser.close();
			
			state.newPara("text");
			state.newSpan();
			// Blogger API really doesn't support uploading images, so let's send them to DH
			String res = uploader.upload(tmpFile, argValues.get("file"));
			if (res == null) {
				throw new CantHappenException("no result image location was returned");
			}
			// TODO: delete the tmp file when it is a tmp file
			state.op(new ImageOp(res));
			state.endPara();
			state.observeBlanks();
		} catch (Exception e) {
			throw WrappedException.wrap(e);
		}
		// much more to do here ...
		data = null;
	}
	
	private String makeInput() {
		StringBuilder ret = new StringBuilder();
		for (String s : data) {
			ret.append(s);
			ret.append("\n");
		}
		return ret.toString();
	}

	private void ensureServer(String webPath) {
		if (server != null)
			return;
		server = new GrizzlyTDAServer(0);
		SimplePathTree<RequestProcessor> pt = new SimplePathTree<RequestProcessor>();
		Map<String, Object> refs = new TreeMap<>();
		defineRefs(refs, new File(webPath));
		configureStaticPaths(pt, refs);
		server.httpMappingTree(pt);
		try {
			server.start();
		} catch (IOException e) {
			throw WrappedException.wrap(e);
		}
	}
	
	private void defineRefs(Map<String, Object> refs, File webroot) {
		refs.put("index", new File(webroot, "index.html"));
		refs.put("js", new File(webroot, "js"));
		refs.put("css", new File(webroot, "css"));
	}

	private void configureStaticPaths(SimplePathTree<RequestProcessor> pt, Map<String, Object> refs) {
		{
			Map<String, Object> opts = new TreeMap<>();
			opts.put("class", FileResponseProcessor.class.getName());
			opts.put("under", "_index_");
			opts.put("type", "text/html");
			Instantiatable inst = new Instantiator("index", opts);
		    DehydratedHandler<RequestProcessor> web = new DehydratedHandler<>(inst, refs);
			pt.add("/", web);
		}
		{
			Map<String, Object> opts = new TreeMap<>();
			opts.put("class", FileResponseProcessor.class.getName());
			opts.put("under", "_js_");
			opts.put("type", "text/javascript");
			Instantiatable inst = new Instantiator("jsfiles", opts);
		    DehydratedHandler<RequestProcessor> js = new DehydratedHandler<>(inst, refs);
			pt.add("/js/*", js);
		}
		{
			Map<String, Object> opts = new TreeMap<>();
			opts.put("class", FileResponseProcessor.class.getName());
			opts.put("under", "_css_");
			opts.put("type", "text/css");
			Instantiatable inst = new Instantiator("cssfiles", opts);
		    DehydratedHandler<RequestProcessor> js = new DehydratedHandler<>(inst, refs);
			pt.add("/css/{path}", js);
		}
	}
}
