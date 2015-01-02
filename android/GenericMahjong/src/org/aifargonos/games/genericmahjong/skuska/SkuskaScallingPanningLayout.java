package org.aifargonos.games.genericmahjong.skuska;

import org.aifargonos.games.genericmahjong.gui.ScallingPanningView;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;



public class SkuskaScallingPanningLayout extends ScallingPanningView {
	
	
	
	/* TODO .:
	 * 	TODO: get padding out of client area !!
	 */
	
	
	
	public SkuskaScallingPanningLayout(Context context) {
		this(context, null);
	}
	
	public SkuskaScallingPanningLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public SkuskaScallingPanningLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	
	
	@Override
	protected void adaptClientAreaToNewChild(View child, RectF clientArea) {
		
		final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
		child.measure(
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		final int width = child.getMeasuredWidth()
				+ getPaddingLeft() + getPaddingRight()
				+ lp.leftMargin + lp.rightMargin;
		final int height = child.getMeasuredHeight()
				+ getPaddingTop() + getPaddingBottom()
				+ lp.topMargin + lp.bottomMargin;
		
		/* 
		 * Add the height to the height and
		 * set the width to the maximum.
		 */
		
		final float clientWidth = Math.max(clientArea.width(), width);
		final float clientHeight = clientArea.height() + height;
		
		clientArea.right = clientArea.left + clientWidth;
		clientArea.bottom = clientArea.top + clientHeight;
	}
	
	
	
	@Override
	protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
		
		int nonGoneChildCount = 0;
		
		final int count = getChildCount();
		for(int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if(child.getVisibility() != GONE) {
				nonGoneChildCount++;
			}
		}
		
		RectF clientArea = new RectF();
		getClientArea(clientArea);
		final int width = (int)(clientArea.width() - getPaddingLeft() - getPaddingRight());
		final int height = (int)((clientArea.height() - getPaddingTop() - getPaddingBottom())
				/ nonGoneChildCount);
		
		for(int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if(child.getVisibility() != GONE) {
				final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
				child.measure(
						MeasureSpec.makeMeasureSpec(
								Math.max(0, width
										- lp.leftMargin - lp.rightMargin),
								MeasureSpec.EXACTLY),
						MeasureSpec.makeMeasureSpec(
								Math.max(0, height
										- lp.topMargin - lp.bottomMargin),
								MeasureSpec.EXACTLY));
			}
		}
		
	}
	
	
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		int top = t + getPaddingTop();
		
		final int count = getChildCount();
		for(int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if(child.getVisibility() != GONE) {
				final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
				final int left = getPaddingLeft() + lp.leftMargin;
				top += lp.topMargin;
				final int width = child.getMeasuredWidth();
				final int height = child.getMeasuredHeight();
				child.layout(
						left,
						top,
						left + width,
						top + height);
				top += height + lp.bottomMargin;
			}
		}
		
	}
    
	
	
	@Override
	protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }
	
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new MarginLayoutParams(getContext(), attrs);
	}
	
	@Override
	protected LayoutParams generateLayoutParams(LayoutParams p) {
		return new MarginLayoutParams(p);
	}
	
	@Override
	protected boolean checkLayoutParams(LayoutParams p) {
		return p != null && p instanceof MarginLayoutParams;
	}
	
	
	
}
