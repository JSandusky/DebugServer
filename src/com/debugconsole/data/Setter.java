package com.debugconsole.data;

import java.lang.reflect.Field;

public final class Setter {
	public static void set(Field fld, Object owner, String value) {
		try {
			fld.setAccessible(true);
			if (fld.getType() == boolean.class) {
				if (value.equalsIgnoreCase("on"))
					fld.setBoolean(owner, true);
				else if (value.equalsIgnoreCase("off"))
					fld.setBoolean(owner, false);
				else
					fld.setBoolean(owner, Boolean.parseBoolean(value));
			} else if (fld.getType() == int.class) {
				fld.setInt(owner, Integer.parseInt(value));
			} else if (fld.getType() == long.class) {
				fld.setLong(owner, Long.parseLong(value));
			} else if (fld.getType() == String.class) {
				fld.set(owner, value);
			} else if (fld.getType() == float.class) {
				fld.setFloat(owner, Float.parseFloat(value));
			} else if (fld.getType() == double.class) {
				fld.setDouble(owner, Double.parseDouble(value));
			}
		} catch (Exception e) {
			//don't care
			e.printStackTrace();
		}
	}
}
