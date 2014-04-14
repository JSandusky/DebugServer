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
		add("<br/>");
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
	
	public HTMLBuilder heading(int num) {
		return tag("<h" + num + ">","</h" + num + ">");
	}
	
	public HTMLBuilder heading(int num, String cssClass) {
		return tag("<h" + num + " class='" + cssClass + "'>","</h" + num + ">");
	}
	
	public HTMLBuilder table() {
		return tag("<table border='1'>","</table>");
	}
	
	public HTMLBuilder row() {
		return tag("<tr>","</tr>");
	}
	
	public HTMLBuilder row(String cssClass) {
		return tag("<tr class='" + cssClass + "'>","</tr>");
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
	
	public HTMLBuilder ul() {
		return tag("<ul>","</ul>");
	}
	
	public HTMLBuilder ol() {
		return tag("<ol>","</ol>");
	}
	
	public HTMLBuilder ul(String cssClass) {
		return tag("<ul class='" + cssClass + "'>", "</ul>");
	}
	
	public HTMLBuilder ol(String cssClass) {
		return tag("<ol class='" + cssClass + "'>", "</ol>");
	}
	
	public HTMLBuilder li() {
		return tag("<li>","</li>");
	}
	
	public HTMLBuilder li(String cssClass) {
		return tag("<li class='" + cssClass + "'>", "</li>");
	}
	
	public HTMLBuilder span(String cssClass) {
		return tag("<span class='" + cssClass + "'>","</span>");
	}
	
	public HTMLBuilder add(String txt) {
		string.append(txt);
		return this;
	}
	
	public HTMLBuilder link(String url) {
		return tag("<a href='" + url + "'>","</a>");
	}
	
	public HTMLBuilder div(String cssClass) {
		return tag("<div class='" + cssClass + "'>","</div>");
	}
	
	public HTMLBuilder tag(String open, String close) {
		string.append(open);
		popStack.add(close);
		return this;
	}
	
	public HTMLBuilder img(String path) {
		add("<img src='"+path +"'/>");
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
