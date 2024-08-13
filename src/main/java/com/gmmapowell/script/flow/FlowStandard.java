package com.gmmapowell.script.flow;

// The standard operations on flows are short codes with the high byte being '00' and the low byte being the opcode
public class FlowStandard {

	public static final short TEXT = 0x0000;
	public static final short BREAKING_SPACE = 0x0001;
	
	public static final short LINK_OP = 0x0010;
	
	public static final short NESTED = 0x0070;

}
