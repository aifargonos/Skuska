package org.aifargonos.games.genericmahjong.skuska;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;



public class ViewSkuskaLayout extends ViewGroup {
	
	
	
	private int theGap = 30;// TODO .: magic const
	private Paint paint = new Paint();
	
	private final GestureDetectorCompat gestureDetector;
	
	private Rect contentRect = new Rect(theGap, theGap, theGap + 200, theGap + 250);// TODO .: magic const
	
	
	
	public ViewSkuskaLayout(Context context) {
		this(context, null, 0);
	}
	
	public ViewSkuskaLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ViewSkuskaLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		paint.setARGB(255, 255, 0, 0);// TODO .: these are also kind of magic constants
		paint.setStrokeWidth(0);
		paint.setStyle(Paint.Style.STROKE);
		
		setWillNotDraw(false);
		
		gestureDetector = new GestureDetectorCompat(getContext(), gestureListener);
		
	}
	
	
	
	@Override
	public void addView(View child, int index, LayoutParams params) {
//		removeAllViews();
		removeAllViewsInLayout();
		super.addView(child, index, params);
	}
	
	@Override
	protected boolean addViewInLayout(View child, int index, LayoutParams params, boolean preventRequestLayout) {
//		removeAllViews();
		removeAllViewsInLayout();
		return super.addViewInLayout(child, index, params, preventRequestLayout);
	}
	
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// draw the control points
		canvas.drawRect(
				contentRect.left - theGap,
				contentRect.top - theGap,
				contentRect.left + theGap,
				contentRect.top + theGap,
				paint);
		canvas.drawRect(
				contentRect.right - theGap,
				contentRect.bottom - theGap,
				contentRect.right + theGap,
				contentRect.bottom + theGap,
				paint);
		
		// draw the content rectangle
		canvas.drawRect(contentRect, paint);
	}
	
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		// I want to keep the size and position of contentRect
		int measuredWidth = contentRect.right + theGap + 1 + getPaddingRight();
		int measuredHeight = contentRect.bottom + theGap + 1 + getPaddingBottom();
		
		// apply the restrictions from parent
		final int resolvedWidth = resolveSize(measuredWidth, widthMeasureSpec);
		final int resolvedHeight = resolveSize(measuredHeight, heightMeasureSpec);
		
		// adapt the content rectangle according to the restrictions
		// TODO .: changing the contentRect should be done in onSizeChanged !!!
		/* 
		 * if I need to be smaller, first move contentRect,
		 * then make contentRect smaller,
		 * and if it is not enough, tell the parent.
		 */
		// along X axis
		if(resolvedWidth < measuredWidth) {
			contentRect.left = Math.max(
					resolvedWidth - getPaddingRight() - theGap - contentRect.width(),
					getPaddingLeft() + theGap);
			contentRect.right = Math.max(
					resolvedWidth - getPaddingRight() - theGap,
					getPaddingLeft() + 2*theGap);
			measuredWidth = contentRect.right + theGap + 1 + getPaddingRight();
			if(resolvedWidth < measuredWidth) {
				measuredWidth |= MEASURED_STATE_TOO_SMALL;
			}
		}
		// along Y axis
		if(resolvedHeight < measuredHeight) {
			contentRect.top = Math.max(
					resolvedHeight - getPaddingBottom() - theGap - contentRect.height(),
					getPaddingTop() + theGap);
			contentRect.bottom = Math.max(
					resolvedHeight - getPaddingBottom() - theGap,
					getPaddingTop() + 2*theGap);
			measuredHeight = contentRect.bottom + theGap + 1 + getPaddingBottom();
			if(resolvedHeight < measuredHeight) {
				measuredHeight |= MEASURED_STATE_TOO_SMALL;
			}
		}
		
		// Measure the child
		View child = getChildAt(0);// there should be only one child; ensured by overriding addView*
		if(child != null && child.getVisibility() != GONE) {
			/* 
			 * One approach is to enforce my dimensions on the child.
			 */
			final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();// TODO .: check this cast !?!
			child.measure(
					MeasureSpec.makeMeasureSpec(contentRect.width() - lp.leftMargin - lp.rightMargin, MeasureSpec.EXACTLY),
					MeasureSpec.makeMeasureSpec(contentRect.height() - lp.topMargin - lp.bottomMargin, MeasureSpec.EXACTLY));
//			/* 
//			 * Other approach is to take into account child's layout params.
//			 */
//			/* 
//			 * I pretend to be forced to have exactly the measured size,
//			 * because the contentRect is fixed.
//			 */
//			measureChildWithMargins(child,
//					MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
//					contentRect.left - getPaddingLeft() + 2*theGap,
//					MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY),
//					contentRect.top - getPaddingTop() + 2*theGap);
		}
		
		setMeasuredDimension(
				Math.max(measuredWidth, MeasureSpec.getSize(widthMeasureSpec)),
				Math.max(measuredHeight, MeasureSpec.getSize(heightMeasureSpec)));
	}
	
	

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		View child = getChildAt(0);// there should be only one child; ensured by overriding addView*
		if(child != null && child.getVisibility() != GONE) {
			/* 
			 * One approach is to enforce my dimensions on the child.
			 */
			final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();// TODO .: check this cast !!!
			child.layout(contentRect.left + lp.leftMargin,
					contentRect.top + lp.topMargin,
					contentRect.right - lp.rightMargin,
					contentRect.bottom - lp.bottomMargin);
//			/* 
//			 * Other approach is to take into account child's layout params.
//			 */
//			final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();// TODO .: check this cast !!!
//			final int width = child.getMeasuredWidth();
//			final int height = child.getMeasuredHeight();
//			child.layout(
//					contentRect.left + lp.leftMargin,
//					contentRect.top + lp.topMargin,
//					contentRect.left + width,
//					contentRect.top + height);
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
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}
	
	
	
	private final GestureDetector.SimpleOnGestureListener gestureListener =
			new GestureDetector.SimpleOnGestureListener() {
		
		private boolean movingTopLeft = false;
		private boolean movingBottomRight = false;
		
		private float lastX;
		private float lastY;
		private float diffX;
		private float diffY;
		private float rectX;
		private float rectY;
		private boolean changed;
		
		public boolean onDown(MotionEvent e) {
			
			if(isInTopLeft(e.getX(), e.getY())) {
				movingTopLeft = true;
				movingBottomRight = false;
				lastX = e.getX();
				lastY = e.getY();
				return true;
			} else if(isInBottomRight(e.getX(), e.getY())) {
				movingTopLeft = false;
				movingBottomRight = true;
				lastX = e.getX();
				lastY = e.getY();
				return true;
			} else {
				movingTopLeft = false;
				movingBottomRight = false;
				return false;
			}
			
		};
		
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			
			if(movingTopLeft) {
				
				diffX = contentRect.left - lastX;
				diffY = contentRect.top - lastY;
				
				lastX = e2.getX();
				lastY = e2.getY();
				
				rectX = lastX + diffX;
				rectY = lastY + diffY;
				
				changed = false;
				if(isLeftValid(rectX)) {
					contentRect.left = Math.round(rectX);
					changed = true;
				}
				if(isTopValid(rectY)) {
					contentRect.top = Math.round(rectY);
					changed = true;
				}
				
				if(changed) {
					ViewCompat.postInvalidateOnAnimation(ViewSkuskaLayout.this);
					requestLayout();
					return true;
				} else {
					return false;
				}
				
			} else if(movingBottomRight) {
				
				diffX = contentRect.right - lastX;
				diffY = contentRect.bottom - lastY;
				
				lastX = e2.getX();
				lastY = e2.getY();
				
				rectX = lastX + diffX;
				rectY = lastY + diffY;
				
				changed = false;
				if(isRightValid(rectX)) {
					contentRect.right = Math.round(rectX);
					changed = true;
				}
				if(isBottomValid(rectY)) {
					contentRect.bottom = Math.round(rectY);
					changed = true;
				}
				
				if(changed) {
					ViewCompat.postInvalidateOnAnimation(ViewSkuskaLayout.this);
					requestLayout();
					return true;
				} else {
					return false;
				}
				
			} else {
				
				return false;
				
			}
			
		};
		
	};
	
	
	
	private boolean isInTopLeft(final float x, final float y) {
		return contentRect.left - theGap < x && x < contentRect.left + theGap
				&& contentRect.top - theGap < y && y < contentRect.top + theGap;
	}
	
	private boolean isInBottomRight(final float x, final float y) {
		return contentRect.right - theGap < x && x < contentRect.right + theGap
				&& contentRect.bottom - theGap < y && y < contentRect.bottom + theGap;
	}
	
	
	private boolean isTopValid(final float top) {
		return getPaddingTop() + theGap < top && top < contentRect.bottom - theGap;
	}
	
	private boolean isLeftValid(final float left) {
		return getPaddingLeft() + theGap < left && left < contentRect.right - theGap;
	}
	
	private boolean isBottomValid(final float bottom) {
		return contentRect.top + theGap < bottom && bottom < getHeight() - getPaddingBottom() - theGap;
	}
	
	private boolean isRightValid(final float right) {
		return contentRect.left + theGap < right && right < getWidth() - getPaddingRight() - theGap;
	}
	
	
	
}
