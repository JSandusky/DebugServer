package com.debugconsole;

import java.io.IOException;
import java.util.Map;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.debugconsole.data.FileContent;

public class DebugServer extends NanoHTTPD {
	public static Object root;
	AssetManager assets;
	Array<HTTPContent> content = new Array<HTTPContent>();
	
	public DebugServer(AssetManager assets) {
		super("localhost",8091);
		this.assets = assets;
	}
	
	@Override
	public void start() throws IOException {
		content.add(new FileContent(this));
		super.start();
	}
	
	public void addContent(HTTPContent content) {
		this.content.add(content);
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
		for (HTTPContent c : content) {
			if (c.validFor(uri)) {
				Response r = c.getResponse(uri, parameters);
				if (r != null) {
					if (r instanceof StringResponse) {
						String str = convertStreamToString(r.getData());
						HTMLBuilder builder = new HTMLBuilder();
						for (int i = 0; i < content.size; ++i) {
							content.get(i).writeNavigation(builder,null);
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
