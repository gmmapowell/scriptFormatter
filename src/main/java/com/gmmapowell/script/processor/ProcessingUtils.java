package com.gmmapowell.script.processor;

import com.gmmapowell.script.elements.ElementFactory;
import com.gmmapowell.script.elements.Span;
import com.gmmapowell.script.elements.SpanBlock;

// This should be unit tested
public class ProcessingUtils {
	public static void addSpans(ElementFactory factory, SpanBlock block, String text) {
		int startIdx;
		while ((startIdx = text.indexOf(" _")) != -1) { // NOTE: this is <space> <underscore>
			int endIdx = startIdx+2;
			while ((endIdx = text.indexOf("_", endIdx)) != -1) {
				if (endIdx == text.length()-1)
					break;
				else {
					char c = text.charAt(endIdx+1);
					if (!Character.isLetterOrDigit(c))
						break;
				}
			}
			if (endIdx != -1) {
				Span span = factory.span(null, text.substring(0, startIdx+1)); // +1 to include the space
				block.addSpan(span);
				
				Span itspan = factory.span("italic", text.substring(startIdx+2, endIdx)); // +2 to skip the space & underscore, 
				block.addSpan(itspan);
				
				text = text.substring(endIdx+1); // +1 skips the underscore but leaves the space in text
			} else
				break;
		}
		Span span = factory.span(null, text);
		block.addSpan(span);
	}
}
