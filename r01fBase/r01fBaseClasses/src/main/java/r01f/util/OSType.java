package r01f.util;

import r01f.enums.EnumExtended;
import r01f.patterns.Memoized;

public enum OSType 
 implements EnumExtended<OSType> {
	WINDOWS,
	MacOS,
	Linux,
	NIX,
	UNKNOWN;
	
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////	
	private static String OS_PROP = System.getProperty("os.name").toLowerCase();
	private static transient Memoized<OSType> OS = new Memoized<>() {
															@Override
															public OSType supply() {
																if (OS_PROP.indexOf("win") >= 0) return OSType.WINDOWS;
																if (OS_PROP.indexOf("mac") >= 0 || OS_PROP.indexOf("darwin") >= 0) return OSType.MacOS;
																if (OS_PROP.indexOf("nux") >= 0) return OSType.Linux;
																if (OS_PROP.indexOf("nix") >= 0 || OS_PROP.indexOf("aix") > 0 || OS_PROP.indexOf("sunos") >= 0) return OSType.NIX;
																throw new IllegalStateException("Unknown os.name property: " + OS_PROP + " cannot detect the OS");
															}
												  };
	public static OSType getOS() {
		return OS.get();
	}
}
