package com.gmmapowell.script.flow;

import com.gmmapowell.script.sink.pdf.PageCompositor;

/** This is an interface which allows the client/sink to "talk back" to the
 * Cursor, e.g. to let it know that a new page has been selected.
 * 
 * As far as I can see, FlowCursor will be the only implementation of this interface,
 * but it is here for testing purposes.
 */
public interface CursorFeedback {

	void allProcessed(PageCompositor page);

	void backTo(StyledToken lastAccepted);

	void noRoom(StyledToken lastAccepted);

	void suspend(StyledToken lastAccepted, String enable);

}
