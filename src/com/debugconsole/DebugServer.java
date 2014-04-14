package com.debugconsole;

import java.io.IOException;
import java.util.Map;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.debugconsole.data.FileContent;

public class DebugServer extends NanoHTTPD {
	Array<HTTPContent> content = new Array<HTTPContent>();
	
	public DebugServer(AssetManager assets) {
		super("localhost",8091);
	}
	
	@Override
	public void start() throws IOException {
		content.add(new FileContent(this));
		super.start();
	}
	
	public void addContent(HTTPContent content) {
		this.content.add(content);
	}
	
	public void removeContent(HTTPContent content) {
		this.content.removeValue(content, true);
	}
	
	static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
	
	@Override
    public Response serve(String uri, Method method, 
                          Map<String, String> header,
                          Map<String, String> parameters,
                          Map<String, String> files) {
		
		if (uri.equalsIgnoreCase("/"))
			uri = "/main.html";
		
		if (parameters.containsKey("SEARCH_TEXT")) {
			final String search = parameters.get("SEARCH_TEXT");
			
			if (search.length() > 0) {
				parameters.remove("SEARCH_TEXT");
				
				final String[] query = search.split(" ");
				Array<SearchResult> results = new Array<SearchResult>();
				for (HTTPContent c : content) {
					c.search(query, results); 
				}
				String html = FileContent.getHTML("search.html");
				html = html.replace("${navigation}", getNavigation(null));
				HTMLBuilder res = new HTMLBuilder();
				for (SearchResult r : results) {
					res.row();
						res.td();
							res.add(r.kind);
						res.pop();
						res.td();
							res.link(r.url);
							res.add(r.display);
						res.pop();
					res.pop();
				}
				html = html.replace("${fields}", res.toString());
				return new Response(html);
			}
		}
		
		for (int i = 0; i < content.size; ++i) {
			HTTPContent c = content.get(i);
			if (c.validFor(uri)) {
				Response r = c.getResponse(uri, parameters);
				if (r != null) {
					if (r instanceof StringResponse) {
						String str = convertStreamToString(r.getData());
						HTMLBuilder builder = new HTMLBuilder();
						for (int j = 0; j < content.size; ++j) {
							content.get(j).writeNavigation(builder,null);
						}
						str = str.replace("${navigation}", builder.toString());
						return new Response(str);
					} else {
						return r;
					}
				}
			}
		}
		
		HTMLBuilder builder = getBase();
		for (HTTPContent c : content) {
			c.writeNavigation(builder,null);
			builder.line();
		}
		
		builder.pop().pop();
		return new Response(builder.toString());
	}
	
	public String getNavigation(HTTPContent who) {
		HTMLBuilder builder = new HTMLBuilder();
		for (int i = 0; i < content.size; ++i) {
			if (content.get(i).display())
				content.get(i).writeNavigation(builder,who);
		}
		return builder.toString();
	}
	
	public static HTMLBuilder getBase() {
		HTMLBuilder builder = new HTMLBuilder();
		builder.html().head().title().add("Ibu Debug Server").pop().pop();
		builder.body();
		builder.h3().add("Debug Server").pop();
		return builder;
	}
}
