package test.geofs.utils;

import java.io.IOException;
import java.io.OutputStream;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.gmmapowell.geofs.listeners.LineListener;
import com.gmmapowell.geofs.utils.LineListenerOutputStream;

public class LineListenerOutputStreamTests {
	@Rule public JUnitRuleMockery context = new JUnitRuleMockery();

	@Test
	public void weCanWriteHelloWorldAsASingleLine() throws IOException {
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line("hello, world");
			oneOf(lsnr).complete();
		}});
		OutputStream os = LineListenerOutputStream.oslsnr(lsnr);
		os.write("hello, world\n".getBytes());
		os.close();
	}

	@Test
	public void aPartialLineComesThroughOnClose() throws IOException {
		LineListener lsnr = context.mock(LineListener.class);
		OutputStream os = LineListenerOutputStream.oslsnr(lsnr);
		os.write("hello, world".getBytes());
		context.checking(new Expectations() {{
			oneOf(lsnr).line("hello, world");
			oneOf(lsnr).complete();
		}});
		os.close();
	}

	@Test
	public void twoLinesComeThroughSeparately() throws IOException {
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line("hello, world");
			oneOf(lsnr).line("good morning");
			oneOf(lsnr).complete();
		}});
		OutputStream os = LineListenerOutputStream.oslsnr(lsnr);
		os.write("hello, world\ngood morning\n".getBytes());
		os.close();
	}

	@Test
	public void otherWhiteSpaceIsNotTrimmed() throws IOException {
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line(" hello, world ");
			oneOf(lsnr).complete();
		}});
		OutputStream os = LineListenerOutputStream.oslsnr(lsnr);
		os.write(" hello, world \n".getBytes());
		os.close();
	}
	// TODO: \n at the front
	// TODO: \r\n
	// TODO: \n\n
	// TODO: split across two writes
	// TODO: we can have line numbers as well
}

