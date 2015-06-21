package org.aifargonos.games.genericmahjong.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.aifargonos.games.genericmahjong.data.Coordinates;
import org.aifargonos.games.genericmahjong.engine.Board;
import org.aifargonos.games.genericmahjong.engine.Stone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;



/**
 * TODO [control] .: who will remove the stone from the board and who stoneView from boardView ??
 * 
 * @author aifargonos
 */
public class BoardView extends ScallingPanningView {
	
	
	
	public final Comparator<StoneView> VISIBILITY_COMPARATOR = new Comparator<StoneView>() {
		@Override
		public int compare(StoneView lhs, StoneView rhs) {
			
			final Coordinates c1 = lhs.getStone().getPosition();
			final Coordinates c2 = rhs.getStone().getPosition();
			
			switch(slant) {
			case StoneView.SLANT_SE_TO_NW:
				if(c1.z() < c2.z()) return -1;
				if(c1.z() > c2.z()) return 1;
				if(c1.x() + c1.y() < c2.x() + c2.y()) return -1;
				if(c1.x() + c1.y() > c2.x() + c2.y()) return 1;
				if(c1.x() < c2.x()) return -1;
				if(c1.x() > c2.x()) return 1;
				return 0;
				
			case StoneView.SLANT_NW_TO_SE:
				if(c1.z() < c2.z()) return -1;
				if(c1.z() > c2.z()) return 1;
				if(- c1.x() - c1.y() < - c2.x() - c2.y()) return -1;
				if(- c1.x() - c1.y() > - c2.x() - c2.y()) return 1;
				if(c1.x() < c2.x()) return -1;
				if(c1.x() > c2.x()) return 1;
				return 0;
				
			case StoneView.SLANT_SW_TO_NE:
				if(c1.z() < c2.z()) return -1;
				if(c1.z() > c2.z()) return 1;
				if(c1.y() - c1.x() < c2.y() - c2.x()) return -1;
				if(c1.y() - c1.x() > c2.y() - c2.x()) return 1;
				if(c1.x() < c2.x()) return -1;
				if(c1.x() > c2.x()) return 1;
				return 0;
			
			default:
				if(c1.z() < c2.z()) return -1;
				if(c1.z() > c2.z()) return 1;
				if(c1.x() - c1.y() < c2.x() - c2.y()) return -1;
				if(c1.x() - c1.y() > c2.x() - c2.y()) return 1;
				if(c1.x() < c2.x()) return -1;
				if(c1.x() > c2.x()) return 1;
				return 0;
			}
			
		}
	};
	
	
	
//	private int slant = StoneView.SLANT_NE_TO_SW;
	private int slant = StoneView.SLANT_NW_TO_SE;
//	private int slant = StoneView.SLANT_SE_TO_NW;
//	private int slant = StoneView.SLANT_SW_TO_NE;
	private float depthRatioX = 0.2f;
	private float depthRatioY = 0.15f;
	private float stoneWidth = 80;
	private float stoneWidthToHeightRatio = 0.75f;
	
	private PointF origin = new PointF(0f, 0f);
	
	private StoneView leftMost = null;
	private StoneView topMost = null;
	private StoneView rightMost = null;
	private StoneView bottomMost = null;
	
	private Board board;
	
	// TODO .: removing children! Should it adapt the clientArea ??
	
	/**
	 * This is just a temporary variable,
	 * in order to prevent allocation in methods that should be fast.
	 */
	private final RectF clientArea = new RectF();
	/**
	 * This is just a temporary variable,
	 * in order to prevent allocation in methods that should be fast.
	 */
	private final RectF tmpRect = new RectF();
	
	
	
	public BoardView(Context context) {
		this(context, null);
	}
	
	public BoardView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setBackgroundColor(Color.BLACK);
	}
	
	
	
	@Override
	public void addView(View child, int index, LayoutParams params) {
		
		if(child instanceof StoneView) {
			final StoneView stoneView = (StoneView)child;
			if(stoneView.getStone() != null) {// TODO .: This may be replaced with LayoutParams that contain Coordinates ;-)
				
				stoneView.setSlant(slant);
				stoneView.setDepthRatioX(depthRatioX);
				stoneView.setDepthRatioY(depthRatioY);
				
				final int newIndex = chooseIndex(stoneView);
				super.addView(child, newIndex, params);
			}
		}
		
	}
	
	@Override
	protected boolean addViewInLayout(View child, int index, LayoutParams params, boolean preventRequestLayout) {
		
		boolean ret = false;
		
		if(child instanceof StoneView) {
			StoneView stoneView = (StoneView)child;
			if(stoneView.getStone() != null) {
				
				stoneView.setSlant(slant);
				stoneView.setDepthRatioX(depthRatioX);
				stoneView.setDepthRatioY(depthRatioY);
				
				final int newIndex = chooseIndex(stoneView);
				ret = super.addViewInLayout(child, newIndex, params, preventRequestLayout);
			}
		}
		
		return ret;
	}
	
	private int chooseIndex(final StoneView newView) {
		return chooseIndex(newView, 0, getChildCount());
	}
	
	private int chooseIndex(final StoneView newView, final int start, final int end) {
		
		if(end <= start) {
			return start;
		}
		
		final int half = (start + end) / 2;
		
		final StoneView halfChild = (StoneView)getChildAt(half);// The cast is checked in addView.
		
		final int cmp = VISIBILITY_COMPARATOR.compare(newView, halfChild);
		
		if(cmp == 0) {
			return half;
		} else if(cmp < 0) {
			return chooseIndex(newView, start, half);
		} else {// cmp > 0
			return chooseIndex(newView, half + 1, end);
		}
	}
	
	public StoneView getStoneView(final Stone stone) {
		final int count = getChildCount();
		for(int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if(child instanceof StoneView) {
				final StoneView stoneView = (StoneView)child;
				
				if(stoneView.getStone() == stone) {
					return stoneView;
				}
				
			}
		}
		return null;
	}
	
	
	
	@Override
	protected void adaptClientAreaToNewChild(View child, RectF clientArea) {
		/* 
		 * Assuming the new child has the same size as all the others,
		 * check whether the new child doesn't fit into the clientArea and
		 * if so, extend it.
		 */
		
		StoneView stoneView = (StoneView)child;// The cast is checked in addView.
		
		/* 
		 * The new child doesn't fit into the clientArea when
		 * its bounds are greater than clientArea.
		 */
		final RectF bounds = new RectF();
		coordinatesToBounds(stoneView.getStone().getPosition(), bounds);
		
		if(getChildCount() == 1) {
			// If this is the first stone, set the bounds around it.
			
			origin.set(-bounds.left, -bounds.top);
			clientArea.set(0, 0, bounds.width(), bounds.height());
			
			leftMost = stoneView;
			topMost = stoneView;
			rightMost = stoneView;
			bottomMost = stoneView;
		} else {
			
			// If right or bottom is greater, just extend it.
			if(coordinatesToX(rightMost.getStone().getPosition()) < bounds.left) {
				clientArea.right = bounds.right;
				
				rightMost = stoneView;
			}
			if(coordinatesToY(bottomMost.getStone().getPosition()) < bounds.top) {
				clientArea.bottom = bounds.bottom;
				
				bottomMost = stoneView;
			}
			
			// If left or top is lower, move origin and right or bottom.
			if(bounds.left < coordinatesToX(leftMost.getStone().getPosition())) {
				clientArea.right += clientArea.left - bounds.left;
				origin.x += -bounds.left;
				
				leftMost = stoneView;
			}
			if(bounds.top < coordinatesToY(topMost.getStone().getPosition())) {
				clientArea.bottom += clientArea.top - bounds.top;
				origin.y += -bounds.top;
				
				topMost = stoneView;
			}
			
		}
		
	}
	
	
	
	@Override
	protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
		
		getClientArea(clientArea);
		
		/* 
		 * Measure how much space stones need (from mostLeft/Top to mostRight/Bottom)
		 * get ration of this and clientArea, and
		 * scale stoneWidth in this ration.
		 * TODO .: Then some centering may be needed. ??
		 */
		final float stonesWidth = coordinatesToX(rightMost.getStone().getPosition()) -
				coordinatesToX(leftMost.getStone().getPosition()) +
				getStoneWidth() + getStoneDepthX();
		final float stonesHeight = coordinatesToY(bottomMost.getStone().getPosition()) -
				coordinatesToY(topMost.getStone().getPosition()) +
				getStoneHeight() + getStoneDepthY();
		
		final float ratio = Math.min(clientArea.width() / stonesWidth,
				clientArea.height() / stonesHeight);
		
		stoneWidth *= ratio;
		origin.x *= ratio;
		origin.y *= ratio;
		
		/* 
		 * Finally, measure all the children.
		 */
		final int widthSpec = MeasureSpec.makeMeasureSpec(
				(int)(getStoneWidth() + getStoneDepthX() + 0.5f),
				MeasureSpec.EXACTLY);
		final int heightSpec = MeasureSpec.makeMeasureSpec(
				(int)(getStoneHeight() + getStoneDepthY() + 0.5f),
				MeasureSpec.EXACTLY);
		
		final int count = getChildCount();
		for(int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if(child.getVisibility() != GONE) {
				child.measure(widthSpec, heightSpec);
			}
		}
		
	}
	
	
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		/* 
		 * Layout each child into its bounds.
		 */
		final int count = getChildCount();
		for(int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if(child.getVisibility() != GONE) {
				StoneView stoneView = (StoneView)child;
				
				coordinatesToBounds((stoneView).getStone().getPosition(), tmpRect);
				child.layout(
						(int)(tmpRect.left + 0.5f),
						(int)(tmpRect.top + 0.5f),
						(int)(tmpRect.right + 0.5f),
						(int)(tmpRect.bottom + 0.5f));
//				TODO .: DEBUG
//				if(isCovered(stoneView.getStone())/* || isOutOfViewPort(tmpRect)*/) {
//					stoneView.setVisibility(INVISIBLE);
//				} else {
//					stoneView.setVisibility(VISIBLE);
//				}
				
			}
		}
		
	}
	
	private boolean isOutOfViewPort(final RectF rect) {
		
		final int scrollX = getScrollX();
		final int scrollY = getScrollY();
		final int width = getWidth();
		final int height = getHeight();
		
		return
				rect.right < scrollX ||
				rect.bottom < scrollY ||
				rect.left > scrollX + width ||
				rect.top > scrollY + height;
	}
	
	private boolean isCovered(final Stone stone) {
		
		final boolean coveredFromAbove =
				board.hasNeighbour(stone, Coordinates.ANE) &&
				board.hasNeighbour(stone, Coordinates.ANW) &&
				board.hasNeighbour(stone, Coordinates.ASE) &&
				board.hasNeighbour(stone, Coordinates.ASW);
		
		final boolean coveredFromHorizontalSide;
		if(slant == StoneView.SLANT_NE_TO_SW || slant == StoneView.SLANT_SE_TO_NW) {
			coveredFromHorizontalSide = 
					board.hasNeighbour(stone, Coordinates.EN) &&
					board.hasNeighbour(stone, Coordinates.ES);
		} else {
			coveredFromHorizontalSide = 
					board.hasNeighbour(stone, Coordinates.WN) &&
					board.hasNeighbour(stone, Coordinates.WS);
		}
		
		final boolean coveredFromVerticalSide;
		if(slant == StoneView.SLANT_NE_TO_SW || slant == StoneView.SLANT_NW_TO_SE) {
			coveredFromVerticalSide = 
					board.hasNeighbour(stone, Coordinates.NE) &&
					board.hasNeighbour(stone, Coordinates.NW);
		} else {
			coveredFromVerticalSide = 
					board.hasNeighbour(stone, Coordinates.SE) &&
					board.hasNeighbour(stone, Coordinates.SW);
		}
		
		return coveredFromAbove && coveredFromHorizontalSide && coveredFromVerticalSide;
	}
	
	
	
	public float getStoneWidth() {
		return stoneWidth;
	}
	
	public float getStoneHeight() {
		return stoneWidth / stoneWidthToHeightRatio;
	}
	
	public float getStoneDepthX() {
//		cw + d = w
//		d = w * r
//		w = d / r
//		cw + d = d / r
//		cw = d / r - d
//		cw*r = d - d*r
//		cw*r = d * (1 - r)
//		d = cw*r / (1 - r)
		return stoneWidth * depthRatioX / (1 - depthRatioX);
		// TODO .: I may need to count with stroke width of the stone here !!!
	}
	
	public float getStoneDepthY() {
		return (stoneWidth / stoneWidthToHeightRatio) * depthRatioY / (1 - depthRatioY);
	}
	
	
	
	private float coordinatesToX(Coordinates c) {
		final float width = getStoneWidth();
		final float depthX = getStoneDepthX();
		final int xDepthCorrection = (slant == StoneView.SLANT_SE_TO_NW || slant == StoneView.SLANT_NE_TO_SW) ? -(c.z() + 1) : c.z();
		return origin.x + c.x()*width/2 + xDepthCorrection * depthX;
	}
	
	private float coordinatesToY(Coordinates c) {
		final float height = getStoneHeight();
		final float depthY = getStoneDepthY();
		final int yDepthCorrection = (slant == StoneView.SLANT_SE_TO_NW || slant == StoneView.SLANT_SW_TO_NE) ? -(c.z() + 1) : c.z();
		return origin.y + c.y()*height/2 + yDepthCorrection * depthY;
	}
	
	private RectF coordinatesToBounds(Coordinates c, RectF ret) {
		
		final float width = getStoneWidth();
		final float height = getStoneHeight();
		final float depthX = getStoneDepthX();
		final float depthY = getStoneDepthY();
		
		final float left = coordinatesToX(c);
		final float top = coordinatesToY(c);
		
		ret.set(left, top,
				left + width + depthX,
				top + height + depthY);
		
		return ret;
	}
	
	
	
	public void setBoard(final Board board) {
		this.board = board;
		resetStoneViews();
	}
	
	public void resetStoneViews() {
		
		removeAllViews();
		
		// TODO .: reset clientArea
		
		for(Stone stone : board.getStones()) {
			StoneView sv = new StoneView(getContext());
			sv.setStone(stone);
			addView(sv);
		}
//		
//		// This is an old optimization. Draw caching in ScallingPanningView optimizes instead.
//		/* TODO [drawing_in_BoardView]
//		 * From here on these are just temporary things for
//		 * trying out drawing everything together in BoardView.
//		 */
//		
//		// Storing stones ordered according to slant.
//		stones = new ArrayList<Stone>(board.size());
//		final int count = getChildCount();
//		for(int i = 0; i < count; i++) {
//			final Stone stone = ((StoneView)getChildAt(i)).getStone();
//			if(!isCovered(stone)) {
//				stones.add(stone);
//			}
//		}
//		
//		// Removing all the views
//		// .. they were added only to order the stones
//		// and enlarge the clientArea
//		removeAllViews();
//		
//		setWillNotDraw(false);
//		
//		outlinePaint.setStrokeWidth(3);// TODO .: maybe this can be loaded from some xml ..
//		outlinePaint.setStyle(Paint.Style.STROKE);
//		outlinePaint.setColor(Color.BLACK);
//		outlinePaint.setAntiAlias(true);
//		
//		sideXPaint.setStyle(Paint.Style.FILL);
//		sideXPaint.setColor(Color.GRAY);
//		sideXPaint.setAntiAlias(true);
//		
//		sideYPaint.setStyle(Paint.Style.FILL);
//		sideYPaint.setColor(Color.DKGRAY);
//		sideYPaint.setAntiAlias(true);
		
	}
//	
//	// This is an old optimization. Draw caching in ScallingPanningView optimizes instead.
//	private List<Stone> stones;
//	
//	final private Paint outlinePaint = new Paint();
//	final private Paint sideXPaint = new Paint();
//	final private Paint sideYPaint = new Paint();
//	
//	final private Path outline = new Path();// TODO .: maybe these paths can be loaded from some xml ..
//	final private Path sideX = new Path();
//	final private Path sideY = new Path();
//	
//	@Override
//	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
//		
////		Log.i("BoardView.onDraw", "canvas.isHardwareAccelerated() = " + canvas.isHardwareAccelerated());// DEBUG
//		
//		for(Stone stone : stones) {
//			
//			coordinatesToBounds(stone.getPosition(), tmpRect);
//			
////			if(isCovered(stone) || isOutOfViewPort(tmpRect)) {
//			// No need to check isCovered(stone), because
//			// stones contains only not covered stones ;-)
////			if(isOutOfViewPort(tmpRect)) {
////				continue;
////			}
//			
//			// If at least some part of the stone is visible ...
//			
//			final float halfStroke = outlinePaint.getStrokeWidth() / 2;
//			// TODO .: all this stroke business :. setClipChildren(clipChildren)
//			final float left = tmpRect.left + halfStroke;
//			final float top = tmpRect.top + halfStroke;
//			final float right = tmpRect.right - halfStroke;
//			final float bottom = tmpRect.bottom - halfStroke;
//			final float depthX = (right - left) * depthRatioX;
//			final float depthY = (top - bottom) * depthRatioY;
//			
//			// Draw the borders.
//			
//			prepareSides(left, top, right, bottom, depthX, depthY);
//			canvas.drawPath(sideX, sideXPaint);
//			canvas.drawPath(sideY, sideYPaint);
//			
//			prepareOutline(left, top, right, bottom, depthX, depthY);
//			canvas.drawPath(outline, outlinePaint);
//			
//			// TODO .: content !!!
//			
//		}
//		
//	}
//	
//	private void prepareOutline(final float l, final float t, final float r, final float b,
//			final float depthX, final float depthY) {
//		outline.rewind();
//		outline.incReserve(6);
//		
//		switch(slant) {
//		case StoneView.SLANT_SE_TO_NW:
//		case StoneView.SLANT_NW_TO_SE:
//			/* 
//			 * 1---2
//			 * |    \
//			 * |     3
//			 * 6     |
//			 *  \    |
//			 *   5---4
//			 */
//			outline.moveTo(l, t);
//			outline.lineTo(r - depthX, t);
//			outline.lineTo(r, t - depthY);
//			outline.lineTo(r, b);
//			outline.lineTo(l + depthX, b);
//			outline.lineTo(l, b + depthY);
//			outline.close();
//			break;
//		
//		default:
//			/* 
//			 *   6---1
//			 *  /    |
//			 * 5     |
//			 * |     2
//			 * |    /
//			 * 4---3
//			 */
//			outline.moveTo(r, t);
//			outline.lineTo(r, b + depthY);
//			outline.lineTo(r - depthX, b);
//			outline.lineTo(l, b);
//			outline.lineTo(l, t - depthY);
//			outline.lineTo(l + depthX, t);
//			outline.close();
//			break;
//		}
//		
//	}
//	
//	private void prepareSides(final float l, final float t, final float r, final float b,
//			final float depthX, final float depthY) {
//		sideX.rewind();
//		sideX.incReserve(4);
//		sideY.rewind();
//		sideY.incReserve(4);
//		
//		switch(slant) {
//		case StoneView.SLANT_SE_TO_NW:
//			/* 
//			 *     1
//			 *     |\
//			 *     | 2
//			 *     4 |
//			 *      \|
//			 *       3
//			 */
//			sideX.moveTo(r - depthX, t);
//			sideX.lineTo(r, t - depthY);
//			sideX.lineTo(r, b);
//			sideX.lineTo(r - depthX, b + depthY);
//			sideX.close();
//			/* 
//			 * 
//			 * 
//			 * 
//			 * 1---2
//			 *  \   \
//			 *   4---3
//			 */
//			sideY.moveTo(l, b + depthY);
//			sideY.lineTo(r - depthX, b + depthY);
//			sideY.lineTo(r, b);
//			sideY.lineTo(l + depthX, b);
//			sideY.close();
//			break;
//			
//		case StoneView.SLANT_NW_TO_SE:
//			/* 
//			 * 1
//			 * |\
//			 * | 2
//			 * 4 |
//			 *  \|
//			 *   3
//			 */
//			sideX.moveTo(l, t);
//			sideX.lineTo(l + depthX, t - depthY);
//			sideX.lineTo(l + depthX, b);
//			sideX.lineTo(l, b + depthY);
//			sideX.close();
//			/* 
//			 * 1---2
//			 *  \   \
//			 *   4---3
//			 * 
//			 * 
//			 * 
//			 */
//			sideY.moveTo(l, t);
//			sideY.lineTo(r - depthX, t);
//			sideY.lineTo(r, t - depthY);
//			sideY.lineTo(l + depthX, t - depthY);
//			sideY.close();
//			break;
//			
//		case StoneView.SLANT_SW_TO_NE:
//			/* 
//			 *   1
//			 *  /|
//			 * 4 |
//			 * | 2
//			 * |/
//			 * 3
//			 */
//			sideX.moveTo(l + depthX, t);
//			sideX.lineTo(l + depthX, b + depthY);
//			sideX.lineTo(l, b);
//			sideX.lineTo(l, t - depthY);
//			sideX.close();
//			/* 
//			 * 
//			 * 
//			 * 
//			 *   4---1
//			 *  /   /
//			 * 3---2
//			 */
//			sideY.moveTo(r, b + depthY);
//			sideY.lineTo(r - depthX, b);
//			sideY.lineTo(l, b);
//			sideY.lineTo(l + depthX, b + depthY);
//			sideY.close();
//			break;
//		
//		default:
//			/* 
//			 *       1
//			 *      /|
//			 *     4 |
//			 *     | 2
//			 *     |/
//			 *     3
//			 */
//			sideX.moveTo(r, t);
//			sideX.lineTo(r, b + depthY);
//			sideX.lineTo(r - depthX, b);
//			sideX.lineTo(r - depthX, t - depthY);
//			sideX.close();
//			/* 
//			 *   4---1
//			 *  /   /
//			 * 3---2
//			 * 
//			 * 
//			 * 
//			 */
//			sideY.moveTo(r, t);
//			sideY.lineTo(r - depthX, t - depthY);
//			sideY.lineTo(l, t - depthY);
//			sideY.lineTo(l + depthX, t);
//			sideY.close();
//			break;
//		}
//		
//	}
	
	
	
}
