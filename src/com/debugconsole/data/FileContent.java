package com.debugconsole.data;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.debugconsole.DebugServer;
import com.debugconsole.HTMLBuilder;
import com.debugconsole.HTTPContent;
import com.debugconsole.StringResponse;
import com.debugconsole.NanoHTTPD.Response;
import com.debugconsole.NanoHTTPD.Response.Status;

public class FileContent extends HTTPContent {

	public FileContent(DebugServer server) {
		super(server);
	}
	
	@Override
	public boolean validFor(String url) {
		return true;
	}
	
	public static String getHTML(String fileName) {
		FileHandle file = Gdx.files.classpath("com/debug/web/" + fileName);
		return file.readString();
	}

	@Override
	public Response getResponse(String url, Map<String, String> params) {
		FileHandle file = Gdx.files.classpath("com/debug/web" + url);
		if (file.extension().equals("js")) {
			return new Response(Status.OK,"application/javascript",file.read());
		} else if (file.extension().equals("css")) {
			return new Response(Status.OK,"text/css",file.read());
		} else if (file.extension().equalsIgnoreCase("html")) {
			return new StringResponse(file.readString());
		}
		return null;
	}

	@Override
	public void writeNavigation(HTMLBuilder builder, HTTPContent who) {
		// TODO Auto-generated method stub
		
	}

}
