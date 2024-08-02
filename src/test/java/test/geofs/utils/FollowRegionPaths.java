package test.geofs.utils;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.exceptions.GeoFSInvalidWorldException;
import com.gmmapowell.geofs.exceptions.GeoFSNoRegionException;
import com.gmmapowell.geofs.utils.GeoFSUtils;

/** Paths are a hack in my view, but a necessary one.
 * So all of path processing is extracted into GeoFSUtils, although it is then referenced directly by worlds and regions to make
 * users' lives easier.
 */
// RegionPaths and PlacePaths are very similar, except the final step is "subregion" or "place" respectively
// So much so that the logic is shared for finding the top region
public class FollowRegionPaths {
	@Rule public JUnitRuleMockery context = new JUnitRuleMockery();

	@Test
	public void aSimpleOneSegmentPathIsJustASubregion() {
		World world = context.mock(World.class);
		Region region = context.mock(Region.class, "parent");
		Region sr = context.mock(Region.class, "sr");
		context.checking(new Expectations() {{
			oneOf(region).subregion("region"); will(returnValue(sr));
		}});
		Region sub = GeoFSUtils.regionPath(world, region, "region");
		assertEquals(sr, sub);
	}

	@Test(expected=GeoFSNoRegionException.class)
	public void cannotFindARelativePathFromAWorld() {
		World world = context.mock(World.class);
		GeoFSUtils.regionPath(world, null, "region");
	}

	@Test
	public void aDoubleSegmentPathCallsSubregionTwice() {
		World world = context.mock(World.class);
		Region region = context.mock(Region.class, "parent");
		Region sr = context.mock(Region.class, "sr");
		Region ret = context.mock(Region.class, "ret");

		context.checking(new Expectations() {{
			oneOf(region).subregion("region"); will(returnValue(sr));
			oneOf(sr).subregion("then"); will(returnValue(ret));
		}});
		Region sub = GeoFSUtils.regionPath(world, region, "region/then");
		assertEquals(ret, sub);
	}

	@Test
	public void anAbsoluteOneSegmentPathIsJustASubregion() {
		World world = context.mock(World.class);
		Region root = context.mock(Region.class, "/");
		Region region = context.mock(Region.class, "unused");
		Region sr = context.mock(Region.class, "sr");
		context.checking(new Expectations() {{
			oneOf(world).root(); will(returnValue(root));
			oneOf(root).subregion("from"); will(returnValue(sr));
		}});
		Region sub = GeoFSUtils.regionPath(world, region, "/from");
		assertEquals(sr, sub);
	}

	@Test
	public void anAbsoluteDoubleSegmentPathCallsSubregionTwice() {
		World world = context.mock(World.class);
		Region root = context.mock(Region.class, "/");
		Region region = context.mock(Region.class, "unused");
		Region sr = context.mock(Region.class, "sr");
		Region ret = context.mock(Region.class, "ret");

		context.checking(new Expectations() {{
			oneOf(world).root(); will(returnValue(root));
			oneOf(root).subregion("from"); will(returnValue(sr));
			oneOf(sr).subregion("region"); will(returnValue(ret));
		}});
		Region sub = GeoFSUtils.regionPath(world, region, "/from/region");
		assertEquals(ret, sub);
	}

	@Test
	public void tildeRepresentsTheHomeDirectory() {
		World world = context.mock(World.class);
		Region root = context.mock(Region.class, "/");
		Region region = context.mock(Region.class, "unused");
		Region sr = context.mock(Region.class, "sr");
		context.checking(new Expectations() {{
			oneOf(world).root("~"); will(returnValue(root));
			oneOf(root).subregion("from"); will(returnValue(sr));
		}});
		Region sub = GeoFSUtils.regionPath(world, region, "~/from");
		assertEquals(sr, sub);
	}

	@Test
	public void tildeNameRepresentsAnotherUsersDirectory() {
		World world = context.mock(World.class);
		Region root = context.mock(Region.class, "/");
		Region region = context.mock(Region.class, "unused");
		Region sr = context.mock(Region.class, "sr");
		context.checking(new Expectations() {{
			oneOf(world).root("~henry"); will(returnValue(root));
			oneOf(root).subregion("from"); will(returnValue(sr));
		}});
		Region sub = GeoFSUtils.regionPath(world, region, "~henry/from");
		assertEquals(sr, sub);
	}

	@Test
	public void windowsHasDriveLetters() {
		World world = context.mock(World.class);
		Region root = context.mock(Region.class, "C:");
		Region region = context.mock(Region.class, "unused");
		Region sr = context.mock(Region.class, "sr");
		Region ret = context.mock(Region.class, "ret");

		context.checking(new Expectations() {{
			oneOf(world).root("C:"); will(returnValue(root));
			oneOf(root).subregion("from"); will(returnValue(sr));
			oneOf(sr).subregion("region"); will(returnValue(ret));
		}});
		Region sub = GeoFSUtils.regionPath(world, region, "C:/from/region");
		assertEquals(ret, sub);
	}

	@Test
	public void weCanHandleURITypeRelativePaths() {
		Universe universe = context.mock(Universe.class);
		World world = context.mock(World.class);
		Region region = context.mock(Region.class, "region");
		Region sr = context.mock(Region.class, "sr");
		Region ret = context.mock(Region.class, "ret");

		context.checking(new Expectations() {{
			oneOf(world).getUniverse(); will(returnValue(universe));
			oneOf(universe).getWorld("file"); will(returnValue(world));
			oneOf(region).subregion("from"); will(returnValue(sr));
			oneOf(sr).subregion("region"); will(returnValue(ret));
		}});
		Region sub = GeoFSUtils.regionPath(world, region, "file://from/region");
		assertEquals(ret, sub);
	}

	@Test
	public void weCanHandleURITypeAbsolutePaths() {
		Universe universe = context.mock(Universe.class);
		World world = context.mock(World.class);
		Region root = context.mock(Region.class, "/");
		Region region = context.mock(Region.class, "unused");
		Region sr = context.mock(Region.class, "sr");
		Region ret = context.mock(Region.class, "ret");

		context.checking(new Expectations() {{
			oneOf(world).getUniverse(); will(returnValue(universe));
			oneOf(universe).getWorld("file"); will(returnValue(world));
			oneOf(world).root(); will(returnValue(root));
			oneOf(root).subregion("from"); will(returnValue(sr));
			oneOf(sr).subregion("region"); will(returnValue(ret));
		}});
		Region sub = GeoFSUtils.regionPath(world, region, "file:///from/region");
		assertEquals(ret, sub);
	}

	@Test
	public void weCanHandleURITypePathsWithWindowsDriveLetters() {
		Universe universe = context.mock(Universe.class);
		World world = context.mock(World.class);
		Region root = context.mock(Region.class, "/");
		Region region = context.mock(Region.class, "unused");
		Region sr = context.mock(Region.class, "sr");
		Region ret = context.mock(Region.class, "ret");

		context.checking(new Expectations() {{
			oneOf(world).getUniverse(); will(returnValue(universe));
			oneOf(universe).getWorld("file"); will(returnValue(world));
			oneOf(world).root("C:"); will(returnValue(root));
			oneOf(root).subregion("from"); will(returnValue(sr));
			oneOf(sr).subregion("region"); will(returnValue(ret));
		}});
		Region sub = GeoFSUtils.regionPath(world, region, "file://C:/from/region");
		assertEquals(ret, sub);
	}

	@Test(expected = GeoFSInvalidWorldException.class)
	public void cannotUseRelativePathsOnAnotherWorld() {
		Universe universe = context.mock(Universe.class);
		World world = context.mock(World.class, "lfs");
		World gdw = context.mock(World.class, "gdw");
		Region region = context.mock(Region.class, "unused");
		Region ret = context.mock(Region.class, "ret");

		context.checking(new Expectations() {{
			oneOf(world).getUniverse(); will(returnValue(universe));
			oneOf(universe).getWorld("google"); will(returnValue(gdw));
		}});
		Region sub = GeoFSUtils.regionPath(world, region, "google://from/region");
		assertEquals(ret, sub);
	}

	@Test
	public void weCanHandleURITypePathsOnGoogleWorld() {
		Universe universe = context.mock(Universe.class);
		World world = context.mock(World.class, "lfs");
		World gdw = context.mock(World.class, "gdw");
		Region root = context.mock(Region.class, "/");
		Region region = context.mock(Region.class, "unused");
		Region sr = context.mock(Region.class, "sr");
		Region ret = context.mock(Region.class, "ret");

		context.checking(new Expectations() {{
			oneOf(world).getUniverse(); will(returnValue(universe));
			oneOf(universe).getWorld("google"); will(returnValue(gdw));
			oneOf(gdw).root(); will(returnValue(root));
			oneOf(root).subregion("from"); will(returnValue(sr));
			oneOf(sr).subregion("region"); will(returnValue(ret));
		}});
		Region sub = GeoFSUtils.regionPath(world, region, "google:///from/region");
		assertEquals(ret, sub);
	}
}
