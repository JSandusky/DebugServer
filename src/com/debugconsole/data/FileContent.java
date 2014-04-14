package com.debugconsole.data;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.debugconsole.DebugServer;
import com.debugconsole.HTMLBuilder;
import com.debugconsole.HTTPContent;
import com.debugconsole.SearchResult;
import com.debugconsole.StringResponse;
import com.debugconsole.NanoHTTPD.Response;
import com.debugconsole.NanoHTTPD.Response.Status;

public class FileContent extends HTTPContent {

	public FileContent(DebugServer server) {
		super(server);
	}
	
	public boolean intercept(String url) {
		return (url.contains(".css") || url.contains(".js"));
	}
	
	@Override
	public boolean validFor(String url) {
		return true;
	}
	
	public static String getHTML(String fileName) {
		FileHandle file = Gdx.files.classpath("com/debugconsole/web/" + fileName);
		return file.readString();
	}

	@Override
	public Response getResponse(String url, Map<String, String> params) {
		/*if (url.indexOf("/js/") != -1) {
			url = url.substring(url.indexOf("/js/"));
		} else if (url.indexOf("/css/") != -1) {
			url = url.substring(url.indexOf("/css/"));
		} else if (url.contains(".css") || url.contains(".js")) {
			url = "/" + url.substring(url.lastIndexOf("/"));
		}*/
		FileHandle file = Gdx.files.classpath("com/debugconsole/web" + url);
		if (file.extension().equals("js")) {
			return new Response(Status.OK,"application/javascript",file.read());
		} else if (file.extension().equals("css")) {
			return new Response(Status.OK,"text/css",file.read());
		} else if (file.extension().equals("woff")) {
			return new Response(Status.OK, "application/x-font-woff", file.read());
		} else if (file.extension().equals("ttf")) {
			return new Response(Status.OK, "font/opentype", file.read());
		} else if (file.extension().equals("svg")) {
			return new Response(Status.OK, "image/svg+xml", file.read());
		} else if (file.extension().equalsIgnoreCase("html")) {
			return new StringResponse(file.readString());
		}
		return null;
	}

	@Override
	public void writeNavigation(HTMLBuilder builder, HTTPContent who) {
		
		
		
	}

	@Override
	public void search(String[] query, Array<SearchResult> results) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getPrettyName() {
		return null;
	}
}
