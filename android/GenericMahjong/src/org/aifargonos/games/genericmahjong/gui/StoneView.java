package org.aifargonos.games.genericmahjong.gui;

import org.aifargonos.games.genericmahjong.MainActivity;
import org.aifargonos.games.genericmahjong.engine.Stone;
import org.aifargonos.games.genericmahjong.engine.StoneContent;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;



public class StoneView extends View {
	
	
	
	/**<pre>
	 * +---+
	 * |   |\
	 * |   | +
	 * +---+ |
	 *  \   \|
	 *   +---+</pre>
	 */
	public static final int SLANT_SE_TO_NW = 1;
	/**<pre>
	 * +---+
	 * |\   \
	 * | +---+
	 * + |   |
	 *  \|   |
	 *   +---+</pre>
	 */
	public static final int SLANT_NW_TO_SE = 2;
	/**<pre>
	 *   +---+
	 *  /|   |
	 * + |   |
	 * | +---+
	 * |/   /
	 * +---+</pre>
	 */
	public static final int SLANT_SW_TO_NE = 3;
	/**<pre>
	 *   +---+
	 *  /   /|
	 * +---+ |
	 * |   | +
	 * |   |/
	 * +---+</pre>
	 */
	public static final int SLANT_NE_TO_SW = 4;
	
	
	
	private Stone stone;
	
	//	private int slant = SLANT_SE_TO_NW;
	private int slant = SLANT_NE_TO_SW;
	private float depthRatioX = 0.2f;
	private float depthRatioY = 0.15f;
	
	private final Paint outlinePaint = new Paint();
	private final Paint sideXPaint = new Paint();
	private final Paint sideYPaint = new Paint();
	private final Paint contentPaint = new Paint();
	
	private final Path outline = new Path();// TODO .: maybe these paths can be loaded from some xml ..
	private final Path sideX = new Path();
	private final Path sideY = new Path();
	private final RectF contentRect = new RectF();
	
	
	
	public StoneView(Context context) {
		this(context, null);
	}
	
	public StoneView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public StoneView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		outlinePaint.setStrokeWidth(3);// TODO .: maybe this can be loaded from some xml ..
		outlinePaint.setStyle(Paint.Style.STROKE);
		outlinePaint.setColor(Color.BLACK);
		outlinePaint.setAntiAlias(true);
		
		sideXPaint.setStyle(Paint.Style.FILL);
		sideXPaint.setColor(Color.GRAY);
		sideXPaint.setAntiAlias(true);
		
		sideYPaint.setStyle(Paint.Style.FILL);
		sideYPaint.setColor(Color.DKGRAY);
		sideYPaint.setAntiAlias(true);
		
		contentPaint.setStyle(Paint.Style.FILL);
		contentPaint.setColor(Color.LTGRAY);
		contentPaint.setAntiAlias(true);
		
//		updatePaths();
		
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(stone != null) {
					stone.click();
					invalidate();
				}
			}
		});
		
	}
	
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		final float halfStroke = outlinePaint.getStrokeWidth() / 2;
		// TODO .: all this stroke business :. setClipChildren(clipChildren)
		final float left = getPaddingLeft() + halfStroke;
		final float top = getPaddingTop() + halfStroke;
		final float right = getWidth() - getPaddingRight() - halfStroke;
		final float bottom = getHeight() - getPaddingBottom() - halfStroke;
		final float depthX = (right - left) * depthRatioX;
		final float depthY = (top - bottom) * depthRatioY;
		
		// Draw the borders.
		
		prepareSides(left, top, right, bottom, depthX, depthY);
		canvas.drawPath(sideX, sideXPaint);
		canvas.drawPath(sideY, sideYPaint);
		
		prepareOutline(left, top, right, bottom, depthX, depthY);
		canvas.drawPath(outline, outlinePaint);
		
		if(stone != null) {
			
			// Draw selection.
			
			final int oldContentColor = contentPaint.getColor();
			if(stone.isSelected()) {
				contentPaint.setColor(Color.GREEN);
			}
			canvas.drawRect(contentRect, contentPaint);
			contentPaint.setColor(oldContentColor);
			
			// Draw the content.
			
			final StoneContent stoneContent = stone.getContent();
			if(stoneContent != null) {
				final StoneContentDrawer stoneContentDrawer = MainActivity.STONE_CONTENT_DRAWERS.get(stoneContent.getClass());
				if(stoneContentDrawer == null) {
					throw new IllegalStateException("No " + StoneContentDrawer.class.getName() + " for " + stoneContent.getClass());
				}
				stoneContentDrawer.draw(stoneContent, canvas, contentRect.left, contentRect.top, contentRect.right, contentRect.bottom);
			}
			
		}
		
	}
	
	private void prepareOutline(final float l, final float t, final float r, final float b,
			final float depthX, final float depthY) {
		outline.rewind();
		outline.incReserve(6);
		
		switch(slant) {
		case SLANT_SE_TO_NW:
		case SLANT_NW_TO_SE:
			/* 
			 * 1---2
			 * |    \
			 * |     3
			 * 6     |
			 *  \    |
			 *   5---4
			 */
			outline.moveTo(l, t);
			outline.lineTo(r - depthX, t);
			outline.lineTo(r, t - depthY);
			outline.lineTo(r, b);
			outline.lineTo(l + depthX, b);
			outline.lineTo(l, b + depthY);
			outline.close();
			break;
		
		default:
			/* 
			 *   6---1
			 *  /    |
			 * 5     |
			 * |     2
			 * |    /
			 * 4---3
			 */
			outline.moveTo(r, t);
			outline.lineTo(r, b + depthY);
			outline.lineTo(r - depthX, b);
			outline.lineTo(l, b);
			outline.lineTo(l, t - depthY);
			outline.lineTo(l + depthX, t);
			outline.close();
			break;
		}
		
	}
	
	private void prepareSides(final float l, final float t, final float r, final float b,
			final float depthX, final float depthY) {
		sideX.rewind();
		sideX.incReserve(4);
		sideY.rewind();
		sideY.incReserve(4);
		
		switch(slant) {
		case SLANT_SE_TO_NW:
			contentRect.left = l;
			contentRect.top = t;
			contentRect.right = r - depthX;
			contentRect.bottom = b + depthY;
			/* 
			 *     1
			 *     |\
			 *     | 2
			 *     4 |
			 *      \|
			 *       3
			 */
			sideX.moveTo(r - depthX, t);
			sideX.lineTo(r, t - depthY);
			sideX.lineTo(r, b);
			sideX.lineTo(r - depthX, b + depthY);
			sideX.close();
			/* 
			 * 
			 * 
			 * 
			 * 1---2
			 *  \   \
			 *   4---3
			 */
			sideY.moveTo(l, b + depthY);
			sideY.lineTo(r - depthX, b + depthY);
			sideY.lineTo(r, b);
			sideY.lineTo(l + depthX, b);
			sideY.close();
			break;
			
		case SLANT_NW_TO_SE:
			contentRect.left = l + depthX;
			contentRect.top = t - depthY;
			contentRect.right = r;
			contentRect.bottom = b;
			/* 
			 * 1
			 * |\
			 * | 2
			 * 4 |
			 *  \|
			 *   3
			 */
			sideX.moveTo(l, t);
			sideX.lineTo(l + depthX, t - depthY);
			sideX.lineTo(l + depthX, b);
			sideX.lineTo(l, b + depthY);
			sideX.close();
			/* 
			 * 1---2
			 *  \   \
			 *   4---3
			 * 
			 * 
			 * 
			 */
			sideY.moveTo(l, t);
			sideY.lineTo(r - depthX, t);
			sideY.lineTo(r, t - depthY);
			sideY.lineTo(l + depthX, t - depthY);
			sideY.close();
			break;
			
		case SLANT_SW_TO_NE:
			contentRect.left = l + depthX;
			contentRect.top = t;
			contentRect.right = r;
			contentRect.bottom = b + depthY;
			/* 
			 *   1
			 *  /|
			 * 4 |
			 * | 2
			 * |/
			 * 3
			 */
			sideX.moveTo(l + depthX, t);
			sideX.lineTo(l + depthX, b + depthY);
			sideX.lineTo(l, b);
			sideX.lineTo(l, t - depthY);
			sideX.close();
			/* 
			 * 
			 * 
			 * 
			 *   4---1
			 *  /   /
			 * 3---2
			 */
			sideY.moveTo(r, b + depthY);
			sideY.lineTo(r - depthX, b);
			sideY.lineTo(l, b);
			sideY.lineTo(l + depthX, b + depthY);
			sideY.close();
			break;
		
		default: // SLANT_NE_TO_SW
			contentRect.left = l;
			contentRect.top = t - depthY;
			contentRect.right = r - depthX;
			contentRect.bottom = b;
			/* 
			 *       1
			 *      /|
			 *     4 |
			 *     | 2
			 *     |/
			 *     3
			 */
			sideX.moveTo(r, t);
			sideX.lineTo(r, b + depthY);
			sideX.lineTo(r - depthX, b);
			sideX.lineTo(r - depthX, t - depthY);
			sideX.close();
			/* 
			 *   4---1
			 *  /   /
			 * 3---2
			 * 
			 * 
			 * 
			 */
			sideY.moveTo(r, t);
			sideY.lineTo(r - depthX, t - depthY);
			sideY.lineTo(l, t - depthY);
			sideY.lineTo(l + depthX, t);
			sideY.close();
			break;
		}
		
	}
//	
//	private void updatePaths() {
//		
//		final float halfStroke = outlinePaint.getStrokeWidth() / 2;
//		// TODO .: all this stroke business :. setClipChildren(clipChildren)
//		final float left = getPaddingLeft() + halfStroke;
//		final float top = getPaddingTop() + halfStroke;
//		final float right = getWidth() - getPaddingRight() - halfStroke;
//		final float bottom = getHeight() - getPaddingBottom() - halfStroke;
//		final float depthX = (right - left) * depthRatioX;
//		final float depthY = (top - bottom) * depthRatioY;
//		
//		prepareSides(left, top, right, bottom, depthX, depthY);
//		
//		prepareOutline(left, top, right, bottom, depthX, depthY);
//		
//	}
//	
//	@Override
//	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//		updatePaths();
//		super.onSizeChanged(w, h, oldw, oldh);
//	}
//	
//	@Override
//	public void setPadding(int left, int top, int right, int bottom) {
//		if(outlinePaint != null) {// TODO .: this is weird :-P
//			updatePaths();
//		}
//		super.setPadding(left, top, right, bottom);
//	}
	
	
	
	public Stone getStone() {
		return stone;
	}
	
	public void setStone(Stone stone) {
		this.stone = stone;
	}
	
	
	public int getSlant() {
		return slant;
	}
	
	public void setSlant(final int slant) {
		this.slant = slant;
	}
	
	
	public float getDepthRatioX() {
		return depthRatioX;
	}
	
	public void setDepthRatioX(final float depthRatioX) {
		this.depthRatioX = depthRatioX;
	}
	
	public float getDepthRatioY() {
		return depthRatioY;
	}
	
	public void setDepthRatioY(final float depthRatioY) {
		this.depthRatioY = depthRatioY;
	}
	
	
	
}
