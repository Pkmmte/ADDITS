package com.pk.addits.fragment;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;

import com.pk.addits.R;
import com.pk.addits.activity.ActivityMain;
import com.pk.addits.adapter.FeedAdapter;
import com.pk.addits.data.Data;
import com.pk.addits.model.Article;
import com.pk.addits.view.ParallaxListView;

public class FragmentHome extends Fragment
{
	static ParallaxListView list;
	static FrameLayout frame;
	static FeedAdapter adapter;
	static Context cntxt;
	static int scrollPosition;
	static int topOffset;
	
	static int currentSlide;
	static Timer timer;
	Handler timeHandler;
	long startTime;
	
	static Fragment fragSlide;
	static FragmentManager fm;
	static Integer currentSlideID;
	
	private static int returnCount; // To prevent a StackOverflowError
	
	public static FragmentHome newInstance(int lastScrollPosition, int lastTopOffset)
	{
		FragmentHome f = new FragmentHome();
		Bundle bdl = new Bundle();
		
		bdl.putInt("Last Scroll Position", lastScrollPosition);
		bdl.putInt("Last Top Offset", lastTopOffset);
		f.setArguments(bdl);
		
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.list, container, false);
		setHasOptionsMenu(true);
		cntxt = getActivity();
		
		list = (ParallaxListView) view.findViewById(R.id.ListView);
		list.setDividerHeight(0);
		View header = (View) inflater.inflate(R.layout.header, list, false);
		//frame = new FrameLayout(getActivity());
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, Data.getHeightByPercent(getActivity(), 0.35));
		//frame.setLayoutParams(layoutParams);
		//frame.setId(R.id.slider_content);
		//frame.setClickable(true);
		list.setParallaxHeader(header);
		list.addHeaderView(header, null, true);
		header.setLayoutParams(layoutParams);
		adapter = new FeedAdapter(getActivity(), ActivityMain.articleList);
		list.setAdapter(adapter);
		
		currentSlide = 1;
		currentSlideID = 1;
		scrollPosition = 0;
		topOffset = 0;
		returnCount = 0;
		
		Bundle args = getArguments();
		if (args != null)
		{
			scrollPosition = args.getInt("Last Scroll Position", 0);
			topOffset = args.getInt("Last Top Offset", 0);
		}
		
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		fm = getChildFragmentManager();
		
		timer = new Timer();
		timeHandler = new Handler(new Callback()
		{
			@Override
			public boolean handleMessage(Message msg)
			{
				// if (mFadingHelper.mLastScrollPosition < 10 && ActivityMain.articleList != null)
				populateSlide();
				
				return false;
			}
		});
		timer.schedule(new firstTask(), 5000, 7000);
		
		populateSlide();
		updateState();
		
		list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long index)
			{
				if (position > 0)
				{
					int ID = ActivityMain.articleList.get(position - 1).getID();
					Article article = ActivityMain.db.getArticle(ID);
					
					/*
					 * String Title = ActivityMain.articleList.get(ID).getTitle(); String Description = ActivityMain.articleList.get(ID).getDescription(); String Content =
					 * ActivityMain.articleList.get(ID).getContent(); String CommentFeed = ActivityMain.articleList.get(ID).getCommentFeed(); String Author =
					 * ActivityMain.articleList.get(ID).getAuthor(); String Date = ActivityMain.articleList.get(ID).getDate(); String Category = ActivityMain.articleList.get(ID).getCategory(); String
					 * Image = ActivityMain.articleList.get(ID).getImage(); String URL = ActivityMain.articleList.get(ID).getURL(); boolean Favorite = ActivityMain.articleList.get(ID).isFavorite();
					 * boolean Read = ActivityMain.articleList.get(ID).isRead();
					 * 
					 * Article Article = new Article(ID, Title, Description, Content, CommentFeed, Author, Date, Category, Image, URL, Favorite, Read);
					 */
					ActivityMain.callArticle(article, list.getFirstVisiblePosition(), (list.getChildAt(0) == null) ? 0 : list.getChildAt(0).getTop());
				}
				else if (currentSlideID != null)
				{
					// int ID = ActivityMain.articleList.get(currentSlideID - 1).getID();
					Article article = ActivityMain.db.getArticle(currentSlideID);
					
					/*
					 * String Title = ActivityMain.articleList.get(currentSlideID).getTitle(); String Description = ActivityMain.articleList.get(currentSlideID).getDescription(); String Content =
					 * ActivityMain.articleList.get(currentSlideID).getContent(); String CommentFeed = ActivityMain.articleList.get(currentSlideID).getCommentFeed(); String Author =
					 * ActivityMain.articleList.get(currentSlideID).getAuthor(); String Date = ActivityMain.articleList.get(currentSlideID).getDate(); String Category =
					 * ActivityMain.articleList.get(currentSlideID).getCategory(); String Image = ActivityMain.articleList.get(currentSlideID).getImage(); String URL =
					 * ActivityMain.articleList.get(currentSlideID).getURL(); boolean Favorite = ActivityMain.articleList.get(currentSlideID).isFavorite(); boolean Read =
					 * ActivityMain.articleList.get(currentSlideID).isRead();
					 * 
					 * Article Article = new Article(ID, Title, Description, Content, CommentFeed, Author, Date, Category, Image, URL, Favorite, Read);
					 */
					ActivityMain.callArticle(article, 0, 0);
				}
			}
		});
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		timer.cancel();
		timer.purge();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		menu.clear();
		inflater.inflate(R.menu.home, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.Website_Label:
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(Data.MAIN_URL));
				startActivity(i);
				return true;
			case R.id.Settings_Label:
				ActivityMain.callSettings();
				return true;
			default:
				
				return super.onOptionsItemSelected(item);
		}
	}
	
	public static void updateState()
	{
		if (ActivityMain.articleList != null)
		{
			adapter.notifyDataSetChanged();
			list.setSelectionFromTop(scrollPosition, topOffset);
			populateSlide();
		}
	}
	
	public static void populateSlide()
	{
		String sTitle = "";
		String sAuthor = "";
		String sDate = "";
		String sImage = "";
		String sCategory = "";
		
		if (ActivityMain.articleList != null && ActivityMain.articleList.size() > 0)
		{
			Random generator = new Random();
			int r = generator.nextInt(ActivityMain.articleList.size());
			sTitle = ActivityMain.articleList.get(r).getTitle();
			sAuthor = ActivityMain.articleList.get(r).getAuthor();
			sDate = Data.parseDate(cntxt, ActivityMain.articleList.get(r).getDate());
			sImage = ActivityMain.articleList.get(r).getImage();
			sCategory = ActivityMain.articleList.get(r).getCategory();
			currentSlideID = ActivityMain.articleList.get(r).getID();
			
			if (sImage.length() < 1 && returnCount < 5)
			{
				returnCount++;
				populateSlide();
				return;
			}
		}
		
		if (!sCategory.equalsIgnoreCase("DAILY SAVER"))
			fragSlide = FragmentSlider.newInstance(sTitle, sAuthor, sDate, sImage);
		else if (returnCount < 5)
		{
			returnCount++;
			populateSlide();
			return;
		}
		
		FragmentTransaction trans = fm.beginTransaction();
		trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_out, R.anim.fade_in);
		trans.replace(R.id.slider_content, fragSlide);
		trans.commit();
		returnCount = 0;
	}
	
	class firstTask extends TimerTask
	{
		@Override
		public void run()
		{
			timeHandler.sendEmptyMessage(0);
		}
	};
}
