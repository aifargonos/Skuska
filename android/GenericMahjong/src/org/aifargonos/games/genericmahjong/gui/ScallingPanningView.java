package org.aifargonos.games.genericmahjong.gui;

import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;



public abstract class ScallingPanningView extends ViewGroup {
	
	
	
	/* TODO .: get inspired by ScrollView
	 * 	DONE: Implement the scrolling using mScrollX/Y.
	 * 		It can be controlled by scrollTo.
	 * 	TODO: Layout should be done by descendants!
	 * 	TODO: onInterceptTouchEvent
	 * 	TODO: inflate from layout.xml
	 * 	TODO: Scroll bars
	 * 		awakenScrollBars ... getScrollBarSize, onDrawScrollBars
	 * 		computeHorizontalScrollRange, computeHorizontalScrollOffset, computeHorizontalScrollExtent
	 * 	TODO: Overscroll, Scroller and Fling
	 * 		setOverScrollMode, overScrollBy, onOverScrolled
	 * 	TODO: FadingEdge animation
	 * 		getTopFadingEdgeStrength
	 * 	TODO: other scroll support ...
	 * 		computeScroll, onScrollChanged, setScrollContainer, requestRectangleOnScreen!!!???, getLocationInWindow
	 * 		canScrollHorizontally, onScrollChanged
	 * 	DONE: shouldDelayChildPressedState
	 * 	TODO: Nested Scroll
	 * 		getNestedScrollAxes and onNestedScroll... ???
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
	 * Float version of {@link #getScrollX()}. Used to prevent rounding errors.
	 * This is just a local copy, in order to take effect,
	 * it has to be written to the original value by {@link #scrollTo(int, int)}.
	 */
	private float scrollXF;
	/**
	 * Float version of {@link #getScrollY()}. Used to prevent rounding errors.
	 * This is just a local copy, in order to take effect,
	 * it has to be written to the original value by {@link #scrollTo(int, int)}.
	 */
	private float scrollYF;
	/**
	 * These two replace the <code>contentRect</code>, so that
	 * left and top is always 0.
	 */
	/**
	 * Width of the client area where the children should be laid out.
	 * Scroll occurs over this area and this is the are that is scaled.
	 */
	private float virtualWidth = 0;
	/**
	 * Height of the client area where the children should be laid out.
	 * Scroll occurs over this area and this is the are that is scaled.
	 */
	private float virtualHeight = 0;
	
	private final GestureDetectorCompat gestureDetector;
	private ScaleGestureDetector scaleGestureDetector;
	
	private boolean isGestureInProgress = false;
	private DrawingTask drawingTask;
	private Bitmap drawCache = null;
	
	
	
	public ScallingPanningView(Context context) {
		this(context, null);
	}
	
	public ScallingPanningView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ScallingPanningView(Context context, AttributeSet attrs, int defStyleAttr) {
//		this(context, attrs, defStyleAttr, 0);
		super(context, attrs, defStyleAttr);
//		
//		linePaint.setStrokeWidth(0);
//		linePaint.setStyle(Paint.Style.STROKE);
//		
//		textPaint.setARGB(255, 0, 0, 0);
//		textPaint.setAntiAlias(true);
//		
//		borderPaint.setStrokeWidth(0);
//		borderPaint.setStyle(Paint.Style.STROKE);
		
		setWillNotDraw(false);// The draw(Canvas) needs to be called because of drawing cache!
		
		gestureDetector = new GestureDetectorCompat(getContext(), gestureListener);
		scaleGestureDetector = new ScaleGestureDetector(getContext(), scaleGestureListener);
	}
//	TODO .: old API does not support this .!
//	public ScallingPanningView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//		super(context, attrs, defStyleAttr, defStyleRes);
//	}
	
	
	
	@Override
	public void addView(View child, int index, LayoutParams params) {
		super.addView(child, index, params);
		
		RectF clientArea = new RectF();
		getClientArea(clientArea);
		adaptClientAreaToNewChild(child, clientArea);
		virtualWidth = clientArea.width();
		virtualHeight = clientArea.height();
		scrollXF -= clientArea.left;
		scrollYF -= clientArea.top;
		scrollTo((int)(scrollXF), (int)(scrollYF));
	}
	
	@Override
	protected boolean addViewInLayout(View child, int index, LayoutParams params, boolean preventRequestLayout) {
		boolean ret = super.addViewInLayout(child, index, params, preventRequestLayout);
		
		RectF clientArea = new RectF();
		getClientArea(clientArea);
		adaptClientAreaToNewChild(child, clientArea);
		virtualWidth = clientArea.width();
		virtualHeight = clientArea.height();
		scrollXF -= clientArea.left;
		scrollYF -= clientArea.top;
		scrollTo((int)(scrollXF), (int)(scrollYF));
		
		return ret;
	}
	
	abstract protected void adaptClientAreaToNewChild(final View child, final RectF clientArea);
//	
//	
//	
//	// TODO [layout] .: Remove these after there can be some components inside.
//	private Paint linePaint = new Paint();
//	private Paint textPaint = new Paint();
//	private Paint borderPaint = new Paint();
//	
//	@Override
//	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
//		
//		final float left = getPaddingLeft();
//		final float top = getPaddingTop();
//		final float right = virtualWidth - getPaddingRight();
//		final float bottom = virtualHeight - getPaddingBottom();
//		final float height = bottom - top;
//		
//		linePaint.setStrokeWidth(height / 10);
//		
//		linePaint.setARGB(255, 0, 255, 0);
//		canvas.drawLine(
//				left, (top + bottom) / 2,
//				right, (top + bottom) / 2,
//				linePaint);
//		
//		linePaint.setARGB(255, 0, 0, 255);
//		canvas.drawLine(
//				(left + right) / 2, top,
//				(left + right) / 2, bottom,
//				linePaint);
//		
//		linePaint.setARGB(255, 255, 0, 0);
//		canvas.drawLine(
//				left, top,
//				right, bottom,
//				linePaint);
//		
//		
//		textPaint.setTextSize(height / 3);
//		
//		canvas.drawText("text",
//				left, (top + bottom) / 2,
//				textPaint);
//		
//		borderPaint.setARGB(255, 0, 255, 0);
//		canvas.drawRect(
//				0, 0,
//				virtualWidth, virtualHeight,
//				borderPaint);
//		
//		borderPaint.setARGB(255, 0, 0, 255);
//		canvas.drawRect(
//				getPaddingLeft(), getPaddingTop(),
//				virtualWidth - getPaddingRight(), virtualHeight - getPaddingBottom(),
//				borderPaint);
//	}
	
	
	
	@Override
	abstract protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec);
	
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		// I want to be my virtual size.
		// Apply the restrictions from parent.
		final int resolvedWidth = resolveSize((int)(virtualWidth + 0.5f), widthMeasureSpec);
		final int resolvedHeight = resolveSize((int)(virtualHeight + 0.5f), heightMeasureSpec);
		
		// I should be of the resolved size, but at least the minimal size.
		setMeasuredDimension(
				Math.max(getSuggestedMinimumWidth(), resolvedWidth),
				Math.max(getSuggestedMinimumHeight(), resolvedHeight));
		
		measureChildren(widthMeasureSpec, heightMeasureSpec);
		
	}
	
	
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		clampClientArea();
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
		ret = super.onTouchEvent(event) || ret;
    	
    	final int action = MotionEventCompat.getActionMasked(event);
        switch(action) {
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			/* TODO .: I hope these are all the cases that finish a gesture.
			 * It may happen that events come in inconsistent order, so I'll need to be clever :-P
			 */
			isGestureInProgress = false;
			invalidate();// to that super.draw(Canvas) is called
			break;
		}
		
        return ret;
	}
	
	
	
	@Override
	public void draw(Canvas canvas) {
		/* 
		 * TODO !! prepare drawCache when
		 * 	created (onAttachedToWindow(), how about detach),
		 * 	resized
		 * 	child changed ... pretty much each time marked dirty ...
		 * 
		 * like this .:
		 * 	when draw is called
		 * 		and a gesture is in progress
		 * 			and drawCache is ready,
		 * 				draw it,
		 * 			otherwise
		 * 				call super.
		 * 		If no gesture is in progress,
		 * 			call super
		 * 			and if drawCache is ready, draw it again.
		 * Is there a case when draw is called outside a gesture and I don't need to prepare the cache ??
		 */
		if(drawingTask == null) {
			// TODO .: This is meant to be called only once at the beginning .. maybe there is a better place for it ..
			drawingTask = new DrawingTask();
			drawingTask.execute();
		}
		
		final Bitmap newDrawCache = getDrawCacheIfReady();
		
		if(isGestureInProgress) {
			
			Log.d(getClass().getName(), "drawing when gesture IS in progress");
			
			if(newDrawCache == null) {
				Log.d(getClass().getName(), "cache is NOT ready");
				if(drawCache == null) {
					super.draw(canvas);
				} else {
					if((int)(virtualWidth + 0.5f) == drawCache.getWidth() && (int)(virtualHeight + 0.5f) == drawCache.getHeight()) {
						canvas.drawBitmap(drawCache, 0, 0, null);
					} else {
						final int saveCount = canvas.save();
						canvas.scale(virtualWidth / drawCache.getWidth(), virtualHeight / drawCache.getHeight());
						canvas.drawBitmap(drawCache, 0, 0, null);
						canvas.restoreToCount(saveCount);
					}
				}
			} else {
				Log.d(getClass().getName(), "cache IS ready");
				drawCache = newDrawCache;
				if((int)(virtualWidth + 0.5f) == newDrawCache.getWidth() && (int)(virtualHeight + 0.5f) == newDrawCache.getHeight()) {
					canvas.drawBitmap(newDrawCache, 0, 0, null);
				} else {
					final int saveCount = canvas.save();
					canvas.scale(virtualWidth / newDrawCache.getWidth(), virtualHeight / newDrawCache.getHeight());
					canvas.drawBitmap(newDrawCache, 0, 0, null);
					canvas.restoreToCount(saveCount);
				}
			}
			
		} else {
			
			Log.d(getClass().getName(), "drawing when gesture is NOT in progress");
			
			super.draw(canvas);
			if(newDrawCache != null) {
				Log.d(getClass().getName(), "drawing the cache");
				drawCache = newDrawCache;
				drawingTask = new DrawingTask();
				drawingTask.execute();
			}
			
		}
		
	}
	
	/**
	 * Returns the drawing cache if it is ready, otherwise returns <code>null</code>.
	 * 
	 * @return the drawing cache if it is ready, otherwise returns <code>null</code>.
	 */
	private Bitmap getDrawCacheIfReady() {
		if(drawingTask == null || !drawingTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
			return null;
		}
		
		try {
			return drawingTask.get();
		} catch(InterruptedException e) {
			// TODO What to do in a case of exception ??
			e.printStackTrace();
			return null;
		} catch(ExecutionException e) {
			// TODO What to do in a case of exception ??
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	
	private class DrawingTask extends AsyncTask<Void, Void, Bitmap> {
		
		@Override
		protected Bitmap doInBackground(Void... params) {
			
			Bitmap b = Bitmap.createBitmap((int)(virtualWidth + 0.5f), (int)(virtualHeight + 0.5f), Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(b);
			// TODO .: all children must be visible !!!
			// TODO .: I probably need translation by scrollX/Y
			ScallingPanningView.super.draw(c);
			
			return b;
		}
		
	}
	
	
	
	private final GestureDetector.SimpleOnGestureListener gestureListener =
			new GestureDetector.SimpleOnGestureListener() {
		
		@Override
		public boolean onDown(MotionEvent e) {
			isGestureInProgress = true;
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
			
			ViewCompat.postInvalidateOnAnimation(ScallingPanningView.this);
			requestLayout();
			return true;
			
		}
		
	};
	
	
	
	private final ScaleGestureDetector.OnScaleGestureListener scaleGestureListener =
			new ScaleGestureDetector.SimpleOnScaleGestureListener() {
		
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			isGestureInProgress = true;
			return super.onScaleBegin(detector);
		};
		
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			
			final float ratio = detector.getCurrentSpan() / detector.getPreviousSpan();
			
			final float focusX = detector.getFocusX();
			final float focusY = detector.getFocusY();
			
			scaleContentRect(ratio, focusX, focusY);
			clampClientArea();
			scrollTo((int)(scrollXF), (int)(scrollYF));
			
			ViewCompat.postInvalidateOnAnimation(ScallingPanningView.this);
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
	private void clampClientArea() {
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
		scrollYF = ((scrollYF + originY) * ratio) - originY;
		virtualWidth *= ratio;
		virtualHeight *= ratio;
	}
	
	
	
	public void getClientArea(final RectF result) {
		result.set(0, 0, virtualWidth, virtualHeight);
	}
	
	
	
}
