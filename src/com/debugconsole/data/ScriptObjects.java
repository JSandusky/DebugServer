package com.debugconsole.data;

import java.util.Comparator;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.debugconsole.DebugServer;
import com.debugconsole.HTMLBuilder;
import com.debugconsole.HTTPContent;
import com.debugconsole.NanoHTTPD.Response;
import com.debugconsole.SearchResult;
import com.debugconsole.data.ScriptEditor.ObjectData;

/** uses the ScriptEditor.ScriptProvider to navigate through Scripted objects just like we navigate through java objects */
public class ScriptObjects extends HTTPContent {
	ScriptEditor.ScriptProvider provider;
	String prettyName;
	String baseUrl;
	
	public ScriptObjects(DebugServer server, ScriptEditor.ScriptProvider prov, String prettyName, String baseUrl) {
		super(server);
		provider = prov;
		this.prettyName = prettyName;
		this.baseUrl = baseUrl;
	}

	@Override
	public boolean validFor(String url) {
		return url.indexOf("/" + baseUrl) == 0;
	}
	
	static Comparator<ScriptEditor.ObjectData> sort = new Comparator<ScriptEditor.ObjectData>() {
		@Override
		public int compare(ObjectData arg0, ObjectData arg1) {
			return arg0.Name.compareTo(arg1.Name);
		}
	};

	@Override
	public Response getResponse(String url, Map<String, String> params) {
		
		if (url.contains("DELETE")) {
			Array<String> path = new Array<String>();
			String[] splits = url.replace("/" + baseUrl, "").split("/");
			for (int i = 0; i < splits.length - 1; ++i)
				path.add(splits[i]);
			splits = new String[path.size];
			for (int i = 0; i < path.size; ++i)
				splits[i] = path.get(i);
			provider.deleteData(splits,params.get("NanoHttpd.QUERY_STRING"));
		}
		
		String[] path = url.replace("/" + baseUrl, "").split("/");
		
		if (params.size() > 1) {
			provider.setData(path, params);
		}
		
		Array<ScriptEditor.ObjectData> objs = new Array<ScriptEditor.ObjectData>();
		provider.getData(path, objs);
		objs.sort(sort);
		
		String html = FileContent.getHTML("script_objects.html");
		html = html.replace("${servername}",getServer().getName());
		html = html.replace("${navigation}",getServer().getNavigation(this));
		html = html.replace("${path}", url);
		html = html.replace("${prettyname}",prettyName);
		
		HTMLBuilder builder = new HTMLBuilder();
		for (ScriptEditor.ObjectData obj : objs) {
			builder.row();
				builder.td();
				if (obj.url != null && obj.url.length() > 0) {
					builder.link((baseUrl + obj.url).replace("//", "/")).add(obj.Name).pop();
				} else 
					builder.add(obj.Name);
				builder.pop();
				builder.td().add(obj.type).pop();
				
				if (obj.canEdit) {
					builder.td();
					builder.add("<input name='" + obj.Name + "' type='text' value='" + obj.Value + "' />");
					builder.add("<button type='button' class='btn btn-default' onClick=''><span class='glyphicon glyphicon-remove'></span></button>");
					builder.pop();
				} else {
					builder.td();
					builder.add(obj.Value);
					builder.add("<button type='button' class='btn btn-default' onClick=''><span class='glyphicon glyphicon-remove'></span></button>");
					builder.pop();
				}
			builder.pop();
		}
		
		html = html.replace("${url}", url);
		html = html.replace("${fields}",builder.toString());
		
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
		provider.search(query, baseUrl, results);
	}
	
	@Override
	public String getPrettyName() {
		return prettyName;
	}

	@Override
	public String getDoc() {
		return "Explore/manipulate the scripting VM on the remote machine";
	}
}
