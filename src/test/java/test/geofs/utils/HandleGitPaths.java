package test.geofs.utils;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.utils.GeoFSUtils;

public class HandleGitPaths {
	@Rule public JUnitRuleMockery context = new JUnitRuleMockery();

	@Test
	public void aLiveGitPathHasThreeSegments() {
		Universe u = context.mock(Universe.class);
		World world = context.mock(World.class, "lfs-say");
		World gitWorld = context.mock(World.class, "gitworld");
		Region region = context.mock(Region.class, "parent");
		Region repoRoot = context.mock(Region.class, "repoRoot");
		Region sr = context.mock(Region.class, "sr");
		context.checking(new Expectations() {{
			oneOf(world).getUniverse(); will(returnValue(u));
			oneOf(u).getWorld("git"); will(returnValue(gitWorld));
			oneOf(gitWorld).root("~/repo"); will(returnValue(repoRoot));
			oneOf(repoRoot).subregion("region"); will(returnValue(sr));
		}});
		Region sub = GeoFSUtils.regionPath(world, region, "git:~/repo:/region");
		assertEquals(sr, sub);
	}

	@Test
	public void aTaggedGitPathHasFourSegments() {
		Universe u = context.mock(Universe.class);
		World world = context.mock(World.class);
		World gitWorld = context.mock(World.class, "gitworld");
		Region repoRoot = context.mock(Region.class, "repoRoot");
		Region region = context.mock(Region.class, "parent");
		Region sr = context.mock(Region.class, "sr");
		context.checking(new Expectations() {{
			oneOf(world).getUniverse(); will(returnValue(u));
			oneOf(u).getWorld("git"); will(returnValue(gitWorld));
			oneOf(gitWorld).root("~/repo:TAG"); will(returnValue(repoRoot));
			oneOf(repoRoot).subregion("region"); will(returnValue(sr));
		}});
		Region sub = GeoFSUtils.regionPath(world, region, "git:~/repo:TAG:/region");
		assertEquals(sr, sub);
	}

}
