package test.geofs;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.doubled.RegionDouble;
import com.gmmapowell.geofs.doubled.WorldDouble;
import com.gmmapowell.geofs.listeners.LineListener;

public class StreamFile {
	@Rule public JUnitRuleMockery context = new JUnitRuleMockery();

	@Test
	public void testWeCanStreamATextFileAsLines() {
		World world = new WorldDouble();
		((RegionDouble)world.root()).addPlace("helloworld", "hello, world!\n");
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line("hello, world!");
		}});
		world.root().place("helloworld").lines(lsnr);
	}

}
