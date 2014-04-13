package com.debugconsole;

import com.badlogic.gdx.utils.Array;

public class HTMLBuilder {
	StringBuilder string = new StringBuilder();
	Array<String> popStack = new Array<String>();
	
	public HTMLBuilder() {
		
	}
	
	public String toString() {
		return string.toString();
	}
	
	public HTMLBuilder line() {
		string.append("<br/>");
		return this;
	}
	
	public HTMLBuilder html() {
		return tag("<html>","</html>");
	}
	
	public HTMLBuilder body() {
		return tag("<body>","</body>");
	}
	
	public HTMLBuilder head() {
		return tag("<head>","</head>");
	}
	
	public HTMLBuilder title() {
		return tag("<title>","</title>");
	}
	
	public HTMLBuilder h3() {
		return tag("<h3>","</h3>");
	}
	
	public HTMLBuilder table() {
		return tag("<table border='1'>","</table>");
	}
	
	public HTMLBuilder row() {
		return tag("<tr>","</tr>");
	}
	
	public HTMLBuilder row(String color) {
		return tag("<tr bgcolor='" + color + "'>","</tr>");
	}
	
	public HTMLBuilder color(String color) {
		return tag("<font color='" + color + "'>","</font>");
	}
	
	public HTMLBuilder td() {
		return tag("<td valign='top'>","</td>");
	}
	
	public HTMLBuilder td(int colSpan) {
		return tag("<td colspan='" + colSpan + " valign='top''>","</td>");
	}
	
	public HTMLBuilder bold() {
		return tag("<b>","</b>");
	}
	
	public HTMLBuilder italic() {
		return tag("<i>","</i>");
	}
	
	public HTMLBuilder underline() {
		return tag("<u>","</u>");
	}
	
	public HTMLBuilder add(String txt) {
		string.append(txt);
		return this;
	}
	
	public HTMLBuilder link(String url) {
		return tag("<a href='" + url + "'>","</a>");
	}
	
	HTMLBuilder tag(String open, String close) {
		string.append(open);
		popStack.add(close);
		return this;
	}
	
	public HTMLBuilder pop() {
		if (popStack.size > 0) {
			string.append(popStack.get(popStack.size-1));
			popStack.removeIndex(popStack.size-1);
		}
		return this;
	}
}
