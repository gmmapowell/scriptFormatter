package test.geofs.utils;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.exceptions.GeoFSInvalidWorldException;
import com.gmmapowell.geofs.utils.GeoFSNoRegionException;
import com.gmmapowell.geofs.utils.GeoFSUtils;

/** Paths are a hack in my view, but a necessary one.
 * So all of path processing is extracted into GeoFSUtils, although it is then referenced directly by worlds and regions to make
 * users' lives easier.
 */
// RegionPaths and PlacePaths are very similar, except the final step is "subregion" or "place" respectively
// So much so that the logic is shared for finding the top region
public class FollowPlacePaths {
	@Rule public JUnitRuleMockery context = new JUnitRuleMockery();

	@Test
	public void aSimpleOneSegmentPathIsJustAPlaceInTheRegion() {
		World world = context.mock(World.class);
		Region region = context.mock(Region.class, "parent");
		Place p = context.mock(Place.class, "p");
		context.checking(new Expectations() {{
			oneOf(region).place("region"); will(returnValue(p));
		}});
		Place place = GeoFSUtils.placePath(world, region, "region");
		assertEquals(p, place);
	}

	@Test(expected=GeoFSNoRegionException.class)
	public void cannotFindARelativePathFromAWorld() {
		World world = context.mock(World.class);
		GeoFSUtils.placePath(world, null, "place");
	}

	@Test
	public void aDoubleSegmentPathCallsSubregionTwice() {
		World world = context.mock(World.class);
		Region region = context.mock(Region.class, "parent");
		Region sr = context.mock(Region.class, "sr");
		Place p = context.mock(Place.class, "p");

		context.checking(new Expectations() {{
			oneOf(region).subregion("region"); will(returnValue(sr));
			oneOf(sr).place("then"); will(returnValue(p));
		}});
		Place place = GeoFSUtils.placePath(world, region, "region/then");
		assertEquals(p, place);
	}

	@Test
	public void anAbsoluteOneSegmentPathIsJustASubregion() {
		World world = context.mock(World.class);
		Region root = context.mock(Region.class, "/");
		Region region = context.mock(Region.class, "unused");
		Place sr = context.mock(Place.class, "sr");
		context.checking(new Expectations() {{
			oneOf(world).root(); will(returnValue(root));
			oneOf(root).place("from"); will(returnValue(sr));
		}});
		Place place = GeoFSUtils.placePath(world, region, "/from");
		assertEquals(sr, place);
	}

	@Test
	public void anAbsoluteDoubleSegmentPathCallsSubregionTwice() {
		World world = context.mock(World.class);
		Region root = context.mock(Region.class, "/");
		Region region = context.mock(Region.class, "unused");
		Region sr = context.mock(Region.class, "sr");
		Place p = context.mock(Place.class, "p");

		context.checking(new Expectations() {{
			oneOf(world).root(); will(returnValue(root));
			oneOf(root).subregion("from"); will(returnValue(sr));
			oneOf(sr).place("place"); will(returnValue(p));
		}});
		Place place = GeoFSUtils.placePath(world, region, "/from/place");
		assertEquals(p, place);
	}

	@Test
	public void tildeRepresentsTheHomeDirectory() {
		World world = context.mock(World.class);
		Region root = context.mock(Region.class, "/");
		Region region = context.mock(Region.class, "unused");
		Place p = context.mock(Place.class, "p");
		context.checking(new Expectations() {{
			oneOf(world).root("~"); will(returnValue(root));
			oneOf(root).place("from"); will(returnValue(p));
		}});
		Place place = GeoFSUtils.placePath(world, region, "~/from");
		assertEquals(p, place);
	}

	@Test
	public void tildeNameRepresentsAnotherUsersDirectory() {
		World world = context.mock(World.class);
		Region root = context.mock(Region.class, "/");
		Region region = context.mock(Region.class, "unused");
		Place p = context.mock(Place.class, "p");
		context.checking(new Expectations() {{
			oneOf(world).root("~henry"); will(returnValue(root));
			oneOf(root).place("from"); will(returnValue(p));
		}});
		Place place = GeoFSUtils.placePath(world, region, "~henry/from");
		assertEquals(p, place);
	}

	@Test
	public void aPlaceCanBeDirectlyInTheRoot() {
		World world = context.mock(World.class);
		Region root = context.mock(Region.class, "C:");
		Region region = context.mock(Region.class, "unused");
		Place p = context.mock(Place.class, "p");

		context.checking(new Expectations() {{
			oneOf(world).root("C:"); will(returnValue(root));
			oneOf(root).place("place"); will(returnValue(p));
		}});
		Place place = GeoFSUtils.placePath(world, region, "C:/place");
		assertEquals(p, place);
	}

	@Test
	public void windowsHasDriveLetters() {
		World world = context.mock(World.class);
		Region root = context.mock(Region.class, "C:");
		Region region = context.mock(Region.class, "unused");
		Region sr = context.mock(Region.class, "sr");
		Place p = context.mock(Place.class, "p");

		context.checking(new Expectations() {{
			oneOf(world).root("C:"); will(returnValue(root));
			oneOf(root).subregion("from"); will(returnValue(sr));
			oneOf(sr).place("place"); will(returnValue(p));
		}});
		Place place = GeoFSUtils.placePath(world, region, "C:/from/place");
		assertEquals(p, place);
	}

	@Test
	public void weCanHandleURITypeRelativePaths() {
		Universe universe = context.mock(Universe.class);
		World world = context.mock(World.class);
		Region region = context.mock(Region.class, "region");
		Region sr = context.mock(Region.class, "sr");
		Place ret = context.mock(Place.class, "ret");

		context.checking(new Expectations() {{
			oneOf(world).getUniverse(); will(returnValue(universe));
			oneOf(universe).getWorld("file"); will(returnValue(world));
			oneOf(region).subregion("from"); will(returnValue(sr));
			oneOf(sr).place("place"); will(returnValue(ret));
		}});
		Place place = GeoFSUtils.placePath(world, region, "file://from/place");
		assertEquals(ret, place);
	}

	@Test
	public void weCanHandleURITypeAbsolutePaths() {
		Universe universe = context.mock(Universe.class);
		World world = context.mock(World.class);
		Region root = context.mock(Region.class, "/");
		Region region = context.mock(Region.class, "unused");
		Region sr = context.mock(Region.class, "sr");
		Place ret = context.mock(Place.class, "ret");

		context.checking(new Expectations() {{
			oneOf(world).getUniverse(); will(returnValue(universe));
			oneOf(universe).getWorld("file"); will(returnValue(world));
			oneOf(world).root(); will(returnValue(root));
			oneOf(root).subregion("from"); will(returnValue(sr));
			oneOf(sr).place("place"); will(returnValue(ret));
		}});
		Place place = GeoFSUtils.placePath(world, region, "file:///from/place");
		assertEquals(ret, place);
	}

	@Test
	public void weCanHandleURITypePathsWithWindowsDriveLetters() {
		Universe universe = context.mock(Universe.class);
		World world = context.mock(World.class);
		Region root = context.mock(Region.class, "/");
		Region region = context.mock(Region.class, "unused");
		Region sr = context.mock(Region.class, "sr");
		Place ret = context.mock(Place.class, "ret");

		context.checking(new Expectations() {{
			oneOf(world).getUniverse(); will(returnValue(universe));
			oneOf(universe).getWorld("file"); will(returnValue(world));
			oneOf(world).root("C:"); will(returnValue(root));
			oneOf(root).subregion("from"); will(returnValue(sr));
			oneOf(sr).place("place"); will(returnValue(ret));
		}});
		Place place = GeoFSUtils.placePath(world, region, "file://C:/from/place");
		assertEquals(ret, place);
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
		Place place = GeoFSUtils.placePath(world, region, "google://from/region");
		assertEquals(ret, place);
	}

	@Test
	public void weCanHandleURITypePathsOnGoogleWorld() {
		Universe universe = context.mock(Universe.class);
		World world = context.mock(World.class, "lfs");
		World gdw = context.mock(World.class, "gdw");
		Region root = context.mock(Region.class, "/");
		Region region = context.mock(Region.class, "unused");
		Region sr = context.mock(Region.class, "sr");
		Place ret = context.mock(Place.class, "ret");

		context.checking(new Expectations() {{
			oneOf(world).getUniverse(); will(returnValue(universe));
			oneOf(universe).getWorld("google"); will(returnValue(gdw));
			oneOf(gdw).root(); will(returnValue(root));
			oneOf(root).subregion("from"); will(returnValue(sr));
			oneOf(sr).place("place"); will(returnValue(ret));
		}});
		Place place = GeoFSUtils.placePath(world, region, "google:///from/place");
		assertEquals(ret, place);
	}
}
