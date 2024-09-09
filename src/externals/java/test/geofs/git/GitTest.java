package test.geofs.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Ignore;
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
	public void theSubregionKnowsItsName() throws Exception {
		assertEquals("src", world.root(gitroot).subregion("src").name());
	}

	@Test
	public void theSubregionKnowsItsParent() throws Exception {
		Region root = world.root(gitroot);
		assertEquals(root, root.subregion("src").parent());
	}
	
	@Test
	@Ignore
	public void testWeCanStreamAFileFromGitHead() throws Exception {
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line("\uFEFFhello, world!");
			oneOf(lsnr).complete();
		}});

		world.root(gitroot).place("hw").lines(lsnr);
	}

	@Test
	@Ignore
	public void testWeCanStreamAFileFromGitTag() throws Exception {
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line("\uFEFFhello, world!");
			oneOf(lsnr).complete();
		}});

		world.root(gitroot).place("hw").lines(lsnr);
	}

	@Test
	public void weCanFindThePlacesInHEAD() throws Exception {
		PlaceListener lsnr = context.mock(PlaceListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).place(with(PlaceMatcher.called(".gitstr")));
		}});

		world.root(gitroot).regionPath("src/test/resources").places(lsnr);
	}	

	@Test
	@Ignore
	public void weCanFindThePlacesInATag() throws Exception {
		PlaceListener lsnr = context.mock(PlaceListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).place(with(PlaceMatcher.called("hwtest")));
			oneOf(lsnr).place(with(PlaceMatcher.called("secondfile")));
		}});

		world.root(gitroot).subregion("testregions").places(lsnr);
	}	

	@Test
	@Ignore
	public void weCanFindTheRegionsInARegion() throws Exception {
		RegionListener lsnr = context.mock(RegionListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).region(with(RegionMatcher.called("nested")));
		}});

		world.root(gitroot).subregion("testregions").regions(lsnr);
	}	
}
