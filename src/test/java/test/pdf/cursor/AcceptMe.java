package test.pdf.cursor;

import org.hamcrest.Description;
import org.jmock.api.Action;
import org.jmock.api.Invocation;

import com.gmmapowell.script.flow.StyledToken;
import com.gmmapowell.script.sink.pdf.Acceptability;
import com.gmmapowell.script.sink.pdf.Acceptance;

public class AcceptMe implements Action {
	private Acceptability acc;
	private StyledToken token;
	private AcceptMe pullFrom;

	public AcceptMe(Acceptability acc) {
		this.acc = acc;
	}

	public AcceptMe(Acceptability acc, AcceptMe apb) {
		this.acc = acc;
		this.pullFrom = apb;
	}

	@Override
	public void describeTo(Description arg0) {
		arg0.appendText("returns Accepting[");
		arg0.appendValue(acc);
		if (pullFrom != null) {
			arg0.appendText(", ");
			if (pullFrom.token != null) { 
				arg0.appendValue(pullFrom.token);
			} else {
				arg0.appendText("<pending>");
			}
		}
		arg0.appendText("]");
	}

	@Override
	public Object invoke(Invocation invocation) throws Throwable {
		this.token = (StyledToken) invocation.getParameter(0);
		return new Acceptance(acc, this.pullFrom != null ? this.pullFrom.token : null);
	}

}
