package com.debugconsole;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

public class ViewState extends ObjectMap<String,String> {
	public String write() {
		Json json = new Json();
		return json.toJson(this);
	}
	public void read(String data) {
		Json json = new Json();
		ObjectMap<String,String> in = json.fromJson(ObjectMap.class, data);
		for (ObjectMap.Entry<String, String> entry : in.entries()) {
			this.put(entry.key, entry.value);
		}
	}
}
