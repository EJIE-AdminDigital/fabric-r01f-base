package r01f.types;

import r01f.enums.EnumExtended;

public enum ExecutionMode 
 implements EnumExtended<ExecutionMode> {
	SYNC,
	ASYNC;
	
	public static ExecutionMode fromName(final String name) {
		return EnumExtended.fromName(name,ExecutionMode.class)
						   .orElseThrow(() -> new IllegalArgumentException("no " + ExecutionMode.class + " element with name " + name));
	}
}
