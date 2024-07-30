package test.geofs;

import static org.junit.Assert.assertEquals;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.exceptions.GeoFSDuplicateWorldException;
import com.gmmapowell.geofs.exceptions.GeoFSNoWorldException;
import com.gmmapowell.geofs.simple.SimpleUniverse;

public class UniverseTests {
	@Rule public JUnitRuleMockery context = new JUnitRuleMockery();
	SimpleUniverse test = new SimpleUniverse();

	@Test(expected = GeoFSNoWorldException.class)
	public void nothingThereNothingFound() {
		test.getWorld("notthere");
	}

	@Test
	public void canFindLFS() {
		World lfs = context.mock(World.class, "lfs");
		test.register("lfs", lfs);
		assertEquals(lfs, test.getWorld("lfs"));
	}

	@Test(expected = GeoFSNoWorldException.class)
	public void otherThingNothingFound() {
		World lfs = context.mock(World.class, "lfs");
		test.register("lfs", lfs);
		test.getWorld("notthere");
	}

	@Test(expected = GeoFSDuplicateWorldException.class)
	public void cannotRegisterTheSameNameTwice() {
		World lfs = context.mock(World.class, "lfs");
		test.register("lfs", lfs);
		test.register("lfs", lfs);
	}
}
