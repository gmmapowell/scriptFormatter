package test.geofs;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.zinutils.support.jmock.ByteArrayMatcher;

import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.doubled.RegionDouble;
import com.gmmapowell.geofs.doubled.WorldDouble;
import com.gmmapowell.geofs.listeners.BinaryBlockListener;
import com.gmmapowell.geofs.listeners.LineListener;
import com.gmmapowell.geofs.listeners.NumberedLineListener;

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

	@Test
	public void testWeCanStreamATextFileAsNumberedLines() {
		World world = new WorldDouble();
		((RegionDouble)world.root()).addPlace("helloworld", "hello, world!\n");
		NumberedLineListener lsnr = context.mock(NumberedLineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line(1, "hello, world!");
		}});
		world.root().place("helloworld").lines(lsnr);
	}

	@Test
	public void testCRsAreRemovedFromTheEnd() {
		World world = new WorldDouble();
		((RegionDouble)world.root()).addPlace("helloworld", "hello, world!\r\n");
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line("hello, world!");
		}});
		world.root().place("helloworld").lines(lsnr);
	}

	@Test
	public void testLinesAreNotOtherwiseTrimmed() {
		World world = new WorldDouble();
		((RegionDouble)world.root()).addPlace("helloworld", " hello, world! \r\n");
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line(" hello, world! ");
		}});
		world.root().place("helloworld").lines(lsnr);
	}

	@Test
	public void testWeCanStreamABinaryFileInBlocks() {
		World world = new WorldDouble();
		byte[] bs = "hello\'\"world\r\n".getBytes();
		((RegionDouble)world.root()).addPlace("helloworld", bs);
		BinaryBlockListener lsnr = context.mock(BinaryBlockListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).block(with(ByteArrayMatcher.of(bs)), with(14));
		}});
		world.root().place("helloworld").binary(lsnr);
	}
}
