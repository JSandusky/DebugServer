package com.debugconsole.data;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.Array;
import com.debugconsole.DebugServer;
import com.debugconsole.HTMLBuilder;
import com.debugconsole.HTTPContent;
import com.debugconsole.NanoHTTPD.Response;
import com.debugconsole.NanoHTTPD.Response.Status;
import com.debugconsole.SearchResult;

public abstract class ImageContent extends HTTPContent {
	String prettyName;
	String baseUrl;
	
	public ImageContent(DebugServer server, String prettyName, String baseUrl) {
		super(server);
		this.prettyName = prettyName;
		this.baseUrl = baseUrl;
	}

	@Override
	public boolean validFor(String url) {
		return url.indexOf("/" + baseUrl) == 0;
	}

	@Override
	public Response getResponse(String url, Map<String, String> params) {
		if (url.equalsIgnoreCase(getImagePath())) {
			Pixmap p = getPixmap();
			PixmapIO.writePNG(Gdx.files.local("tmp.png"), p);
			return new Response(Status.OK, "image/png",Gdx.files.local("tmp.png").read());
		}
		
		String html = FileContent.getHTML("image_source.html");
		html = html.replace("${title}", prettyName);
		html = html.replace("${servername}", getServer().getName());
		html = html.replace("${image}", "<img src='" + getImagePath() + "' />");
		html = html.replace("${navigation}",getServer().getNavigation(this));
		
		return new Response(html);
	}
	
	String getImagePath() {
		return "/" + baseUrl + "/0.png";
	}
	
	public abstract Pixmap getPixmap();

	@Override
	public void writeNavigation(HTMLBuilder builder, HTTPContent who) {
		if (who == this)
			builder.add("<li class='active'>").link("/" + baseUrl).add(prettyName).pop().add("</li>");
		else
			builder.add("<li>").link("/" + baseUrl).add(prettyName).pop().add("</li>");
	}

	@Override
	public void search(String[] query, Array<SearchResult> results) {
		
	}

}
