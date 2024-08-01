package matchers.geofs;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.gmmapowell.geofs.Region;

public class RegionMatcher extends TypeSafeMatcher<Region> {
	private final String called;

	public RegionMatcher(String called) {
		this.called = called;
	}

	@Override
	public void describeTo(Description arg0) {
		arg0.appendText("a region called ");
		arg0.appendValue(called);
	}

	@Override
	protected boolean matchesSafely(Region arg0) {
		return arg0.name().equals(called);
	}

	public static RegionMatcher called(String called) {
		return new RegionMatcher(called);
	}

}
