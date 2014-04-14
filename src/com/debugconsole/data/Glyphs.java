package com.debugconsole.data;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.debugconsole.HTMLBuilder;

public final class Glyphs {
	public static void getGlyp(String ext, HTMLBuilder builder) {
		if (ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg")) {
			builder.span("glyphicon glyphicon-picture").pop();
		} else if (ext.equalsIgnoreCase("wav")) {
			builder.span("glyphicon glyphicon-volume-up").pop();
		} else if (ext.equalsIgnoreCase("ogg") || ext.equalsIgnoreCase("mp3")) {
			builder.span("glyphicon glyphicon-music").pop();
		} else if (ext.equalsIgnoreCase("log")) {
			builder.span("glyphicon glyphicon-info-sign").pop();
		} else if (ext.equalsIgnoreCase("txt") || ext.equalsIgnoreCase("xml")) {
			builder.span("glyphicon glyphicon-file").pop();
		} else {
			builder.span("glyphicon glyphicon-save").pop();
		}
	}
	
	public static void getGlyph(Class c, HTMLBuilder builder) {
		if (c == Texture.class || c == TextureAtlas.class) {
			builder.span("glyphicon glyphicon-picture").pop();
		} else if (c == Sound.class) {
			builder.span("glyphicon glyphicon-volume-up").pop();
		} else if (c == Music.class) {
			builder.span("glyphicon glyphicon-music").pop();
		} else if (c == Skin.class) {
			builder.span("glyphicon glyphicon-picture").pop();
		}
	}
}
