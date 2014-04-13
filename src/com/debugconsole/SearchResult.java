package com.debugconsole;

public class SearchResult {
	public String url;
	public String display;
	public String kind;
	
	public SearchResult(String kind, String disp, String url) {
		this.url = url;
		this.display = disp;
		this.kind = kind;
	}
}
