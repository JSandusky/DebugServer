package com.debugconsole.data;

import java.util.Map;

import com.badlogic.gdx.utils.Array;
import com.debugconsole.DebugServer;
import com.debugconsole.HTMLBuilder;
import com.debugconsole.HTTPContent;
import com.debugconsole.NanoHTTPD.Response;
import com.debugconsole.SearchResult;
import com.debugconsole.data.ScriptEditor.MessageData;

/** used to make an ajax-ish compiler */
public class CompilerContent extends HTTPContent {
	String baseUrl;
	ScriptEditor.ScriptProvider provider;
	
	public CompilerContent(DebugServer server, ScriptEditor.ScriptProvider provider, String baseUrl) {
		super(server);
		this.baseUrl = baseUrl;
		this.provider = provider;
	}

	@Override
	public boolean validFor(String url) {
		return url.indexOf("/" + baseUrl) == 0;
	}

	@Override
	public Response getResponse(String url, Map<String, String> params) {
		//??should always be a post
		String code = params.get("NanoHttpd.QUERY_STRING");
		
		Array<MessageData> messages = new Array<MessageData>();
		if (!provider.compile(code, messages)) {
			HTMLBuilder builder = new HTMLBuilder();
			for (MessageData m : messages) {
				builder.div("alert alert-danger alert-dismissable").add(m.message).pop();
			}
			return new Response(builder.toString());
		} else {
			HTMLBuilder builder = new HTMLBuilder();
			builder.div("alert alert-success").add("Execution succeeded").pop();
			return new Response(builder.toString());
		}
	}

	@Override
	public void writeNavigation(HTMLBuilder builder, HTTPContent who) {
		
	}

	@Override
	public void search(String[] query, Array<SearchResult> results) {
		
	}

	@Override
	public boolean display() {
		return false;
	}
	
	@Override
	public String getPrettyName() {
		return null;
	}
}
