package com.debugconsole.data;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.debugconsole.DebugServer;
import com.debugconsole.HTMLBuilder;
import com.debugconsole.HTTPContent;
import com.debugconsole.NanoHTTPD.Response;
import com.debugconsole.NanoHTTPD.Response.Status;
import com.debugconsole.SearchResult;

public class AssetContent extends HTTPContent {
	AssetManager assets;
	String baseUrl;
	String prettyName;
	
	public AssetContent(DebugServer server, AssetManager assets, String prettyName, String baseUrl) {
		super(server);
		this.assets = assets;
		this.prettyName = prettyName;
		this.baseUrl = baseUrl;
	}
	
	@Override
	public boolean validFor(String url) {
		return url.indexOf("/" + baseUrl) == 0;
	}

	@Override
	public Response getResponse(String uri, Map<String, String> params) {
		String[] parts = uri.split("/");
		if (parts.length > 1 && parts[1].equalsIgnoreCase(baseUrl)) {
			if (uri.equalsIgnoreCase("/" + baseUrl)) {
				String html = FileContent.getHTML("assets.html");
				
				HTMLBuilder builder = new HTMLBuilder();
				builder.h3().add(prettyName).pop().line();
				for (String str : assets.getAssetNames()) {
					builder.row();
					builder.td();
					builder.link(baseUrl + "/" + str).add(str).pop();
					builder.pop();
					builder.td().add(assets.getAssetType(str).getSimpleName()).pop();
					builder.pop();
				}
				
				html = html.replace("${navigation}", getServer().getNavigation(this));
				html = html.replace("${fields}", builder.toString());
				
				return new Response(html);
			}
			String path = "";
			for (int i = 2; i < parts.length; ++i) {
				if (i > 2)
					path += "/";
				path += parts[i];
			}
			
			final Class type = assets.getAssetType(path);
			if (type == Texture.class) {
				FileHandle file = Gdx.files.local(path);
				if (file.extension().equalsIgnoreCase("png")) {
					return new Response(Status.OK,"image/png",file.read());
				} else if (file.extension().equalsIgnoreCase("jpg") || file.extension().equalsIgnoreCase("jpeg")) {
					return new Response(Status.OK,"image/jpeg",file.read());
				}
			} else {
				FileHandle file = Gdx.files.local(path);
				return new Response(Status.OK,"application/octet-stream",file.read());
			}
		}
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
		for (String str : assets.getAssetNames()) {
			for (String q : query) {
				if (str.contains(q)) {
					results.add(new SearchResult("Asset",str,baseUrl + "/" + str));
					break;
				}
			}
		}
	}

}
