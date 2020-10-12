package com.gmmapowell.script.sink.pdf;

import java.io.IOException;

public interface PageCompositor {

	void begin() throws IOException;

	Acceptance token(StyledToken next) throws IOException;

	void close() throws IOException;


}
