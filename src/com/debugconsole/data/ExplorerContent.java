package com.debugconsole.data;

import java.util.Date;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.debugconsole.ByteString;
import com.debugconsole.DebugServer;
import com.debugconsole.HTMLBuilder;
import com.debugconsole.HTTPContent;
import com.debugconsole.NanoHTTPD.Response;
import com.debugconsole.NanoHTTPD.Response.Status;
import com.debugconsole.SearchResult;

//TODO: file exploration
public class ExplorerContent extends HTTPContent {

	FileHandleResolver resolver;
	String baseUrl;
	String prettyName;
	Array<String> treatAsText = new Array<String>();
	
	public ExplorerContent(DebugServer server, FileHandleResolver reso, String prettyName, String baseUrl) {
		super(server);
		resolver = reso;
		this.prettyName = prettyName;
		this.baseUrl = baseUrl;
	}
	
	public void addTextType(String extension) {
		treatAsText.add(extension);
	}

	@Override
	public boolean validFor(String url) {
		return url.indexOf("/" + baseUrl) == 0;
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
		FileHandle file = resolver.resolve("./" + path);//Gdx.files.internal(path.length() == 0 ? "./bin" : path);
		if (!file.isDirectory()) {
			if (file.extension().equalsIgnoreCase("jpg") || file.extension().equalsIgnoreCase("jpeg")) {
				return new Response(Status.OK, "image/jpeg",file.read());
			} else if (file.extension().equalsIgnoreCase("png")) {
				return new Response(Status.OK, "image/png",file.read());
			} else if (file.extension().equalsIgnoreCase("json") || file.extension().equalsIgnoreCase("txt") || file.extension().equalsIgnoreCase("xml") ||
					treatAsText.contains(file.extension(), false)) {
				return new Response(Status.OK, "text/plain",file.read());
			} else {
				return new Response(Status.OK, "application/octet-stream",file.read());
			}
		}
		HTMLBuilder builder = new HTMLBuilder();
		
		for (FileHandle f : file.list()) {
			builder.row();
				builder.td();
				if (f.isDirectory()) {
					builder.span("glyphicon glyphicon-folder-open").pop().add("&nbsp;&nbsp;");
					builder.link("/" + baseUrl + "/" + f.path()).add(f.name()).pop();
				} else {
					Glyphs.getGlyp(f.extension(), builder);
					builder.link("/" + baseUrl + "/" + f.path()).add(f.name()).pop();
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
				builder.add(ByteString.getString(f.length()));
				builder.pop();
				
				builder.td();
					long date = f.lastModified();
					Date dt = new Date(date);
					builder.add(dt.toString());
				builder.pop();
			builder.pop();
		}
		String html = FileContent.getHTML("files.html");
		html = html.replace("${servername}", getServer().getName());
		html = html.replace("${navigation}", getServer().getNavigation(this));
		html = html.replace("${fields}",builder.toString());
		html = html.replace("${path}",file.path());
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getPrettyName() {
		return prettyName;
	}

	@Override
	public String getDoc() {
		return "Navigate/download files stored on the remote machine";
	}
}
