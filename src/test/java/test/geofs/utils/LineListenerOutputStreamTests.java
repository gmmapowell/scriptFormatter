package test.geofs.utils;

import java.io.IOException;
import java.io.OutputStream;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.gmmapowell.geofs.listeners.LineListener;
import com.gmmapowell.geofs.listeners.NumberedLineListener;
import com.gmmapowell.geofs.utils.LineListenerOutputStream;

// TODO: there is something to be said for introducing a "Sequence" to ensure that everything comes through in the right order
// But I think that is logically going to happen.
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
	public void twoPartialLinesComesAreJoined() throws IOException {
		LineListener lsnr = context.mock(LineListener.class);
		OutputStream os = LineListenerOutputStream.oslsnr(lsnr);
		os.write("hello, ".getBytes());
		os.flush();
		os.write("world".getBytes());
		os.flush();
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
	public void newlinesInTheMiddleOfTwoStringsAreResolvedCorrectly() throws IOException {
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line("good morning");
			oneOf(lsnr).line("hello, world");
			oneOf(lsnr).line("Salutations!");
			oneOf(lsnr).complete();
		}});
		OutputStream os = LineListenerOutputStream.oslsnr(lsnr);
		os.write("good morning\nhello, ".getBytes());
		os.flush();
		os.write("world\nSalutations!".getBytes());
		os.flush();
		os.close();
	}

	@Test
	public void newlineBecomesABlankLine() throws IOException {
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line("hello,");
			oneOf(lsnr).line("");
			oneOf(lsnr).line("world");
			oneOf(lsnr).complete();
		}});
		OutputStream os = LineListenerOutputStream.oslsnr(lsnr);
		os.write("hello,\n\nworld".getBytes());
		os.close();
	}

	@Test
	public void initialNewlineBecomesABlankLine() throws IOException {
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line("");
			oneOf(lsnr).line("hello, world");
			oneOf(lsnr).complete();
		}});
		OutputStream os = LineListenerOutputStream.oslsnr(lsnr);
		os.write("\nhello, world\n".getBytes());
		os.close();
	}

	@Test
	public void crBeforeNlIsTrimmed() throws IOException {
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line("hello, world");
			oneOf(lsnr).complete();
		}});
		OutputStream os = LineListenerOutputStream.oslsnr(lsnr);
		os.write("hello, world\r\n".getBytes());
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

	@Test
	public void lineNumbersAreAvailable() throws IOException {
		NumberedLineListener lsnr = context.mock(NumberedLineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line(1, "good morning");
			oneOf(lsnr).line(2, "hello, world");
			oneOf(lsnr).line(3, "Salutations!");
			oneOf(lsnr).complete();
		}});
		OutputStream os = LineListenerOutputStream.oslsnr(lsnr);
		os.write("good morning\nhello, ".getBytes());
		os.flush();
		os.write("world\nSalutations!".getBytes());
		os.flush();
		os.close();
	}
}

