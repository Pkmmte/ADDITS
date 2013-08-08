package com.pk.addits.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;

public class ParallaxListView extends ListView implements OnScrollListener
{
	
	View header;
	int mDrawableMaxHeight = -1;
	int mHeaderHeight = -1;
	public final static double NO_ZOOM = 1;
	public final static double ZOOM_X2 = 2;
	
	private interface OnOverScrollByListener
	{
		public boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent);
	}
	
	private interface OnTouchEventListener
	{
		public void onTouchEvent(MotionEvent ev);
	}
	
	public ParallaxListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}
	
	public ParallaxListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public ParallaxListView(Context context)
	{
		super(context);
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
	}
	
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent)
	{
		boolean isCollapseAnimation = false;
		
		isCollapseAnimation = scrollByListener.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent) || isCollapseAnimation;
		
		return isCollapseAnimation ? true : super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
	{
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		touchListener.onTouchEvent(ev);
		return super.onTouchEvent(ev);
	}
	
	public void setParallaxHeader(View v)
	{
		header = v;
	}
	
	public void setViewsBounds(double zoomRatio)
	{
		if (mHeaderHeight == -1)
		{
			mHeaderHeight = header.getHeight();
			//double ratio = ((double) header.getDrawable().getIntrinsicWidth()) / ((double) mImageView.getWidth());
			
			//mDrawableMaxHeight = (int) ((mImageView.getDrawable().getIntrinsicHeight() / ratio) * (zoomRatio > 1 ? zoomRatio : 1));
		}
	}
	
	private OnOverScrollByListener scrollByListener = new OnOverScrollByListener()
	{
		@Override
		public boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent)
		{
			if (header.getHeight() <= mDrawableMaxHeight && isTouchEvent)
			{
				if (deltaY < 0)
				{
					if (header.getHeight() - deltaY / 2 >= mHeaderHeight)
					{
						header.getLayoutParams().height = header.getHeight() - deltaY / 2 < mDrawableMaxHeight ? header.getHeight() - deltaY / 2 : mDrawableMaxHeight;
						header.requestLayout();
					}
				}
				else
				{
					if (header.getHeight() > mHeaderHeight)
					{
						header.getLayoutParams().height = header.getHeight() - deltaY > mHeaderHeight ? header.getHeight() - deltaY : mHeaderHeight;
						header.requestLayout();
						return true;
					}
				}
			}
			return false;
		}
	};
	
	private OnTouchEventListener touchListener = new OnTouchEventListener()
	{
		@Override
		public void onTouchEvent(MotionEvent ev)
		{
			if (ev.getAction() == MotionEvent.ACTION_UP)
			{
				if (mHeaderHeight - 1 < header.getHeight())
				{
					ResetAnimimation animation = new ResetAnimimation(header, mHeaderHeight);
					animation.setDuration(300);
					header.startAnimation(animation);
				}
			}
		}
	};
	
	public class ResetAnimimation extends Animation
	{
		int targetHeight;
		int originalHeight;
		int extraHeight;
		View mView;
		
		protected ResetAnimimation(View view, int targetHeight)
		{
			this.mView = view;
			this.targetHeight = targetHeight;
			originalHeight = view.getHeight();
			extraHeight = this.targetHeight - originalHeight;
		}
		
		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t)
		{
			
			int newHeight;
			newHeight = (int) (targetHeight - extraHeight * (1 - interpolatedTime));
			mView.getLayoutParams().height = newHeight;
			mView.requestLayout();
		}
		
		@Override
		public void initialize(int width, int height, int parentWidth, int parentHeight)
		{
			super.initialize(width, height, parentWidth, parentHeight);
		}
		
	}
}