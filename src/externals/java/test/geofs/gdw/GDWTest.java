package test.geofs.gdw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.gdw.GoogleDriveWorld;
import com.gmmapowell.geofs.lfs.LocalFileSystem;
import com.gmmapowell.geofs.listeners.LineListener;
import com.gmmapowell.geofs.simple.SimpleUniverse;

public class GDWTest {
	@Rule public JUnitRuleMockery context = new JUnitRuleMockery();

	static Universe uv = new SimpleUniverse();
	static LocalFileSystem lfs = new LocalFileSystem(uv);
	static GoogleDriveWorld world;

	@BeforeClass
	public static void connectToGoogle() throws IOException, GeneralSecurityException {
		world = new GoogleDriveWorld(uv, "ScriptFormatter", lfs.placePath("~/.ssh/google_scriptformatter_creds.json"));
	}

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
}
