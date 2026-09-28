package test.pdf.cursor;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.zinutils.exceptions.NotImplementedException;

import com.gmmapowell.script.flow.ParaBreak;
import com.gmmapowell.script.flow.SpanItem;
import com.gmmapowell.script.flow.StyledToken;
import com.gmmapowell.script.flow.TextSpanItem;

public class StyledTokenMatcher extends TypeSafeMatcher<StyledToken> {
	private final SpanItem si;

	private StyledTokenMatcher(SpanItem si) {
		this.si = si;
	}

	@Override
	public void describeTo(Description arg0) {
		arg0.appendText("StyledToken[");
		arg0.appendValue(si);
		arg0.appendText("]");
	}

	@Override
	protected boolean matchesSafely(StyledToken arg0) {
		SpanItem osi = arg0.it;
		if (si instanceof TextSpanItem) {
			if (!(osi instanceof TextSpanItem)) {
				return false;
			}
			return ((TextSpanItem)osi).text.equals(((TextSpanItem)si).text);
		} else if (si instanceof ParaBreak) {
			return osi instanceof ParaBreak;
		} else {
			throw new NotImplementedException("si is " + si.getClass());
		}
	}

	public static StyledTokenMatcher parabreak = new StyledTokenMatcher(new ParaBreak());

	public static StyledTokenMatcher token(SpanItem si) {
		return new StyledTokenMatcher(si);
	}
}
