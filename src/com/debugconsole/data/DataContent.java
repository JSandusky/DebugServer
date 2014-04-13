package com.debugconsole.data;

import java.util.Date;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.debugconsole.DebugServer;
import com.debugconsole.HTMLBuilder;
import com.debugconsole.HTTPContent;
import com.debugconsole.NanoHTTPD.Response;
import com.debugconsole.SearchResult;

//TODO: file exploration
public class DataContent extends HTTPContent {

	public DataContent(DebugServer server) {
		super(server);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean validFor(String url) {
		return url.indexOf("/file") == 0;
	}

	@Override
	public Response getResponse(String url, Map<String, String> params) {
		String[] parts = url.split("/");
		String path = "";
		for (int i = 2; i < parts.length; ++i) {
			if (i > 2)
				path += "/";
			path += parts[i];
		}
		FileHandle file = Gdx.files.local(path.length() == 0 ? "/" : path);
		HTMLBuilder builder = new HTMLBuilder();
		
		for (FileHandle f : file.list()) {
			builder.row();
				builder.td();
				if (f.isDirectory()) {
					builder.link("file/" + f.path()).add(f.name()).pop();
				} else {
					builder.add(f.name());
				}
				builder.pop();
				
				if (f.isDirectory()) {
					builder.td();
					builder.add(" ");
					builder.pop();
				} else {
					builder.td();
					builder.add(f.extension());
					builder.pop();
				}
				
				builder.td();
					long date = f.lastModified();
					Date dt = new Date(date);
					builder.add(dt.toString());
				builder.pop();
			builder.pop();
		}
		String html = FileContent.getHTML("files.html");
		html = html.replace("${navigation}", getServer().getNavigation(this));
		html = html.replace("${fields}",builder.toString());
		
		return new Response(html);
	}

	@Override
	public void writeNavigation(HTMLBuilder builder, HTTPContent who) {
		if (who == this)
			builder.add("<li class='active'>").link("file").add("Files").pop().add("</li>");
		else
			builder.add("<li>").link("file").add("Files").pop().add("</li>");
	}

	@Override
	public void search(String[] query, Array<SearchResult> results) {
		// TODO Auto-generated method stub
		
	}

}
