package matchers.geofs;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.gmmapowell.geofs.Place;

public class PlaceMatcher extends TypeSafeMatcher<Place> {
	private final String called;

	public PlaceMatcher(String called) {
		this.called = called;
	}

	@Override
	public void describeTo(Description arg0) {
		arg0.appendText("a place called ");
		arg0.appendValue(called);
	}

	@Override
	protected boolean matchesSafely(Place arg0) {
		return arg0.name().equals(called);
	}

	public static PlaceMatcher called(String called) {
		return new PlaceMatcher(called);
	}

}
