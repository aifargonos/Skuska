package org.aifargonos.games.genericmahjong.gui;

import org.aifargonos.games.genericmahjong.engine.DictStoneContent;
import org.aifargonos.games.genericmahjong.engine.StoneContent;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;



public class DictStoneContentDrawer implements StoneContentDrawer {
//	
//	public final Paint debugPaint = new Paint();
	
	public final Paint textPaint = new Paint();
	private final Rect textBounds = new Rect();
	
	public DictStoneContentDrawer() {
//		debugPaint.setARGB(255, 0, 255, 255);
//		debugPaint.setStyle(Paint.Style.STROKE);
		textPaint.setARGB(255, 0, 0, 0);
		textPaint.setAntiAlias(true);
	}
	
	@Override
	public void draw(final StoneContent sc, final Canvas canvas,
			final float l, final float t, final float r, final float b) {
		if(!(sc instanceof DictStoneContent)) {
			throw new IllegalArgumentException(getClass().getName() + " can draw only " + DictStoneContent.class.getName());
		}
		DictStoneContent dsc = (DictStoneContent)sc;
		/* 
		 * 	draw the text so that it just fits into the bounds and is centered
		 */
		
		final float spaceWidth = Math.abs(r-l);
		final float spaceHeight = Math.abs(b-t);
		
		textPaint.setTextSize(100);// TODO .: magic number !!
		
		textPaint.getTextBounds(dsc.text, 0, dsc.text.length(), textBounds);
		final float scale;
		if(textBounds.width() / ((float)textBounds.height()) > spaceWidth / spaceHeight) {
			// the text is more wide than the space
			scale = spaceWidth / textBounds.width();
		} else {
			// the space is more wide than the text
			scale = spaceHeight / textBounds.height();
		}
		
		textPaint.setTextSize(textPaint.getTextSize() * scale);
		
		textPaint.getTextBounds(dsc.text, 0, dsc.text.length(), textBounds);
//		canvas.drawRect(
//				l + (spaceWidth-textBounds.width())/2,
//				b - textBounds.height() - (spaceHeight-textBounds.height())/2,
//				l + textBounds.width() + (spaceWidth-textBounds.width())/2,
//				b - (spaceHeight-textBounds.height())/2,
//				debugPaint);
		
		canvas.drawText(dsc.text, l + (spaceWidth-textBounds.width())/2, b - (spaceHeight-textBounds.height())/2, textPaint);
		
		// TODO [dict sc drawing] does not work with letters like 'j' or 'p'!
	}
	
}
