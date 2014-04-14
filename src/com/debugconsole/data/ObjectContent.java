package com.debugconsole.data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import com.badlogic.gdx.utils.Array;
import com.debugconsole.DebugServer;
import com.debugconsole.HTMLBuilder;
import com.debugconsole.HTTPContent;
import com.debugconsole.ObjToHTML;
import com.debugconsole.SearchResult;
import com.debugconsole.URIResolver;
import com.debugconsole.NanoHTTPD.Response;

public class ObjectContent extends HTTPContent {
	Object root;
	String baseUrl;
	String name;
	InfoExtend extension;
	ObjToHTML.DocSource docSource;
	
	public interface InfoExtend {
		public void extend(Field fld, Object obj, HTMLBuilder builder);
		public void extend(Class clazz, HTMLBuilder builder);
	}
	
	public ObjectContent(DebugServer server, String name, Object root, String baseUrl) {
		this(server,name,root,baseUrl,null);
	}
	
	public ObjectContent(DebugServer server, String name, Object root, String baseUrl, InfoExtend ext) {
		super(server);
		extension = ext;
		this.root = root;
		this.baseUrl = baseUrl;
		this.name = name;
		
		docSource = new ObjToHTML.DocSource() {
			@Override
			public String getMethodDoc(Method method) {
				//??
				return null;
			}
			
			@Override
			public String getFieldDoc(Field fld) {
				HTMLBuilder builder = new HTMLBuilder();
				extension.extend(fld, null, builder);
				return builder.toString();
			}
		};
	}
	
	
	@Override
	public boolean validFor(String url) {
		return url.contains(baseUrl);
	}

	@Override
	public Response getResponse(String url, Map<String,String> parameters) {
		URIResolver reso = new URIResolver(root);
		URIResolver.Meta meta = new URIResolver.Meta();
		Object target = reso.resolve(url.replace(baseUrl,""),meta);
		
		if (parameters.containsKey("instance")) { //??the hell was this about
			
		}
		
		if (target == null && !parameters.containsKey("INSTANTIATE")) {
			String html = FileContent.getHTML("null.html");
			html = html.replace("${nullpath}","<div class='well'>" + url + "</div>");
			html = html.replace("${navigation}",getServer().getNavigation(this));
			return new Response(html);
		} else if (target == null && parameters.containsKey("INSTANTIATE")){
			try {
				Object val = meta.fld.getType().newInstance();
				meta.fld.set(meta.parent, val);
			} catch (Exception ex) {
				String html = FileContent.getHTML("error_create.html");
				html = html.replace("${nullpath}","<div class='well'>" + url + "</div>");
				html = html.replace("${navigation}", getServer().getNavigation(this));
				return new Response(html);
			}
		}
		
		if (parameters.size() > 1) {
			synchronized (target) {
				for (Map.Entry<String,String> entry : parameters.entrySet()) {
					try {
						Field fld = target.getClass().getDeclaredField(entry.getKey());
						Setter.set(fld, target, entry.getValue());
					} catch (Exception ex) {
						
					}
				}
			}
		}
		
		String html = FileContent.getHTML("object.html");
		html = html.replace("${navigation}",getServer().getNavigation(this));
		
		
		{
			HTMLBuilder builder = new HTMLBuilder();
			builder.ol("breadcrumb");
			String[] parts = url.split("/");
			if (parts.length > 1) {
				String link = "";
				
				for (int i = 0; i < parts.length-1; ++i) {
					link += parts[i] + "/";
				}
				builder.li().link(link).add("main").pop().pop();
			}
			
			String link = "";
			for (String str : parts) {
				link += str + "/";
				builder.li().link(link.replace("//","/")).add(str).pop().pop();
			}
			builder.pop();
			html = html.replace("${path}", builder.toString());
			html = html.replace("${class}", target.getClass().getSimpleName());
			html = html.replace("${objectname}",target.toString());
			if (extension != null) {
				HTMLBuilder ext = new HTMLBuilder();
				extension.extend(target.getClass(), ext);
				html = html.replace("${classinfo}", ext.toString());
			} else {
				html = html.replace("${classinfo}", "");
			}
		}
		
		HTMLBuilder builder = new HTMLBuilder();
			
		
		if (!parameters.containsKey("METHODS") || parameters.get("METHODS").equalsIgnoreCase("0")) {
			html = html.replace("${switchlink}","<a href='" + url + "?METHODS=1'>Methods</a>");
			ObjToHTML.buildTable(target,builder, url, docSource);
		} else {
			html = html.replace("${switchlink}","<a href='" + url + "?METHODS=0'>Fields</a>");
			html = html.replace("Field-name","Method-name");
			html = html.replace("Value", "Parameters");
			ObjToHTML.writeMethodTable(target, builder, url, docSource);
		}
		
		
		html = html.replace("${fields}",builder.toString());
		
		return new Response(html);
	}

	@Override
	public void writeNavigation(HTMLBuilder builder, HTTPContent who) {
		if (who == this)
			builder.add("<li class='active'>").link(baseUrl).add(name).pop().add("</li>");
		else
			builder.add("<li>").link(baseUrl).add(name).pop().add("</li>");
	}

	@Override
	public void search(String[] query, Array<SearchResult> results) {
		Array<Object> checked = new Array<Object>(); //prevents circle searches
		search(root, baseUrl, query, results, checked,0);
	}
	
	static void search(Object obj, String url, String[] query, Array<SearchResult> results, Array<Object> checked, int depth) {
		//if (depth > 16)
			//return;
		checked.add(obj);
		for (Field fld : obj.getClass().getDeclaredFields()) {
			fld.setAccessible(true);
			
			final String fldName = fld.getName();
			for (String q : query) {
				if (fldName.contains(q)) {
					results.add(new SearchResult("Object",obj.toString(),url));
					break;
				}
			}
			
			try {
				Object val = fld.get(obj);
				if (!checked.contains(val, false)) {
					search(val,url + "/" + fld.getName(), query, results,checked, depth + 1);
				}
			} catch (Exception ex) {
				//don't care
			}
		}
	}
}
