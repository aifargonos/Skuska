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
	/**
	 * These two replace the <code>contentRect</code>, so that
	 * left and top is always 0.
	 */
	private int virtualWidth = 3;// TODO [layout] .: initialize contentRect with all 0-s and enlarge it when children are added!
	private int virtualHeight = 4;
	
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
//		
//		linePaint.setStrokeWidth(0);
//		linePaint.setStyle(Paint.Style.STROKE);
//		
//		textPaint.setARGB(255, 0, 0, 0);
//		textPaint.setAntiAlias(true);
		
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
//	
//	
//	
//	// TODO [layout] .: Remove these after there can be some components inside.
//	private Paint linePaint = new Paint();
//	private Paint textPaint = new Paint();
	private Paint borderPaint = new Paint();
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		
//		linePaint.setStrokeWidth(contentRect.height() / 10);
//		
//		linePaint.setARGB(255, 0, 255, 0);
//		canvas.drawLine(
//				contentRect.left, (contentRect.top + contentRect.bottom) / 2,
//				contentRect.right, (contentRect.top + contentRect.bottom) / 2,
//				linePaint);
//		
//		linePaint.setARGB(255, 0, 0, 255);
//		canvas.drawLine(
//				(contentRect.left + contentRect.right) / 2, contentRect.top,
//				(contentRect.left + contentRect.right) / 2, contentRect.bottom,
//				linePaint);
//		
//		linePaint.setARGB(255, 255, 0, 0);
//		canvas.drawLine(
//				contentRect.left, contentRect.top,
//				contentRect.right, contentRect.bottom,
//				linePaint);
//		
//		
//		textPaint.setTextSize(contentRect.height() / 3);
//		
//		canvas.drawText("text",
//				contentRect.left, (contentRect.top + contentRect.bottom) / 2,
//				textPaint);
//		
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
								Math.max(0, virtualWidth
										- getPaddingLeft() - getPaddingRight()
										- lp.leftMargin - lp.rightMargin),
								MeasureSpec.EXACTLY),
						MeasureSpec.makeMeasureSpec(
								Math.max(0, virtualHeight
										- getPaddingTop() - getPaddingBottom()
										- lp.topMargin - lp.bottomMargin),
								MeasureSpec.EXACTLY));
			}
		}
		
		// I want to be my virtual size.
		// Apply the restrictions from parent.
		final int resolvedWidth = resolveSize(virtualWidth, widthMeasureSpec);
		final int resolvedHeight = resolveSize(virtualHeight, heightMeasureSpec);
		
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

		PointF scroll = new PointF(getScrollX(), getScrollY());
		PointF size = new PointF(virtualWidth, virtualHeight);
		fixContentRect(scroll, size);
		scrollTo((int)(scroll.x + 0.49f), (int)(scroll.y + 0.49f));
		virtualWidth = (int)(size.x + 0.49f);
		virtualHeight = (int)(size.y + 0.49f);
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
//		
//		private float lastX;
//		private float lastY;
//		private float diffX;
//		private float diffY;
//		private float rectX;
//		private float rectY;
//		private boolean changed;
		
		@Override
		public boolean onDown(MotionEvent e) {
//			lastX = e.getX();
//			lastY = e.getY();
			return true;
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
//			
//			getContentRect(contentRect);
//			
//			diffX = contentRect.left - lastX;
//			diffY = contentRect.top - lastY;
//			
//			lastX = e2.getX();
//			lastY = e2.getY();
//			
//			rectX = lastX + diffX;
//			rectY = lastY + diffY;
//			
//			changed = false;
//			if(isLeftValid(rectX) && isRightValid(rectX + contentRect.width())) {
//				contentRect.right = rectX + contentRect.width();
//				contentRect.left = rectX;
//				changed = true;
//			}
//			if(isTopValid(rectY) && isBottomValid(rectY + contentRect.height())) {
//				contentRect.bottom = rectY + contentRect.height();
//				contentRect.top = rectY;
//				changed = true;
//			}
//			
//			if(changed) {
//				setContentRect(contentRect);
//				ViewCompat.postInvalidateOnAnimation(ScallingPanningView2.this);
//				requestLayout();
//				return true;
//			} else {
//				return false;
//			}
			
			final int currentScrollX = getScrollX();
			final int currentScrollY = getScrollY();
			
			int newScrollX = currentScrollX + (int)(distanceX + 0.49f);
			int newScrollY = currentScrollY + (int)(distanceY + 0.49f);
			
			final int rangeX = virtualWidth - getWidth();
			final int rangeY = virtualHeight - getHeight();
			
			if(newScrollX < 0 || rangeX < newScrollX) {
				newScrollX = currentScrollX;
			}
			if(newScrollY < 0 || rangeY < newScrollY) {
				newScrollY = currentScrollY;
			}
			
			scrollTo(newScrollX, newScrollY);
			
			ViewCompat.postInvalidateOnAnimation(ScallingPanningView2.this);
			requestLayout();
			return true;
			
		}
		
	};
	
	
	
	private final ScaleGestureDetector.OnScaleGestureListener scaleGestureListener =
			new ScaleGestureDetector.SimpleOnScaleGestureListener() {
		
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			
			float ratio = detector.getCurrentSpan() / detector.getPreviousSpan();
			
			final float focusX = detector.getFocusX();
			final float focusY = detector.getFocusY();
			
			PointF scroll = new PointF(getScrollX(), getScrollY());
			PointF size = new PointF(virtualWidth, virtualHeight);
			scaleContentRect(scroll, size, ratio, focusX, focusY);
			fixContentRect(scroll, size);
			scrollTo((int)(scroll.x + 0.49f), (int)(scroll.y + 0.49f));
			virtualWidth = (int)(size.x + 0.49f);
			virtualHeight = (int)(size.y + 0.49f);
			
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
	 * @param scroll TODO
	 * @param size TODO
	 */
	private void fixContentRect(PointF scroll, PointF size) {// TODO .: rename this !!!
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
			scroll.x = rangeX / 2;
			wasWidthTooSmall = true;
		} else {
			wasWidthTooSmall = false;
		}
		if(virtualHeight < height) {
			scroll.y = rangeY / 2;
			wasHeightTooSmall = true;
		} else {
			wasHeightTooSmall = false;
		}
		
		// After the centering, if both dimensions are too small, scale up the contentRect.
		if(wasWidthTooSmall && wasHeightTooSmall) {
			
			final float ratio;
			
			// Choose which dimension should fit.
			if(width / ((float)height) > virtualWidth / ((float)virtualHeight)) {
				// This view is wider than contentRect, so match height.
				ratio = height / ((float)virtualHeight);
			} else {
				// This view is higher than contentRect, so match width.
				ratio = width / ((float)virtualWidth);
			}
			
			final float middleX = width / 2.0f;
			final float middleY = height / 2.0f;
			scaleContentRect(scroll, size, ratio, middleX, middleY);
			
		}
		
		/* If only one side is inside, move the contentRect.
		 * 	This is done only if contentRect is big enough to contain this view.
		 * 	The case when it is not is dealt with above.
		 */
		if(!wasWidthTooSmall) {
			// clamp
			if(scroll.x < 0) {
				scroll.x = 0;
			} else if(scroll.x > rangeX) {
				scroll.x = rangeX;
			}
		}
		if(!wasHeightTooSmall) {
			// clamp
			if(scroll.y < 0) {
				scroll.y = 0;
			} else if(scroll.y > rangeY) {
				scroll.y = rangeY;
			}
		}
		
	}
	
	
	
	/**
	 * Scales <code>contentRect</code> in <code>ratio</code> around (<code>originX</code>, <code>originY</code>).
	 * @param scroll TODO
	 * @param size TODO
	 * @param ratio
	 * @param originX
	 * @param originY
	 */
	private static void scaleContentRect(PointF scroll, PointF size, final float ratio, final float originX, final float originY) {
		/* 
		 * Translate origin of coordinate system of contentRect to (originX, originY),
		 * scale it, and translate it back.
		 */
		scroll.x = ((scroll.x - originX) * ratio) + originX;
		scroll.y = ((scroll.y - originX) * ratio) + originX;
		size.x *= ratio;
		size.y *= ratio;
	}
	
	
	
}
