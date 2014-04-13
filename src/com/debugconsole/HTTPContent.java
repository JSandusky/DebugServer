package com.debugconsole;

import java.util.Map;

import com.debugconsole.NanoHTTPD.Response;

public abstract class HTTPContent {
	protected DebugServer server;
	public DebugServer getServer() {
		return server;
	}
	public HTTPContent(DebugServer server) {
		this.server = server;
	}
	public abstract boolean validFor(String url);
	public abstract Response getResponse(String url, Map<String,String> params);
	public abstract void writeNavigation(HTMLBuilder builder, HTTPContent who);
}
