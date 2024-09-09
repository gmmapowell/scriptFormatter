package test.geofs.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.zinutils.utils.FileUtils;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.git.GitWorld;
import com.gmmapowell.geofs.lfs.LFSPlace;
import com.gmmapowell.geofs.lfs.LocalFileSystem;
import com.gmmapowell.geofs.listeners.LineListener;
import com.gmmapowell.geofs.listeners.PlaceListener;
import com.gmmapowell.geofs.listeners.RegionListener;
import com.gmmapowell.geofs.simple.SimpleUniverse;

import matchers.geofs.PlaceMatcher;
import matchers.geofs.RegionMatcher;

public class GitTest {
	@Rule public JUnitRuleMockery context = new JUnitRuleMockery();

	static Universe uv = new SimpleUniverse();
	static LocalFileSystem lfs = new LocalFileSystem(uv);
	static GitWorld world = new GitWorld(uv);

	@Test
	public void theRootHasNoName() throws Exception {
		assertNull(world.root().name());
	}
	
	@Test
	public void theRootHasNoParent() throws Exception {
		assertNull(world.root().parent());
	}
	
	@Test
	public void testWeCanStreamADriveFile() throws Exception {
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line("\uFEFFhello, world!");
			oneOf(lsnr).complete();
		}});

		world.root().place("hw").lines(lsnr);
	}

	@Test
	public void testWeCanFindASubregion() throws Exception {
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line("\uFEFFhello, world!");
			oneOf(lsnr).complete();
		}});

		world.root().subregion("testregions").place("hwtest").lines(lsnr);
	}

	@Test
	public void theSubregionKnowsItsName() throws Exception {
		assertEquals("testregions", world.root().subregion("testregions").name());
	}

	@Test
	public void theSubregionKnowsItsParent() throws Exception {
		Region root = world.root();
		assertEquals(root, root.subregion("testregions").parent());
	}
	
	@Test
	public void weCanFindThePlacesInARegion() throws Exception {
		PlaceListener lsnr = context.mock(PlaceListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).place(with(PlaceMatcher.called("hwtest")));
			oneOf(lsnr).place(with(PlaceMatcher.called("secondfile")));
		}});

		world.root().subregion("testregions").places(lsnr);
	}	

	@Test
	public void weCanFindTheRegionsInARegion() throws Exception {
		RegionListener lsnr = context.mock(RegionListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).region(with(RegionMatcher.called("nested")));
		}});

		world.root().subregion("testregions").regions(lsnr);
	}	
	
	@Test
	public void weCanDownloadADriveFile() throws Exception {
		File tf = File.createTempFile("download", ",txt");
		LFSPlace local = new LFSPlace(null, tf);
		world.root().place("hw").copyTo(local);
		assertEquals("\uFEFFhello, world!", FileUtils.readFile(tf));
		tf.delete();
	}
}
