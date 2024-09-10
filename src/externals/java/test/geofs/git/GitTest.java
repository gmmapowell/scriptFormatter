package test.geofs.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.exceptions.GeoFSNoRegionException;
import com.gmmapowell.geofs.git.GitWorld;
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

	private static String gitroot;

	static {
		gitroot = System.getProperty("user.dir");
	}
	
	@Test
	public void theRootHasNoName() throws Exception {
		assertNull(world.root(gitroot).name());
	}
	
	@Test
	public void theRootHasNoParent() throws Exception {
		assertNull(world.root(gitroot).parent());
	}
	
	@Test
	public void testWeCanFindASubregionThatExists() throws Exception {
		Region subregion = world.root(gitroot).subregion("src").subregion("test");
		assertNotNull(subregion);
	}

	@Test(expected=GeoFSNoRegionException.class)
	public void testWeCannotFindASubregionThatDoesNotExist() throws Exception {
		world.root(gitroot).subregion("foobar");
	}

	@Test
	public void testWeCanCheckIfAPlaceExists() throws Exception {
		assertTrue(world.root(gitroot).hasPlace(".classpath"));
	}

	@Test
	public void testWeCanCheckIfAPlaceDoesntExist() throws Exception {
		assertFalse(world.root(gitroot).hasPlace("mars"));
	}

	@Test
	public void theSubregionKnowsItsName() throws Exception {
		assertEquals("src", world.root(gitroot).subregion("src").name());
	}

	@Test
	public void theSubregionKnowsItsParent() throws Exception {
		Region root = world.root(gitroot);
		assertEquals(root, root.subregion("src").parent());
	}
	
	@Test
	public void testWeCanStreamAFileFromGitHead() throws Exception {
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line("hello, world");
			oneOf(lsnr).complete();
		}});

		world.root(gitroot).place("src/test/resources/test/geofs/git/seconddir/hello.txt").lines(lsnr);
	}

	@Test
	public void testWeCanStreamAFileFromGitTag() throws Exception {
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line("tag FIRST_TEST");
			oneOf(lsnr).complete();
		}});

		world.root(gitroot+":FIRST_TEST").place("src/test/resources/test/geofs/git/file1.txt").lines(lsnr);
	}

	@Test
	public void weCanFindThePlacesInHEAD() throws Exception {
		PlaceListener lsnr = context.mock(PlaceListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).place(with(PlaceMatcher.called("file1.txt")));
			oneOf(lsnr).place(with(PlaceMatcher.called("file3.txt")));
		}});

		world.root(gitroot).regionPath("src/test/resources/test/geofs/git").places(lsnr);
	}	

	@Test
	public void weCanFindThePlacesInATag() throws Exception {
		PlaceListener lsnr = context.mock(PlaceListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).place(with(PlaceMatcher.called("file1.txt")));
		}});

		world.root(gitroot+":FIRST_TEST").subregion("src/test/resources/test/geofs/git").places(lsnr);
	}	

	@Test
	public void weCanFindTheRegionsInARegionFromHead() throws Exception {
		RegionListener lsnr = context.mock(RegionListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).region(with(RegionMatcher.called("indir")));
			oneOf(lsnr).region(with(RegionMatcher.called("seconddir")));
		}});

		world.root(gitroot).subregion("src/test/resources/test/geofs/git").regions(lsnr);
	}	

	@Test
	public void weCanFindTheRegionsInARegionFromATag() throws Exception {
		RegionListener lsnr = context.mock(RegionListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).region(with(RegionMatcher.called("indir")));
		}});

		world.root(gitroot+":FIRST_TEST").subregion("src/test/resources/test/geofs/git").regions(lsnr);
	}	
}
