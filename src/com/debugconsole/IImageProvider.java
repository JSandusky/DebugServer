package com.debugconsole;

import com.badlogic.gdx.graphics.Pixmap;

/** Eventually classes that implement IImageProvider will get their images rendered in the object view */
//the primary use case for this is an overhead map of the current game state
//the secondary use case for this is Box2d/bullet debug
//the tertiary use case for this is using a FrameBuffered render of something - like an avatar
public interface IImageProvider {
	public Pixmap getWebImage();
}
