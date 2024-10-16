package com.gmmapowell.script.flow;

import java.io.IOException;
import java.util.Set;

public interface CursorClient {
	void beginSection(Set<Cursor> cursors);
	boolean processToken(CursorFeedback cursor, StyledToken tok) throws IOException;
	void endSection();
}
