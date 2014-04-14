package com.debugconsole;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;


public class URIResolver {
	Object relTo;
	
	public URIResolver(Object obj) { //set our base
		relTo = obj;
	}
	
	public static class Meta {
		public Field fld;
		public Object parent;
	}
	
	public Object resolve(String uri, Meta meta) {
		String[] parts = uri.split("/");
		if (parts.length > 1) {
			try {
				Object working = relTo;
				for (int i = 1; i < parts.length; ++i) {
					if (parts[i].equalsIgnoreCase("/") || parts[i].length() == 0)
						continue;
					if (working != null && meta != null)
						meta.parent = working;
					String part = parts[i];
					int value = Integer.MIN_VALUE;
					try {
						value = Integer.parseInt(part);
					} catch (Exception ex) {
						
					}
					if (value != Integer.MIN_VALUE) {
						if (working instanceof com.badlogic.gdx.utils.Array) {
							working = ((com.badlogic.gdx.utils.Array)working).get(value);
						} else 
							working = Array.get(working, value);
					} else {
						Field fld = working.getClass().getDeclaredField(parts[i]);
						fld.setAccessible(true);
						meta.fld = fld;
						Object val = fld.get(working);
						working = val;
					}
				}
				return working;
			} catch (Exception ex) { 
			}
		}
		return relTo;
	}
	
	
}
