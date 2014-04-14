package com.debugconsole;

public class ByteString {
	public static String getString(long bytes) {
	    if (bytes < 1024) 
	    	return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(1024));
	    String pre = ("KMGTPE").charAt(exp-1) + "i";
	    return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
	}

}
