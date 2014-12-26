package org.aifargonos.skuska.viewskuska;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;



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
	private RectF contentRect = new RectF();
	/**
	 * These two replace the <code>contentRect</code>, so that
	 * left and top is always 0.
	 */
	private int virtualWidth = 3;// TODO [layout] .: initialize contentRect with all 0-s and enlarge it when children are added!
	private int virtualHeight = 4;
	
	/**
	 * Translates internal variables into logical contentRect.
	 * 
	 * @param rect The rectangle that is adapted to contain the logical contentRect.
	 */
	private void getContentRect(RectF rect) {
		rect.set(
				-getScrollX(),
				-getScrollY(),
				-getScrollX() + virtualWidth - getPaddingLeft() - getPaddingRight(),
				-getScrollY() + virtualHeight - getPaddingTop() - getPaddingBottom());
	}
	
	/**
	 * Sets internal variables according to <code>rect</code>.
	 * 
	 * @param rect
	 */
	private void setContentRect(RectF rect) {
		virtualWidth = (int)(rect.width() + getPaddingLeft() + getPaddingRight() + 0.5f);
		virtualHeight = (int)(rect.height() + getPaddingTop() + getPaddingBottom() + 0.5f);
		scrollTo(-(int)(rect.left + 0.5f), -(int)(rect.top + 0.5f));
	}
	
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
		borderPaint.setARGB(255, 0, 0, 255);
		
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
		canvas.drawRect(
				getPaddingLeft(), getPaddingTop(),
				virtualWidth + getPaddingRight(), virtualHeight + getPaddingBottom(),
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

		getContentRect(contentRect);
		fixContentRect(contentRect);
		setContentRect(contentRect);
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
		
		private float lastX;
		private float lastY;
		private float diffX;
		private float diffY;
		private float rectX;
		private float rectY;
		private boolean changed;
		
		@Override
		public boolean onDown(MotionEvent e) {
			lastX = e.getX();
			lastY = e.getY();
			return true;
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			
			getContentRect(contentRect);
			
			diffX = contentRect.left - lastX;
			diffY = contentRect.top - lastY;
			
			lastX = e2.getX();
			lastY = e2.getY();
			
			rectX = lastX + diffX;
			rectY = lastY + diffY;
			
			changed = false;
			if(isLeftValid(rectX) && isRightValid(rectX + contentRect.width())) {
				contentRect.right = rectX + contentRect.width();
				contentRect.left = rectX;
				changed = true;
			}
			if(isTopValid(rectY) && isBottomValid(rectY + contentRect.height())) {
				contentRect.bottom = rectY + contentRect.height();
				contentRect.top = rectY;
				changed = true;
			}
			
			if(changed) {
				setContentRect(contentRect);
				ViewCompat.postInvalidateOnAnimation(ScallingPanningView2.this);
				requestLayout();
				return true;
			} else {
				return false;
			}
			
		}
		
	};
	
	
	
	private final ScaleGestureDetector.OnScaleGestureListener scaleGestureListener =
			new ScaleGestureDetector.SimpleOnScaleGestureListener() {
		
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			
			float ratio = detector.getCurrentSpan() / detector.getPreviousSpan();
			
			final float focusX = detector.getFocusX();
			final float focusY = detector.getFocusY();
			
			getContentRect(contentRect);
			scaleContentRect(contentRect, ratio, focusX, focusY);
			fixContentRect(contentRect);
			setContentRect(contentRect);
			
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
	 * @param contentRect
	 */
	private void fixContentRect(RectF contentRect) {// TODO .: rename this !!!
		/* If contentRect is too small, adapt it.
		 * 	- If only one side of contentRect is inside this view and contentRect is big enough to contain the view,
		 * 		move contentRect so that the side is at the edge of the view.
		 * 	- If a dimension of contentRect is too small to contain this view,
		 * 		center the contentRect into the middle of the view along this dimension.
		 * 	- If both dimensions of contentRect are too small,
		 * 		scale up the contentRect so that some dimension fits exactly into the view and the other is smaller.
		 */
		
		final float middleX = (getPaddingLeft() + getWidth() - getPaddingRight()) / 2.0f;
		final float middleY = (getPaddingTop() + getHeight() - getPaddingBottom()) / 2.0f;
		
		/* First, center the small dimensions.
		 * 	Once some dimension is too small, it would be moved anyway
		 * 	and it needs to be centered of the scaling up should occur.
		 */
		final boolean wasWidthTooSmall;
		final boolean wasHeightTooSmall;
//		if(isWidthTooSmall()) {
		if(virtualWidth < getWidth()) {
			float contentRectWidth = contentRect.width();
			contentRect.left = middleX - contentRectWidth / 2;
			contentRect.right = middleX + contentRectWidth / 2;
			wasWidthTooSmall = true;
		} else {
			wasWidthTooSmall = false;
		}
//		if(isHeightTooSmall()) {
		if(virtualHeight < getHeight()) {
			float contentRectHeight = contentRect.height();
			contentRect.top = middleY - contentRectHeight / 2;
			contentRect.bottom = middleY + contentRectHeight / 2;
			wasHeightTooSmall = true;
		} else {
			wasHeightTooSmall = false;
		}
		
		// After the centering, if both dimensions are too small, scale up the contentRect.
		if(wasWidthTooSmall && wasHeightTooSmall) {
			
			final float ratio;
			
			// Choose which dimension should fit.
			final int contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
			final int contentHeight = getHeight() - getPaddingTop() - getPaddingBottom();
			if(contentWidth / ((float)contentHeight) > contentRect.width() / contentRect.height()) {
				// This view is wider than contentRect, so match height.
				ratio = contentHeight / contentRect.height();
			} else {
				// This view is higher than contentRect, so match width.
				ratio = contentWidth / contentRect.width();
			}
			
			scaleContentRect(contentRect, ratio, middleX, middleY);
			
		}
		
		/* If only one side is inside, move the contentRect.
		 * 	This is done only if contentRect is big enough to contain this view.
		 * 	The case when it is not is dealt with above.
		 */
		if(!wasWidthTooSmall) {
			if(!isLeftValid(contentRect.left)) {
				contentRect.right = getPaddingLeft() + contentRect.width();
				contentRect.left = getPaddingLeft();
			}
			if(!isRightValid(contentRect.right)) {
				contentRect.left = getWidth() - getPaddingRight() - contentRect.width();
				contentRect.right = getWidth() - getPaddingRight();
			}
		}
		if(!wasHeightTooSmall) {
			if(!isTopValid(contentRect.top)) {
				contentRect.bottom = getPaddingTop() + contentRect.height();
				contentRect.top = getPaddingTop();
			}
			if(!isBottomValid(contentRect.bottom)) {
				contentRect.top = getHeight() - getPaddingBottom() - contentRect.height();
				contentRect.bottom = getHeight() - getPaddingBottom();
			}
		}
		
	}
	
	
	
	/**
	 * Checks whether <code>left</code> can be assigned to <code>{@link #contentRect}.left</code>.
	 * 
	 * @param left
	 * @return <code>true</code> iff <code>left</code> is at most <code>{@link #getPaddingLeft()}</code>.
	 */
	private boolean isLeftValid(final float left) {
		return left <= getPaddingLeft();
	}
	
	/**
	 * Checks whether <code>right</code> can be assigned to <code>{@link #contentRect}.right</code>.
	 * 
	 * @param right
	 * @return <code>true</code> iff <code>right</code> is at least <code>{@link #getWidth()} - {@link #getPaddingRight()}</code>.
	 */
	private boolean isRightValid(final float right) {
		return right >= getWidth() - getPaddingRight();
	}
	
	/**
	 * Checks whether <code>top</code> can be assigned to <code>{@link #contentRect}.top</code>.
	 * 
	 * @param top
	 * @return <code>true</code> iff <code>top</code> is at most <code>{@link #getPaddingTop()}</code>.
	 */
	private boolean isTopValid(final float top) {
		return top <= getPaddingTop();
	}
	
	/**
	 * Checks whether <code>bottom</code> can be assigned to <code>{@link #contentRect}.bottom</code>.
	 * 
	 * @param bottom
	 * @return <code>true</code> iff <code>bottom</code> is at least <code>{@link #getHeight()} - {@link #getPaddingBottom()}</code>.
	 */
	private boolean isBottomValid(final float bottom) {
		return bottom >= getHeight() - getPaddingBottom();
	}
	
	/**
	 * Just a conjunction of {@link #isLeftValid(float)} and {@link #isRightValid(float)}.
	 * 
	 * @param left
	 * @param right
	 * @return
	 */
	private boolean isLeftRightValid(final float left, final float right) {
		return isLeftValid(left) && isRightValid(right);
	}
	
	/**
	 * Just a conjunction of {@link #isTopValid(float)} and {@link #isBottomValid(float)}.
	 * 
	 * @param top
	 * @param bottom
	 * @return
	 */
	private boolean isTopBottomValid(final float top, final float bottom) {
		return isTopValid(top) && isBottomValid(bottom);
	}
	
	/**
	 * Just a conjunction of {@link #isLeftRightValid(float, float)} and {@link #isTopBottomValid(float, float)}.
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 * @return
	 */
	private boolean isRectValid(final float left, final float top,
			final float right, final float bottom) {
		return isLeftRightValid(left, right) && isTopBottomValid(top, bottom);
	}
	
	/**
	 * Just a conjunction of {@link #isLeftRightValid(float, float)} and {@link #isTopBottomValid(float, float)}.
	 * 
	 * @param rect
	 * @return
	 */
	private boolean isRectValid(final RectF rect) {
		return isRectValid(rect.left, rect.top, rect.right, rect.bottom);
	}
	
	
	/**
	 * Checks whether the width of {@link #contentRect} is too small to contain this view without horizontal padding.
	 * 
	 * @return <code>true</code> iff the width of {@link #contentRect} is smaller than
	 * 	the width of this view minus its horizontal padding.
	 */
	private boolean isWidthTooSmall() {
		return contentRect.width() < getWidth() - getPaddingLeft() - getPaddingRight();
	}
	
	/**
	 * Checks whether the height of {@link #contentRect} is too small to contain this view without vertical padding.
	 * 
	 * @return <code>true</code> iff the hight of {@link #contentRect} is smaller than
	 * 	the height of this view minus its vertical padding.
	 */
	private boolean isHeightTooSmall() {
		return contentRect.height() < getHeight() - getPaddingTop() - getPaddingBottom();
	}
	
	
	/**
	 * Scales <code>contentRect</code> in <code>ratio</code> around (<code>originX</code>, <code>originY</code>).
	 * @param contentRect
	 * @param ratio
	 * @param originX
	 * @param originY
	 */
	private static void scaleContentRect(RectF contentRect, final float ratio, final float originX, final float originY) {
		/* 
		 * Translate origin of coordinate system of contentRect to (originX, originY),
		 * scale it, and translate it back.
		 */
		contentRect.left = ((contentRect.left - originX) * ratio) + originX;
		contentRect.top = ((contentRect.top - originY) * ratio) + originY;
		contentRect.right = ((contentRect.right - originX) * ratio) + originX;
		contentRect.bottom = ((contentRect.bottom - originY) * ratio) + originY;
	}
	
	
	
}
