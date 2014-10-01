package com.flavienlaurent.vdh;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;

public class MyLayout extends ViewGroup {

	class DragCallback extends ViewDragHelper.Callback {

		@Override
		public boolean tryCaptureView(View paramView, int paramInt) {
			return true;
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
//			mTop = top;
//			mLeft = left;
		}

		@Override
	    public int clampViewPositionVertical(View child, int top, int dy) {
			Log.d("khoi", "clamp top="+top);
			return mTop = top;
	    }
		
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			Log.d("khoi", "clamp left="+left);
			return mLeft = left;
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			Log.d("khoi", "xvel="+xvel+",yvel="+yvel);
			final float dur = 0.5f;
			float finLeft = xvel * dur + releasedChild.getLeft();
			float finTop = yvel * dur + releasedChild.getTop();
			
			dragHelper.flingCapturedView(0, 0, getWidth()-releasedChild.getWidth(), getHeight()-releasedChild.getHeight());
			ViewCompat.postInvalidateOnAnimation(MyLayout.this);
		}
	}

	private View squareView;
	private ViewDragHelper dragHelper;

	private int mTop;
	private int mLeft;
	
	private int endX;
	private int endY;
	
	public MyLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		dragHelper = ViewDragHelper.create(this, 0.5f, new DragCallback());
		endX = 0;
		endY = 0;
	}

	public MyLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MyLayout(Context context) {
		super(context);
		init();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		dragHelper.processTouchEvent(event);
		return true;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		squareView = findViewById(R.id.view_square);
//		squareView.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				return false;
//			}
//		});
		
//		squareView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				int left = endX * (getWidth() - squareView.getWidth());
//				int top = endY * (getHeight() - squareView.getHeight());
//				
////				Log.d("khoi", "l="+left+",t="+top);
////				dragHelper.smoothSlideViewTo(squareView, left, top);
////				ViewCompat.postInvalidateOnAnimation(MyLayout.this);
//				
//				endX = (endX + 1) % 2;
//				endY = (endY + 1) % 2;
//			}
//		});
	}
	
	@Override
	public void computeScroll() {
		if (dragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		squareView.layout(mLeft, mTop, mLeft + squareView.getMeasuredWidth(), mTop + squareView.getMeasuredHeight());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);

        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
	}
}
