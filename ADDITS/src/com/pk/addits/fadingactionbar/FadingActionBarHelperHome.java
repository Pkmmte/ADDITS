package com.pk.addits.fadingactionbar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.pk.addits.R;

public class FadingActionBarHelperHome
{
	protected static final String TAG = "FadingActionBarHelperHome";
	private Context cntxt;
	private Drawable mActionBarBackgroundDrawable;
	private int mActionBarBackgroundResId;
	private int mContentLayoutResId;
	private View mContentView;
	private ActionBar mActionBar;
	private LayoutInflater mInflater;
	private boolean mUseParallax = true;
	private int mLastDampedScroll;
	private ViewGroup mContentContainer;
	private ViewGroup mScrollView;
	private View mMarginView;
	private View mListViewBackgroundView;
	
	public FadingActionBarHelperHome actionBarBackground(int drawableResId)
	{
		mActionBarBackgroundResId = drawableResId;
		return this;
	}
	
	public FadingActionBarHelperHome actionBarBackground(Drawable drawable)
	{
		mActionBarBackgroundDrawable = drawable;
		return this;
	}
	
	public FadingActionBarHelperHome withContext(Context context)
	{
		cntxt = context;
		return this;
	}
	
	public FadingActionBarHelperHome contentLayout(int layoutResId)
	{
		mContentLayoutResId = layoutResId;
		return this;
	}
	
	public FadingActionBarHelperHome contentView(View view)
	{
		mContentView = view;
		return this;
	}
	
	public FadingActionBarHelperHome parallax(boolean value)
	{
		mUseParallax = value;
		return this;
	}
	
	public View createView(Context context)
	{
		return createView(LayoutInflater.from(context));
	}
	
	public View createView(LayoutInflater inflater)
	{
		//
		// Prepare everything
		
		mInflater = inflater;
		if (mContentView == null)
		{
			mContentView = inflater.inflate(mContentLayoutResId, null);
		}
		
		//
		// See if we are in a ListView or ScrollView scenario
		
		ListView listView = (ListView) mContentView.findViewById(android.R.id.list);
		View root;
		if (listView != null)
		{
			root = createListView(listView);
		}
		else
		{
			root = createScrollView();
		}
		
		root.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{
			@Override
			public void onGlobalLayout()
			{
			}
		});
		return root;
	}
	
	public void initActionBar(Activity activity)
	{
		mActionBar = getActionBar(activity);
		if (mActionBarBackgroundDrawable == null)
		{
			mActionBarBackgroundDrawable = activity.getResources().getDrawable(mActionBarBackgroundResId);
		}
		mActionBar.setBackgroundDrawable(mActionBarBackgroundDrawable);
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN)
		{
			mActionBarBackgroundDrawable.setCallback(mDrawableCallback);
		}
		mActionBarBackgroundDrawable.setAlpha(0);
	}
	
	protected ActionBar getActionBar(Activity activity)
	{
		if (activity instanceof Activity)
		{
			return ((Activity) activity).getActionBar();
		}
		if (activity instanceof FragmentActivity)
		{
			return ((FragmentActivity) activity).getActionBar();
		}
		if (activity instanceof ListActivity)
		{
			return ((ListActivity) activity).getActionBar();
		}
		try
		{
			Method method = activity.getClass().getMethod("getSupportActionBar");
			return (ActionBar) method.invoke(activity);
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
		throw new RuntimeException("Activity should derive from one of the ActionBarSherlock activities " + "or implement a method called getSupportActionBar");
	}
	
	private Drawable.Callback mDrawableCallback = new Drawable.Callback()
	{
		@Override
		public void invalidateDrawable(Drawable who)
		{
			mActionBar.setBackgroundDrawable(who);
		}
		
		@Override
		public void scheduleDrawable(Drawable who, Runnable what, long when)
		{
		}
		
		@Override
		public void unscheduleDrawable(Drawable who, Runnable what)
		{
		}
	};
	
	private View createScrollView()
	{
		mScrollView = (ViewGroup) mInflater.inflate(R.layout.fab__scrollview_container, null);
		
		NotifyingScrollView scrollView = (NotifyingScrollView) mScrollView.findViewById(R.id.fab__scroll_view);
		scrollView.setOnScrollChangedListener(mOnScrollChangedListener);
		
		mContentContainer = (ViewGroup) mScrollView.findViewById(R.id.fab__container);
		mContentContainer.addView(mContentView);
		// initializeGradient();
		mMarginView = mContentContainer.findViewById(R.id.fab__content_top_margin);
		
		return mScrollView;
	}
	
	private NotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener = new NotifyingScrollView.OnScrollChangedListener()
	{
		public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt)
		{
			onNewScroll(t);
		}
	};
	
	private View createListView(ListView listView)
	{
		mContentContainer = (ViewGroup) mInflater.inflate(R.layout.fab__listview_container, null);
		mContentContainer.addView(mContentView);
		
		// initializeGradient();
		
		mMarginView = new View(listView.getContext());
		mMarginView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, 0));
		listView.addHeaderView(mMarginView, null, false);
		
		// Make the background as high as the screen so that it fills regardless
		// of the amount of scroll.
		mListViewBackgroundView = mContentContainer.findViewById(R.id.fab__listview_background);
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mListViewBackgroundView.getLayoutParams();
		params.height = Utils.getDisplayHeight(listView.getContext());
		mListViewBackgroundView.setLayoutParams(params);
		
		listView.setOnScrollListener(mOnScrollListener);
		return mContentContainer;
	}
	
	private OnScrollListener mOnScrollListener = new OnScrollListener()
	{
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
		{
			View topChild = view.getChildAt(0);
			if (topChild == null)
			{
				onNewScroll(0);
			}
			else
			{
				onNewScroll(-topChild.getTop());
			}
		}
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
		}
	};
	private int mLastScrollPosition;
	
	private void onNewScroll(int scrollPosition)
	{
		if (mActionBar == null)
		{
			return;
		}
		
		int maxHeaderHeight = getMaxHeaderHeight();
		int currentHeaderHeight = maxHeaderHeight;
		
		int headerHeight = currentHeaderHeight - mActionBar.getHeight();
		float ratio = (float) Math.min(Math.max(scrollPosition, 0), headerHeight) / headerHeight;
		int newAlpha = (int) (ratio * 255);
		mActionBarBackgroundDrawable.setAlpha(newAlpha);
		
		addParallaxEffect(scrollPosition);
	}
	
	private void addParallaxEffect(int scrollPosition)
	{
		float damping = mUseParallax ? 0.5f : 1.0f;
		int dampedScroll = (int) (scrollPosition * damping);
		int offset = mLastDampedScroll - dampedScroll;
		
		if (mListViewBackgroundView != null)
		{
			offset = mLastScrollPosition - scrollPosition;
			mListViewBackgroundView.offsetTopAndBottom(offset);
		}
		
		mLastScrollPosition = scrollPosition;
		mLastDampedScroll = dampedScroll;
	}
	
	private int getMaxHeaderHeight()
	{
		Display display = ((Activity) cntxt).getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int maxHeight = (int) (size.y * 0.4);
		
		return maxHeight;
	}
}
