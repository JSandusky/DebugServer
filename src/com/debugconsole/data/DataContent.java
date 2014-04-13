package com.debugconsole.data;

import java.util.Map;

import com.debugconsole.DebugServer;
import com.debugconsole.HTMLBuilder;
import com.debugconsole.HTTPContent;
import com.debugconsole.NanoHTTPD.Response;

//TODO: file exploration
public class DataContent extends HTTPContent {

	public DataContent(DebugServer server) {
		super(server);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean validFor(String url) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Response getResponse(String url, Map<String, String> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeNavigation(HTMLBuilder builder, HTTPContent who) {
		// TODO Auto-generated method stub
		
	}

}
