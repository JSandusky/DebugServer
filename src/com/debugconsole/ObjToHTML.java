package com.debugconsole;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ObjectMap;

public final class ObjToHTML {
	
	//this is used
	public static void buildTable(Object obj, HTMLBuilder builder, String uri) {
		int controlID = 5;
		for (Field fld : obj.getClass().getDeclaredFields()) {
			fld.setAccessible(true);
			if (Modifier.isStatic(fld.getModifiers()))
				continue;
			
			
			builder.row();
			
				builder.td();
				builder.bold();
				if (!fld.getType().isPrimitive()) {
					if (uri.length() == 1)
						builder.link(fld.getName()).add(fld.getName()).pop();
					else
						builder.link((uri + "/" + fld.getName()).replace("//", "/")).add(fld.getName()).pop();
				} else {
					builder.add(fld.getName());
				}
				builder.pop();
				builder.pop();
				
				builder.td().add(fld.getType().getSimpleName());
				builder.line();
				final int modifiers = fld.getModifiers();
				if (Modifier.isFinal(modifiers))
					builder.color("SaddleBrown").add(" Final ").pop();
				if (Modifier.isTransient(modifiers))
					builder.color("OliveDrab").add(" Transient ").pop();
				if (Modifier.isPrivate(modifiers))
					builder.color("purple").add(" Private ").pop();
				builder.pop();
				
				builder.td();
				try {
					Object val = fld.get(obj);
					if (val == null)
						builder.add("NULL");
					else
						writeField(fld,val,builder,controlID,uri);
				} catch (Exception ex) {
					builder.add("<EXCEPTION>");
					//ex.printStackTrace();
				}
				builder.pop();
			
			builder.pop();
			++controlID;
		}
	}
	
	@Deprecated
	public static void buildHtml(Object obj, HTMLBuilder builder, String uri) {
		
		String[] parts = uri.split("/");
		if (parts.length > 1) {
			String link = "";
			for (int i = 0; i < parts.length-1; ++i) {
				link += parts[i] + "/";
			}
			builder.link(link).add("Back").pop();
		}
		
		String link = "";
		boolean any = false;
		for (String str : parts) {
			if (any) {
				builder.add(" -> ");
			}
			any = true;
			link += str + "/";
			builder.link(link.replace("//","/")).add(str).pop();
		}
		builder.line();
		
		builder.table();
		builder.row();
			builder.td(3).h3().add(obj.getClass().getSimpleName() + ": " + obj.toString()).pop().pop().pop();
			
		builder.row("CCCCCC");
		builder.td().add("Name").pop();
		builder.td().add("Type").pop();
		builder.td().add("Value").pop();
		builder.pop();
		
		buildTable(obj,builder,uri);
		builder.pop();
		
		builder.add("<input type='button' value='Refresh' onClick='javascript:history.go(0)'/>");
		builder.add("<input type='submit' value='Commit Edits'/>");
	}
	
	static void writeField(Field fld, Object obj, HTMLBuilder writer, int id, String uri) {
		if (fld.getType().isArray()) {
			int len = Array.getLength(obj);
			for (int i = 0; i < len; ++i) {
				writer.link((uri + "/" + fld.getName() + "/" + i).replace("//", "/")).add(Array.get(obj, i).toString()).pop();
				writer.line();
			}
		} else if (fld.getType() == com.badlogic.gdx.utils.Array.class) {
			com.badlogic.gdx.utils.Array ar = (com.badlogic.gdx.utils.Array) obj;
			for (int i = 0; i < ar.size; ++i) {
				writer.link((uri + "/" + fld.getName() + "/" + i).replace("//", "/")).add(ar.get(i).toString()).pop();
				writer.line();
			}
		} else if (fld.getType() == ObjectMap.class) {
			ObjectMap om = (ObjectMap)obj;
			for (Object key : om.keys()) {
				writer.add(key.toString() + " - " + om.get(key).toString());
				writer.line();
			}
		} else if (fld.getType().isPrimitive()) {
			if (fld.getType() == boolean.class)
				writer.add("<input name='" + fld.getName() + "' type='checkbox' checked='" + obj.toString() + "'/>");
			else
				writer.add("<input name='" + fld.getName() + "' type='text' value='" + obj.toString() + "'/>");
		} else if (fld.getType() == String.class) {
			writer.add("<input name='" + fld.getName() + "' type='text' value='" + obj.toString() + "'/>");
		} else if (fld.getType() == Color.class) {
			writer.add(obj.toString());
			writer.add("<span style=\"font-size:20px; padding-left:30px; background:" + obj.toString().substring(0,6) + ";\">&nbsp;</span>");
		} else {
			writer.add(obj.toString());
		}
	}
}
