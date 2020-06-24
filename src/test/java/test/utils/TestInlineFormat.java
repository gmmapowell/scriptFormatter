package test.utils;

import java.util.Arrays;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.Span;
import com.gmmapowell.script.elements.SpanBlock;
import com.gmmapowell.script.processor.ProcessingUtils;
import com.gmmapowell.script.processor.prose.CurrentState;

public class TestInlineFormat {
	public @Rule JUnitRuleMockery context = new JUnitRuleMockery();
	CurrentState state = new CurrentState();

	@Test
	public void aSimpleCaseWithNothingFancy() {
		ElementFactory factory = context.mock(ElementFactory.class);
		SpanBlock block = context.mock(SpanBlock.class);
		Span s1 = context.mock(Span.class);
		context.checking(new Expectations() {{
			oneOf(factory).span(null, "hello"); will(returnValue(s1));
			oneOf(block).addSpan(s1);
		}});
		ProcessingUtils.addSpans(factory, state, block, "hello");
	}

	@Test
	public void aNormalItalic() {
		ElementFactory factory = context.mock(ElementFactory.class);
		SpanBlock block = context.mock(SpanBlock.class);
		Span s1 = context.mock(Span.class, "s1");
		Span s2 = context.mock(Span.class, "s2");
		Span s3 = context.mock(Span.class, "s3");
		context.checking(new Expectations() {{
			oneOf(factory).span(null, "hello "); will(returnValue(s1));
			oneOf(block).addSpan(s1);
			oneOf(factory).span("italic", "there"); will(returnValue(s2));
			oneOf(block).addSpan(s2);
			oneOf(factory).span(null, " world"); will(returnValue(s3));
			oneOf(block).addSpan(s3);
		}});
		ProcessingUtils.addSpans(factory, state, block, "hello _there_ world");
	}

	@Test
	public void endOfLineItalic() {
		ElementFactory factory = context.mock(ElementFactory.class);
		SpanBlock block = context.mock(SpanBlock.class);
		Span s1 = context.mock(Span.class, "s1");
		Span s2 = context.mock(Span.class, "s2");
		context.checking(new Expectations() {{
			oneOf(factory).span(null, "hello "); will(returnValue(s1));
			oneOf(block).addSpan(s1);
			oneOf(factory).span("italic", "there"); will(returnValue(s2));
			oneOf(block).addSpan(s2);
		}});
		ProcessingUtils.addSpans(factory, state, block, "hello _there_");
	}

	@Test
	public void beginningItalic() {
		ElementFactory factory = context.mock(ElementFactory.class);
		SpanBlock block = context.mock(SpanBlock.class);
		Span s1 = context.mock(Span.class, "s1");
		Span s3 = context.mock(Span.class, "s3");
		context.checking(new Expectations() {{
			oneOf(factory).span("italic", "hello"); will(returnValue(s1));
			oneOf(block).addSpan(s1);
			oneOf(factory).span(null, " world"); will(returnValue(s3));
			oneOf(block).addSpan(s3);
		}});
		ProcessingUtils.addSpans(factory, state, block, "_hello_ world");
	}

	@Test
	public void inAWordUnderscoreIsJustPassedThrough() {
		ElementFactory factory = context.mock(ElementFactory.class);
		SpanBlock block = context.mock(SpanBlock.class);
		Span s1 = context.mock(Span.class);
		context.checking(new Expectations() {{
			oneOf(factory).span(null, "CONSTANT_VALUE"); will(returnValue(s1));
			oneOf(block).addSpan(s1);
		}});
		ProcessingUtils.addSpans(factory, state, block, "CONSTANT_VALUE");
	}

	@Test
	public void aNormalTT() {
		ElementFactory factory = context.mock(ElementFactory.class);
		SpanBlock block = context.mock(SpanBlock.class);
		Span s1 = context.mock(Span.class, "s1");
		Span s2 = context.mock(Span.class, "s2");
		Span s3 = context.mock(Span.class, "s3");
		context.checking(new Expectations() {{
			oneOf(factory).span(null, "hello "); will(returnValue(s1));
			oneOf(block).addSpan(s1);
			oneOf(factory).span("tt", "there"); will(returnValue(s2));
			oneOf(block).addSpan(s2);
			oneOf(factory).span(null, " world"); will(returnValue(s3));
			oneOf(block).addSpan(s3);
		}});
		ProcessingUtils.addSpans(factory, state, block, "hello $there$ world");
	}

	@Test
	public void codeInsideItalic() {
		ElementFactory factory = context.mock(ElementFactory.class);
		SpanBlock block = context.mock(SpanBlock.class);
		Span s1 = context.mock(Span.class, "s1");
		Span s2 = context.mock(Span.class, "s2");
		Span s3 = context.mock(Span.class, "s3");
		Span s4 = context.mock(Span.class, "s4");
		context.checking(new Expectations() {{
			oneOf(factory).span(null, "hello "); will(returnValue(s1));
			oneOf(block).addSpan(s1);
			oneOf(factory).span("italic", "this is "); will(returnValue(s2));
			oneOf(block).addSpan(s2);
			oneOf(factory).lspan(Arrays.asList("italic", "tt"), "MY"); will(returnValue(s3));
			oneOf(block).addSpan(s3);
			oneOf(factory).span("italic", " world"); will(returnValue(s4));
			oneOf(block).addSpan(s4);
		}});
		ProcessingUtils.addSpans(factory, state, block, "hello _this is $MY$ world_");
	}

	@Test
	public void aNormalBold() {
		ElementFactory factory = context.mock(ElementFactory.class);
		SpanBlock block = context.mock(SpanBlock.class);
		Span s1 = context.mock(Span.class, "s1");
		Span s2 = context.mock(Span.class, "s2");
		Span s3 = context.mock(Span.class, "s3");
		context.checking(new Expectations() {{
			oneOf(factory).span(null, "hello "); will(returnValue(s1));
			oneOf(block).addSpan(s1);
			oneOf(factory).span("bold", "there"); will(returnValue(s2));
			oneOf(block).addSpan(s2);
			oneOf(factory).span(null, " world"); will(returnValue(s3));
			oneOf(block).addSpan(s3);
		}});
		ProcessingUtils.addSpans(factory, state, block, "hello *there* world");
	}

	@Test
	public void doubleDollarIsEscape() {
		ElementFactory factory = context.mock(ElementFactory.class);
		SpanBlock block = context.mock(SpanBlock.class);
		Span s1 = context.mock(Span.class);
		context.checking(new Expectations() {{
			oneOf(factory).span(null, "this cost me $$420$$ you see"); will(returnValue(s1));
			oneOf(block).addSpan(s1);
		}});
		ProcessingUtils.addSpans(factory, state, block, "this cost me $$420$$ you see");
	}

	@Test
	public void italicInversionWhenNested() { // actually, somebody downstream will have to do the inversion; we just observe the nesting
		ElementFactory factory = context.mock(ElementFactory.class);
		SpanBlock block = context.mock(SpanBlock.class);
		Span s1 = context.mock(Span.class, "s1");
		Span s2 = context.mock(Span.class, "s2");
		Span s3 = context.mock(Span.class, "s3");
		Span s4 = context.mock(Span.class, "s4");
		context.checking(new Expectations() {{
			oneOf(factory).span(null, "hello "); will(returnValue(s1));
			oneOf(block).addSpan(s1);
			oneOf(factory).span("italic", "this is "); will(returnValue(s2));
			oneOf(block).addSpan(s2);
			oneOf(factory).lspan(Arrays.asList("italic", "italic"), "MY"); will(returnValue(s3));
			oneOf(block).addSpan(s3);
			oneOf(factory).span("italic", " world"); will(returnValue(s4));
			oneOf(block).addSpan(s4);
		}});
		ProcessingUtils.addSpans(factory, state, block, "hello _this is _MY_ world_");
	}
}
