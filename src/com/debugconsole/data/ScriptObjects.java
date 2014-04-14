package com.debugconsole.data;

import java.util.Map;

import com.badlogic.gdx.utils.Array;
import com.debugconsole.DebugServer;
import com.debugconsole.HTMLBuilder;
import com.debugconsole.HTTPContent;
import com.debugconsole.NanoHTTPD.Response;
import com.debugconsole.SearchResult;

/** uses the ScriptEditor.ScriptProvider to navigate through Scripted objects just like we navigate through java objects */
public class ScriptObjects extends HTTPContent {
	ScriptEditor.ScriptProvider provider;
	String prettyName;
	String baseUrl;
	Object root;
	
	public ScriptObjects(DebugServer server, ScriptEditor.ScriptProvider prov, String prettyName, String baseUrl) {
		super(server);
		provider = prov;
		root = prov.getData(null);
	}

	@Override
	public boolean validFor(String url) {
		return url.contains(baseUrl);
	}

	@Override
	public Response getResponse(String url, Map<String, String> params) {
		return null;
	}

	@Override
	public void writeNavigation(HTMLBuilder builder, HTTPContent who) {
		if (who == this)
			builder.add("<li class='active'>").link(baseUrl).add(prettyName).pop().add("</li>");
		else
			builder.add("<li>").link(baseUrl).add(prettyName).pop().add("</li>");
	}

	@Override
	public void search(String[] query, Array<SearchResult> results) {
		
	}

	
}
