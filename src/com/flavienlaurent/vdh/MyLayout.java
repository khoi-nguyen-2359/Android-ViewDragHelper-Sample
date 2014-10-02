package com.flavienlaurent.vdh;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;

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
			spring.setEndValue(1);
			return true;
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			// mTop = top;
			// mLeft = left;
		}

		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			Log.d("khoi", "clamp top=" + top);
			return mTop = top;
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			Log.d("khoi", "clamp left=" + left);
			return mLeft = left;
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			spring.setEndValue(0);

			Log.d("khoi", "xvel=" + xvel + ",yvel=" + yvel);
			final float dur = 0.5f;
			float finLeft = xvel * dur + releasedChild.getLeft();
			float finTop = yvel * dur + releasedChild.getTop();

			dragHelper.flingCapturedView(0, 0,
					getWidth() - releasedChild.getWidth(), getHeight()
							- releasedChild.getHeight());
			ViewCompat.postInvalidateOnAnimation(MyLayout.this);
		}
	}

	private View squareView;
	private ViewDragHelper dragHelper;

	private int mTop;
	private int mLeft;

	private int endX;
	private int endY;
	private Spring spring;

	public MyLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		dragHelper = ViewDragHelper.create(this, 0.75f, new DragCallback());
		endX = 0;
		endY = 0;

		// Create a system to run the physics loop for a set of springs.
		SpringSystem springSystem = SpringSystem.create();

		// Add a spring to the system.
		spring = springSystem.createSpring();

		// Add a listener to observe the motion of the spring.
		spring.addListener(new SimpleSpringListener() {

			@Override
			public void onSpringUpdate(Spring spring) {
				// You can observe the updates in the spring
				// state by asking its current value in onSpringUpdate.
				float mappedValue = (float) SpringUtil
						.mapValueFromRangeToRange(spring.getCurrentValue(), 0,
								1, 1, 0.8);
				squareView.setScaleX(mappedValue);
				squareView.setScaleY(mappedValue);
			}
		});

		// Set the spring in motion; moving from 0 to 1
		// spring.setEndValue(1);
		SpringConfig springConfig = spring.getSpringConfig();
//		 springConfig.friction = 10;
		springConfig.tension = 1000;
		Log.d("khoi", "tension=" + springConfig.tension + ",friction="
				+ springConfig.friction);
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
		// squareView.setOnTouchListener(new OnTouchListener() {
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// int action = event.getAction();
		// switch (action) {
		// case MotionEvent.ACTION_DOWN:
		// spring.setEndValue(1);
		// break;
		// case MotionEvent.ACTION_UP:
		// spring.setEndValue(0);
		// break;
		// case MotionEvent.ACTION_MOVE:
		// //
		// squareView.offsetLeftAndRight((int)event.getX()-squareView.getWidth()/2-squareView.getLeft());
		// //
		// squareView.offsetTopAndBottom((int)event.getY()-squareView.getHeight()/2-squareView.getTop());
		// // spring.setEndValue(0);
		// break;
		// default:
		// break;
		// }
		//
		// return true;
		// }
		// });

	}

	@Override
	public void computeScroll() {
		if (dragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		squareView.layout(mLeft, mTop, mLeft + squareView.getMeasuredWidth(),
				mTop + squareView.getMeasuredHeight());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		measureChildren(widthMeasureSpec, heightMeasureSpec);

		int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
		int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

		setMeasuredDimension(
				resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
				resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
	}
}
