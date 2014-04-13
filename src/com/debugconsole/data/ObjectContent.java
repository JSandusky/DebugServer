package com.debugconsole.data;

import java.lang.reflect.Field;
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
	
	public ObjectContent(DebugServer server, String name, Object root, String baseUrl) {
		super(server);
		this.root = root;
		this.baseUrl = baseUrl;
		this.name = name;
	}
	
	@Override
	public boolean validFor(String url) {
		return url.contains(baseUrl);
	}

	@Override
	public Response getResponse(String url, Map<String,String> parameters) {
		URIResolver reso = new URIResolver(root);
		Object target = reso.resolve(url.replace(baseUrl,""));
		
		if (target == null) {
			String html = FileContent.getHTML("null.html");
			html = html.replace("${navigation}",getServer().getNavigation(this));
			return new Response(html);
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
			String[] parts = url.split("/");
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
			html = html.replace("${path}", builder.toString());
			html = html.replace("${class}", target.getClass().getSimpleName());
			html = html.replace("${objectname}",target.toString());
		}
		
		HTMLBuilder builder = new HTMLBuilder();
			
			ObjToHTML.buildTable(target,builder, url);
			
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
