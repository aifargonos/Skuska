package org.aifargonos.games.genericmahjong.gui;

import java.util.Comparator;

import org.aifargonos.games.genericmahjong.data.Coordinates;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;



public class BoardView extends ScallingPanningView {
	
	
	
	private int stoneSlant = StoneView.SLANT_NE_TO_SW;
	private float stoneDepthRatioX = 0.2f;
	private float stoneDepthRatioY = 0.15f;
	private float stoneWidth = 80;
	private float stoneWidthToHeightRatio = 0.75f;
	
	private Point origin = new Point(0, 0);
	
	private StoneView leftMost = null;
	private StoneView topMost = null;
	private StoneView rightMost = null;
	private StoneView bottomMost = null;
	// TODO .: removing views !!!
	
	private final Comparator<StoneView> horizontalComparator = new Comparator<StoneView>() {
		@Override
		public int compare(StoneView lhs, StoneView rhs) {
			return Float.compare(
					coordinatesToX(lhs.getStone().getPosition()),
					coordinatesToX(rhs.getStone().getPosition())
				);
		}
	};
	// TODO .: these comparators are probably overkill !!
	private final Comparator<StoneView> verticalComparator = new Comparator<StoneView>() {
		@Override
		public int compare(StoneView lhs, StoneView rhs) {
			return Float.compare(
					coordinatesToY(lhs.getStone().getPosition()),
					coordinatesToY(rhs.getStone().getPosition())
				);
		}
	};
	
	/**
	 * This is just a temporary variable,
	 * in order to prevent allocation in methods that should be fast.
	 */
	private final RectF clientArea = new RectF();
	/**
	 * This is just a temporary variable,
	 * in order to prevent allocation in methods that should be fast.
	 */
	private final Rect tmpRect = new Rect();
	
	
	
	public BoardView(Context context) {
		this(context, null);
	}
	
	public BoardView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	
	
	@Override
	public void addView(View child, int index, LayoutParams params) {
		
		if(child instanceof StoneView) {
			StoneView stoneView = (StoneView)child;
			if(stoneView.getStone() != null) {// TODO .: This may be replaced with LayoutParams that contain Coordinates ;-)
				
				stoneView.setSlant(stoneSlant);
				stoneView.setDepthRatioX(stoneDepthRatioX);
				stoneView.setDepthRatioY(stoneDepthRatioY);
				
				super.addView(child, index, params);
			}
		}
		
	}
	
	@Override
	protected boolean addViewInLayout(View child, int index, LayoutParams params, boolean preventRequestLayout) {
		
		boolean ret = false;
		
		if(child instanceof StoneView) {
			StoneView stoneView = (StoneView)child;
			if(stoneView.getStone() != null) {
				
				stoneView.setSlant(stoneSlant);
				stoneView.setDepthRatioX(stoneDepthRatioX);
				stoneView.setDepthRatioY(stoneDepthRatioY);
				
				ret = super.addViewInLayout(child, index, params, preventRequestLayout);
			}
		}
		
		return ret;
	}
	
	
	
	@Override
	protected void adaptClientAreaToNewChild(View child, RectF clientArea) {
		/* TEST .:
		 * Assuming the new child has the same size as all the others,
		 * check whether the new child doesn't fit into the clientArea and
		 * if so, extend it.
		 */
		
		StoneView stoneView = (StoneView)child;// The cast is checked in addView.
		
		/* TEST .:
		 * The new child doesn't fit into the clientArea when
		 * its bounds are greater than clientArea.
		 */
		final Rect bounds = new Rect();
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
//			if(bounds.right > clientArea.right) {
			if(horizontalComparator.compare(rightMost, stoneView) > 0) {
				clientArea.right = bounds.right;
				
				rightMost = stoneView;
			}
//			if(bounds.bottom > clientArea.bottom) {
			if(verticalComparator.compare(bottomMost, stoneView) > 0) {
				clientArea.bottom = bounds.bottom;
				
				bottomMost = stoneView;
			}
			
			// If left or top is lower, move origin and right or bottom.
//			if(bounds.left < clientArea.left) {
			if(horizontalComparator.compare(stoneView, leftMost) > 0) {
				clientArea.right += clientArea.left - bounds.left;
				origin.x = -bounds.left;
				
				leftMost = stoneView;
			}
//			if(bounds.top < clientArea.top) {
			if(verticalComparator.compare(stoneView, topMost) > 0) {
				clientArea.bottom += clientArea.top - bounds.top;
				origin.y = -bounds.top;
				
				topMost = stoneView;
			}
			
		}
		
	}
	
	
	
	@Override
	protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
		
		getClientArea(clientArea);
		
		/* TEST .:
		 * Measure how much space stones need (from mostLeft/Top to mostRight/Bottom)
		 * get ration of this and clientArea, and
		 * scale stoneWidth in this ration.
		 * TODO: Then some centering may be needed. ??
		 */
		final float stonesWidth = coordinatesToX(rightMost.getStone().getPosition()) -
				coordinatesToX(leftMost.getStone().getPosition());
		final float stonesHeight = coordinatesToX(bottomMost.getStone().getPosition()) -
				coordinatesToX(topMost.getStone().getPosition());
		
		final float ratio = Math.min(clientArea.width() / stonesWidth,
				clientArea.height() - stonesHeight);
		
		stoneWidth *= ratio;
		
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
		/* TEST .:
		 * Layout each child into its bounds.
		 */
		final int count = getChildCount();
		for(int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if(child.getVisibility() != GONE) {
				
				coordinatesToBounds(((StoneView)child).getStone().getPosition(), tmpRect);
				child.layout(tmpRect.left, tmpRect.top, tmpRect.right, tmpRect.bottom);
				
			}
		}
		
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
		return stoneWidth * stoneDepthRatioX / (1 - stoneDepthRatioX);
		// TODO .: I may need to count with stroke width of the stone here !!!
	}
	
	public float getStoneDepthY() {
		return (stoneWidth / stoneWidthToHeightRatio) * stoneDepthRatioY / (1 - stoneDepthRatioY);
	}
	
	
	
	private float coordinatesToX(Coordinates c) {
		final float width = getStoneWidth();
		final float depthX = getStoneDepthX();
		final int xDepthCorrection = (stoneSlant == StoneView.SLANT_SE_TO_NW || stoneSlant == StoneView.SLANT_NE_TO_SW) ? -(c.z + 1) : c.z;
		return origin.x + c.x*width/2 + xDepthCorrection * depthX;
	}
	
	private float coordinatesToY(Coordinates c) {
		final float height = getStoneHeight();
		final float depthY = getStoneDepthY();
		final int yDepthCorrection = (stoneSlant == StoneView.SLANT_SE_TO_NW || stoneSlant == StoneView.SLANT_SW_TO_NE) ? -(c.z + 1) : c.z;
		return origin.y + c.y*height/2 + yDepthCorrection * depthY;
	}
	
	private Rect coordinatesToBounds(Coordinates c, Rect ret) {
		
		final float width = getStoneWidth();
		final float height = getStoneHeight();
		final float depthX = getStoneDepthX();
		final float depthY = getStoneDepthY();
		
		final float left = coordinatesToX(c);
		final float top = coordinatesToY(c);
		
		ret.set((int)(left + 0.5f), (int)(top + 0.5f),
				(int)(left + width + depthX + 0.5f),
				(int)(top + height + depthY + 0.5f));
		
		return ret;
	}
	
	
	
}
