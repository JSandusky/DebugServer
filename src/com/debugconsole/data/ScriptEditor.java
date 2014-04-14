package com.debugconsole.data;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.debugconsole.DebugServer;
import com.debugconsole.HTMLBuilder;
import com.debugconsole.HTTPContent;
import com.debugconsole.NanoHTTPD.Response;
import com.debugconsole.SearchResult;

/** ace based script editor */
public class ScriptEditor extends HTTPContent {
	ScriptProvider provider;
	String baseUrl;
	String prettyName;
	String scriptPath;
	
	public ScriptEditor(DebugServer server, ScriptProvider provider, String prettyName, String baseUrl, String baseScriptPath) {
		super(server);
		this.provider = provider;
		scriptPath = baseScriptPath;
		this.prettyName = prettyName;
		this.baseUrl = baseUrl;
	}
	
	public enum MessageKind {
		Info,
		Error,
		Warning
	}
	
	public static class MessageData {
		public int line;
		public MessageKind kind;
		public String message;
		
		public MessageData(int line, MessageKind kind, String txt) {
			message = txt;
			this.line = line;
			this.kind = kind;
		}
	}
	
	public static class ObjectData {
		public String Name;
		public String Value;
		public String url;
		public String type;
		public boolean canEdit;
		
		public ObjectData(String nm, String type, String val, String url, boolean canEdit) {
			this.Name = nm;
			this.type = type;
			this.Value = val;
			this.url = url;
			this.canEdit = canEdit;
		}
	}
	
	public interface ScriptProvider {
		public boolean compile(String code, Array<MessageData> messages);
		public void getData(String[] path, Array<ObjectData> holder);
		public void setData(String[] path, Map<String,String> values);
		public void deleteData(String[] path, String deleteName);
		public void search(String[] query, String url, Array<SearchResult> results);
	}
	
	@Override
	public boolean validFor(String url) {
		return url.indexOf("/" + baseUrl) == 0;
	}
	@Override
	public Response getResponse(String url, Map<String, String> params) {
		Array<MessageData> msgs = new Array<MessageData>();
		if (params.containsKey("SCRIPT_CODE")) {
			provider.compile(params.get("SCRIPT_CODE"), msgs);
		}
		
		String html = FileContent.getHTML("script.html");
		html = html.replace("${navigation}", getServer().getNavigation(this));
		html = html.replace("${servername}", getServer().getName());
		
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
	
	Array<FileHandle> getScriptFiles() {
		Array<FileHandle> ret = new Array<FileHandle>();
		FileHandle cur = Gdx.files.local(scriptPath);
		fill(cur,ret);
		return ret;
	}
	
	void fill(FileHandle dir, Array<FileHandle> holder) {
		for (FileHandle fh : dir.list()) {
			if (fh.isDirectory())
				fill(dir,holder);
			else
				holder.add(fh);
		}
	}
	
	@Override
	public String getPrettyName() {
		return prettyName;
	}

	@Override
	public String getDoc() {
		return "Write and execute scripts on the remote machine";
	}
}
