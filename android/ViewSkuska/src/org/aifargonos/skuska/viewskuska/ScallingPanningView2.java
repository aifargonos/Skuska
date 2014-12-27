package org.aifargonos.skuska.viewskuska;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;



public class ScallingPanningView2 extends ViewGroup {
	
	
	
	/* TODO .: get inspired by ScrollView
	 * 	TODO: mScrollX/Y, computeScroll, onScrollChanged and scrollTo, setOverScrollMode, setScrollContainer, requestRectangleOnScreen!!!???, getLocationInWindow
	 * 		computeHorizontalScrollRange, computeHorizontalScrollOffset, computeHorizontalScrollExtent
	 * 		canScrollHorizontally
	 * 	TODO: scroll bars and awakenScrollBars ... getScrollBarSize, onDrawScrollBars
	 * 	TODO: instantiate from layout.xml
	 * 	DONE: shouldDelayChildPressedState
	 * 	TODO: getNestedScrollAxes and onNestedScroll... ???
	 * 	TODO: use Scroller (maybe only needed for FadingEdge or fling)
	 * 	TODO: FadingEdge animation, getTopFadingEdgeStrength
	 * 	TODO: overScrollBy, onOverScrolled and onScrollChanged ???
	 * 	TODO: all addView should throw IllegalStateException("ScrollView can host only one direct child")
	 * 	TODO: fling
	 * 	TODO: onInterceptTouchEvent
	 * 	TODO: SavedState as in ScrollView
	 * 	TODO: focus ???
	 * 	TODO: performAccessibilityAction, onInitializeAccessibilityNodeInfo, onInitializeAccessibilityEvent
	 * 	TODO: onGenericMotionEvent
	 */
	
	
	
	/**
	 * This is the rectangle into which children of this view are laid-out.
	 * It must be at least the size of this view;
	 * when the content is scaled, it will be bigger.
	 * The coordinates stored in this rectangle are in domain of the view.
	 * This view must always be inside of this rectangle,
	 * so left and top will be often negative (never positive),
	 * while left and top of this view is always 0.
	 * <p>
	 * It must be in floats in order to prevent truncation errors.
	 */
	private float scrollXF;
	private float scrollYF;
	/**
	 * These two replace the <code>contentRect</code>, so that
	 * left and top is always 0.
	 */
	private float virtualWidth = 400;// TODO [layout] .: initialize contentRect with all 0-s and enlarge it when children are added!
	private float virtualHeight = 300;
	
	private final GestureDetectorCompat gestureDetector;
	private ScaleGestureDetector scaleGestureDetector;
	
	
	
	public ScallingPanningView2(Context context) {
		this(context, null);
	}
	
	public ScallingPanningView2(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ScallingPanningView2(Context context, AttributeSet attrs, int defStyleAttr) {
//		this(context, attrs, defStyleAttr, 0);
		super(context, attrs, defStyleAttr);
		
		linePaint.setStrokeWidth(0);
		linePaint.setStyle(Paint.Style.STROKE);
		
		textPaint.setARGB(255, 0, 0, 0);
		textPaint.setAntiAlias(true);
		
		borderPaint.setStrokeWidth(0);
		borderPaint.setStyle(Paint.Style.STROKE);
		
		setWillNotDraw(false);
		
		gestureDetector = new GestureDetectorCompat(getContext(), gestureListener);
		scaleGestureDetector = new ScaleGestureDetector(getContext(), scaleGestureListener);
	}
//	TODO .: old API does not support this .!
//	public ScallingPanningView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//		super(context, attrs, defStyleAttr, defStyleRes);
//	}
	
	
	
	@Override
	public void addView(View child, int index, LayoutParams params) {
		// TODO [layout] .: Delegate to a Layout
//		removeAllViews();
		removeAllViewsInLayout();
		super.addView(child, index, params);
		/* TODO [layout] .: Extend contentRect when a child is added!
		 * 	ask the child what size it wants to be
		 * 	set contentRect to that size ;-)
		 */
		resetContentRectAccordingToChild(child);
	}
	
	@Override
	protected boolean addViewInLayout(View child, int index, LayoutParams params, boolean preventRequestLayout) {
		// TODO [layout] .: Delegate to a Layout
//		removeAllViews();
		removeAllViewsInLayout();
		boolean ret = super.addViewInLayout(child, index, params, preventRequestLayout);
		/* TODO [layout] .: Extend contentRect when a child is added!
		 * 	ask the child what size it wants to be
		 * 	set contentRect to that size ;-)
		 */
		resetContentRectAccordingToChild(child);
		return ret;
	}
	
	private void resetContentRectAccordingToChild(final View child) {
		final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();// TODO .: check this cast !?!
		child.measure(
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		virtualWidth = child.getMeasuredWidth()
				+ getPaddingLeft() + getPaddingRight()
				+ lp.leftMargin + lp.rightMargin;
		virtualHeight = child.getMeasuredHeight()
				+ getPaddingTop() + getPaddingBottom()
				+ lp.topMargin + lp.bottomMargin;
	}
	
	
	
	// TODO [layout] .: Remove these after there can be some components inside.
	private Paint linePaint = new Paint();
	private Paint textPaint = new Paint();
	private Paint borderPaint = new Paint();
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		final float left = getPaddingLeft();
		final float top = getPaddingTop();
		final float right = virtualWidth - getPaddingRight();
		final float bottom = virtualHeight - getPaddingBottom();
		final float height = bottom - top;
		
		linePaint.setStrokeWidth(height / 10);
		
		linePaint.setARGB(255, 0, 255, 0);
		canvas.drawLine(
				left, (top + bottom) / 2,
				right, (top + bottom) / 2,
				linePaint);
		
		linePaint.setARGB(255, 0, 0, 255);
		canvas.drawLine(
				(left + right) / 2, top,
				(left + right) / 2, bottom,
				linePaint);
		
		linePaint.setARGB(255, 255, 0, 0);
		canvas.drawLine(
				left, top,
				right, bottom,
				linePaint);
		
		
		textPaint.setTextSize(height / 3);
		
		canvas.drawText("text",
				left, (top + bottom) / 2,
				textPaint);
		
		borderPaint.setARGB(255, 0, 255, 0);
		canvas.drawRect(
				0, 0,
				virtualWidth, virtualHeight,
				borderPaint);
		
		borderPaint.setARGB(255, 0, 0, 255);
		canvas.drawRect(
				getPaddingLeft(), getPaddingTop(),
				virtualWidth - getPaddingRight(), virtualHeight - getPaddingBottom(),
				borderPaint);
	}
	
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		// Measure the child
		if(getChildCount() > 0) {
			final View child = getChildAt(0);// there should be only one child; ensured by overriding addView*
			if(child.getVisibility() != GONE) {
				final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();// TODO .: check this cast !?!
				child.measure(
						MeasureSpec.makeMeasureSpec(
								Math.max(0, (int)virtualWidth
										- getPaddingLeft() - getPaddingRight()
										- lp.leftMargin - lp.rightMargin),
								MeasureSpec.EXACTLY),
						MeasureSpec.makeMeasureSpec(
								Math.max(0, (int)virtualHeight
										- getPaddingTop() - getPaddingBottom()
										- lp.topMargin - lp.bottomMargin),
								MeasureSpec.EXACTLY));
			}
		}
		
		// I want to be my virtual size.
		// Apply the restrictions from parent.
		final int resolvedWidth = resolveSize((int)(virtualWidth + 0.5f), widthMeasureSpec);
		final int resolvedHeight = resolveSize((int)(virtualHeight + 0.5f), heightMeasureSpec);
		
		// I should be of the resolved size, but at least the minimal size.
		setMeasuredDimension(
				Math.max(getSuggestedMinimumWidth(), resolvedWidth),
				Math.max(getSuggestedMinimumHeight(), resolvedHeight));
	}
	
	
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		if(getChildCount() > 0) {
			final View child = getChildAt(0);// there should be only one child; ensured by overriding addView*
			if(child.getVisibility() != GONE) {
				final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();// TODO .: check this cast !!!
				final int left = getPaddingLeft() + lp.leftMargin;
				final int top = getPaddingTop() + lp.topMargin;
				final int width = child.getMeasuredWidth();
				final int height = child.getMeasuredHeight();
				child.layout(
						left,
						top,
						left + width,
						top + height);
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
	
	
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		fixContentRect();
		scrollTo((int)(scrollXF), (int)(scrollYF));
	}
	
	
	
	@Override
	public boolean shouldDelayChildPressedState() {
		return true;
	}
	
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean ret = scaleGestureDetector.onTouchEvent(event);
		ret = gestureDetector.onTouchEvent(event) || ret;
		return super.onTouchEvent(event) || ret;
	}
	
	
	
	private final GestureDetector.SimpleOnGestureListener gestureListener =
			new GestureDetector.SimpleOnGestureListener() {
		
		@Override
		public boolean onDown(MotionEvent e) {
			return true;
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			
			final float currentScrollX = scrollXF;
			final float currentScrollY = scrollYF;
			
			scrollXF = currentScrollX + distanceX;
			scrollYF = currentScrollY + distanceY;
			
			final float rangeX = virtualWidth - getWidth();
			final float rangeY = virtualHeight - getHeight();
			
			if(scrollXF < 0 || rangeX < scrollXF) {
				scrollXF = currentScrollX;
			}
			if(scrollYF < 0 || rangeY < scrollYF) {
				scrollYF = currentScrollY;
			}
			
			scrollTo((int)(scrollXF), (int)(scrollYF));
			
			ViewCompat.postInvalidateOnAnimation(ScallingPanningView2.this);
			requestLayout();
			return true;
			
		}
		
	};
	
	
	
	private final ScaleGestureDetector.OnScaleGestureListener scaleGestureListener =
			new ScaleGestureDetector.SimpleOnScaleGestureListener() {
		
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			
			final float ratio = detector.getCurrentSpan() / detector.getPreviousSpan();
			
			final float focusX = detector.getFocusX();
			final float focusY = detector.getFocusY();
			
			scaleContentRect(ratio, focusX, focusY);
			fixContentRect();
			scrollTo((int)(scrollXF), (int)(scrollYF));
			
			ViewCompat.postInvalidateOnAnimation(ScallingPanningView2.this);
			requestLayout();
			
			return true;
		}
		
	};
	
	
	
	/**
	 * TODO ...<pre>
	 * If contentRect is too small, adapt it.
	 * 	- If only one side of contentRect is inside this view and contentRect is big enough to contain the view,
	 * 		move contentRect so that the side is at the edge of the view.
	 * 	- If a dimension of contentRect is too small to contain this view,
	 * 		center the contentRect into the middle of the view along this dimension.
	 * 	- If both dimensions of contentRect are too small,
	 * 		scale up the contentRect so that some dimension fits exactly into the view and the other is smaller.
	 * </pre>
	 */
	private void fixContentRect() {// TODO .: rename this !!!
		/* If contentRect is too small, adapt it.
		 * 	- If only one side of contentRect is inside this view and contentRect is big enough to contain the view,
		 * 		move contentRect so that the side is at the edge of the view.
		 * 	- If a dimension of contentRect is too small to contain this view,
		 * 		center the contentRect into the middle of the view along this dimension.
		 * 	- If both dimensions of contentRect are too small,
		 * 		scale up the contentRect so that some dimension fits exactly into the view and the other is smaller.
		 */
		
		final int width = getWidth();
		final int height = getHeight();
		
		final float rangeX = virtualWidth - width;
		final float rangeY = virtualHeight - height;
		
		/* First, center the small dimensions.
		 * 	Once some dimension is too small, it would be moved anyway
		 * 	and it needs to be centered should the scaling up occur.
		 */
		final boolean wasWidthTooSmall;
		final boolean wasHeightTooSmall;
		if(virtualWidth < width) {
			scrollXF = rangeX / 2;
			wasWidthTooSmall = true;
		} else {
			wasWidthTooSmall = false;
		}
		if(virtualHeight < height) {
			scrollYF = rangeY / 2;
			wasHeightTooSmall = true;
		} else {
			wasHeightTooSmall = false;
		}
		
		// After the centering, if both dimensions are too small, scale up the contentRect.
		if(wasWidthTooSmall && wasHeightTooSmall) {
			
			final float ratio;
			
			// Choose which dimension should fit.
			if(width / ((float)height) > virtualWidth / virtualHeight) {
				// This view is wider than contentRect, so match height.
				ratio = height / virtualHeight;
			} else {
				// This view is higher than contentRect, so match width.
				ratio = width / virtualWidth;
			}
			
			final float middleX = width / 2.0f;
			final float middleY = height / 2.0f;
			scaleContentRect(ratio, middleX, middleY);
			
		}
		
		/* If only one side is inside, move the contentRect.
		 * 	This is done only if contentRect is big enough to contain this view.
		 * 	The case when it is not is dealt with above.
		 */
		if(!wasWidthTooSmall) {
			// clamp
			if(scrollXF < 0) {
				scrollXF = 0;
			} else if(scrollXF > rangeX) {
				scrollXF = rangeX;
			}
		}
		if(!wasHeightTooSmall) {
			// clamp
			if(scrollYF < 0) {
				scrollYF = 0;
			} else if(scrollYF > rangeY) {
				scrollYF = rangeY;
			}
		}
		
	}
	
	
	
	/**
	 * Scales <code>contentRect</code> in <code>ratio</code> around (<code>originX</code>, <code>originY</code>).
	 * @param ratio
	 * @param originX
	 * @param originY
	 */
	private void scaleContentRect(final float ratio, final float originX,
			final float originY) {
		/* 
		 * Translate origin of coordinate system of contentRect to (originX, originY),
		 * scale it, and translate it back.
		 */
		scrollXF = ((scrollXF + originX) * ratio) - originX;
		scrollYF = ((scrollYF + originX) * ratio) - originX;
		virtualWidth *= ratio;
		virtualHeight *= ratio;
	}
	
	
	
}
