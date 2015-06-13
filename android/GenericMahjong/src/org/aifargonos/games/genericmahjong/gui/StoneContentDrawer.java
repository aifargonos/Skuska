package org.aifargonos.games.genericmahjong.gui;

import org.aifargonos.games.genericmahjong.engine.StoneContent;

import android.graphics.Canvas;



public interface StoneContentDrawer {
	
	void draw(StoneContent sc, Canvas canvas, float left, float top, float right, float bottom);
	
}
