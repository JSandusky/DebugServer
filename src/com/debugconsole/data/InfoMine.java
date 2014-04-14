package com.debugconsole.data;

import java.util.Map;

import com.badlogic.gdx.utils.Array;
import com.debugconsole.DebugServer;
import com.debugconsole.HTMLBuilder;
import com.debugconsole.HTTPContent;
import com.debugconsole.NanoHTTPD.Response;
import com.debugconsole.SearchResult;

public abstract class InfoMine extends HTTPContent {
	String baseUrl;
	String prettyName;
	public InfoMine(DebugServer server,String prettyName, String baseUrl) {
		super(server);
		this.baseUrl = baseUrl;
		this.prettyName = prettyName;
	}
	
	@Override
	public String getPrettyName() {
		return prettyName;
	}
	
	public abstract String getText();

	@Override
	public boolean validFor(String url) {
		return url.indexOf("/" + baseUrl) == 0;
	}

	@Override
	public Response getResponse(String url, Map<String, String> params) {
		String html = FileContent.getHTML("text_dump.html");
		html = html.replace("${title}",prettyName);
		html = html.replace("${servername}", getServer().getName());
		html = html.replace("${content}",getText().replace("\n", "<br/>").replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;"));
		html = html.replace("${navigation}",getServer().getNavigation(this));
		return new Response(html);
	}

	@Override
	public void writeNavigation(HTMLBuilder builder, HTTPContent who) {
		if (who == this)
			builder.add("<li class='active'>").link("/" + baseUrl).add(prettyName).pop().add("</li>");
		else
			builder.add("<li>").link("/" + baseUrl).add(prettyName).pop().add("</li>");
	}

	@Override
	public void search(String[] query, Array<SearchResult> results) {
		String txt = getText();
		for (String str : query) {
			if (txt.contains(str)) {
				SearchResult sr = new SearchResult("Info Mine", prettyName, "/" + baseUrl);
				break;
			}
		}
	}
	
}
