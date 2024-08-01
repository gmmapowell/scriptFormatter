package test.geofs.lfs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.zinutils.utils.FileUtils;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.lfs.LocalFileSystem;
import com.gmmapowell.geofs.listeners.LineListener;
import com.gmmapowell.geofs.utils.GeoFSUtils;
import com.google.common.io.Files;

// This is an "integration" test and as such, it depends on external realities.
// I have tried to write it in a way that works on all Linux systems.
// Obviously it would be possible to put more effort in to analyze what we have and what exists and adjust.

public class BasicLFS {
	@Rule public JUnitRuleMockery context = new JUnitRuleMockery();

	@Test
	public void testWeCanFindATmpFileWeCreate() throws IOException {
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line("hello, world!");
			oneOf(lsnr).complete();
		}});
		
		File tf = File.createTempFile("lfs", ".txt");
		FileUtils.writeFile(tf, "hello, world!\n");
		List<String> elts = FileUtils.pathElements(tf);
		LocalFileSystem lfs = new LocalFileSystem(null);
		Region r = lfs.root();
		for (int i=0;i<elts.size()-1;i++) {
			String s = elts.get(i);
			if (s == null || s.length() == 0)
				continue;
			r = r.subregion(s);
		}
		Place place = r.place(elts.get(elts.size()-1));
		place.lines(lsnr);
		tf.delete();
	}

	@Test
	public void testWeCanFindATmpFileWeCreateUsingPlacePath() throws IOException {
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line("hello, world!");
			oneOf(lsnr).complete();
		}});
		
		File tf = File.createTempFile("lfs", ".txt");
		FileUtils.writeFile(tf, "hello, world!\n");
		LocalFileSystem lfs = new LocalFileSystem(null);
		Place place = lfs.placePath(tf.getPath());
		place.lines(lsnr);
		tf.delete();
	}

	@Test
	public void testWeCanFindATmpFileWeCreateFromARegionUsingPlacePath() throws IOException {
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line("hello, world!");
			oneOf(lsnr).complete();
		}});
		
		File tf = File.createTempFile("lfs", ".txt");
		FileUtils.writeFile(tf, "hello, world!\n");
		LocalFileSystem lfs = new LocalFileSystem(null);
		Place place = lfs.root().placePath(tf.getPath().substring(1));
		place.lines(lsnr);
		tf.delete();
	}

	@Test
	public void testWeCanFindATmpDirWeCreateUsingRegionPath() throws IOException {
		File tf = Files.createTempDir();
		LocalFileSystem lfs = new LocalFileSystem(null);
		Region region = lfs.regionPath(tf.getPath());
		assertNotNull(region);
		tf.delete();
	}

	@Test
	public void testWeCanWriteAFile() throws IOException {
		File tf = File.createTempFile("lfsw", ",txt");
		LocalFileSystem lfs = new LocalFileSystem(null);
		Place place = lfs.placePath(tf.getPath());
		assertNotNull(place);
		Writer pw = place.writer();
		pw.write("hello world\n");
		pw.close();
		assertEquals("hello world\n", FileUtils.readFile(tf));
		tf.delete();
	}

	@Test
	public void testWeCanObtainAFileFromAnLFSPlace() throws IOException {
		File tf = File.createTempFile("lfsf", ",txt");
		LocalFileSystem lfs = new LocalFileSystem(null);
		Place place = lfs.placePath(tf.getPath());
		assertNotNull(place);
		File pf = GeoFSUtils.file(place);
		assertEquals(tf, pf);
		tf.delete();
	}

	@Test
	public void testWeCanFindATmpDirWeCreateFromARegionUsingRegionPath() throws IOException {
		File tf = Files.createTempDir();
		LocalFileSystem lfs = new LocalFileSystem(null);
		Region region = lfs.root().regionPath(tf.getPath().substring(1));
		assertNotNull(region);
		tf.delete();
	}
	
	@Test
	public void tildeIsUserHome() throws IOException {
		LocalFileSystem lfs = new LocalFileSystem(null);
		String userDir = System.getProperty("user.home");
		Region r = lfs.root("~");
		assertEquals(userDir, GeoFSUtils.file(r).getPath());
	}
}
