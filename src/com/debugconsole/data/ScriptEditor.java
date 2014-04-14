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
	
	@Deprecated //this guy is going to go away
	public static class ScriptData {
		public Object parent;
		public Object self;
		public ObjectMap<String,Object> data = new ObjectMap<String,Object>();
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
	public interface ScriptProvider {
		public boolean compile(String code, Array<MessageData> messages);
		@Deprecated //going away soon
		public ScriptData getData(Object of);
		@Deprecated //going away soon
		public void setData(ScriptData sd);
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
		
		return new Response(html);
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
}
