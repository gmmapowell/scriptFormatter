package test.geofs.gdw;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.gmmapowell.geofs.Universe;
import com.gmmapowell.geofs.gdw.GoogleDriveWorld;
import com.gmmapowell.geofs.lfs.LocalFileSystem;
import com.gmmapowell.geofs.listeners.LineListener;
import com.gmmapowell.geofs.simple.SimpleUniverse;

@Ignore
public class GDWTest {
	@Rule public JUnitRuleMockery context = new JUnitRuleMockery();

	@Test
	public void testWeCanStreamADriveFile() throws Exception {
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line("hello, world!");
			oneOf(lsnr).complete();
		}});

		Universe uv = new SimpleUniverse();
		LocalFileSystem lfs = new LocalFileSystem(uv);
		GoogleDriveWorld world = new GoogleDriveWorld(uv, "ScriptFormatter", lfs.placePath("~/.ssh/google_scriptformatter_creds.json"));
		world.root().place("hw").lines(lsnr);
	}

}
