package test.geofs.utils;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.World;
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
}
