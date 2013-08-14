package com.pk.addits.activity;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.XmlDom;
import com.pk.addits.R;
import com.pk.addits.data.Data;
import com.pk.addits.data.DatabaseHelper;
import com.pk.addits.fragment.FragmentArticle;
import com.pk.addits.fragment.FragmentCustomization;
import com.pk.addits.fragment.FragmentDeveloperFocus;
import com.pk.addits.fragment.FragmentHome;
import com.pk.addits.fragment.FragmentLoading;
import com.pk.addits.fragment.FragmentMerchandise;
import com.pk.addits.fragment.FragmentReviews;
import com.pk.addits.fragment.FragmentSearch;
import com.pk.addits.fragment.FragmentSettings;
import com.pk.addits.fragment.FragmentSupport;
import com.pk.addits.fragment.FragmentTutorials;
import com.pk.addits.model.Article;

public class ActivityMain extends FragmentActivity implements AdapterView.OnItemClickListener
{
	public static DatabaseHelper db = null;
	public static List<Article> articleList;
	
	private static Context cntxt;
	
	private static ActionBar actionBar;
	private SharedPreferences prefs;
	private Thread feedThread;
	private Thread emptyFeedThread;
	private Handler mHandler;
	private static AQuery aq;
	private static boolean emptyFeed;
	private static int lastHomeScrollPosition;
	private static int lastHomeTopOffset;
	private static int backPress;
	public static boolean inBackground;
	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	
	// public static Article[] NewsFeed;
	public static String currentFragment;
	public static boolean articleShowing;
	
	private CharSequence mDrawerTitle;
	private static CharSequence mTitle;
	private String[] mListNames;
	
	private ProgressBar ProgressBar;
	private View ProgressFinished;
	private LinearLayout Loading;
	private TextView LoadingText;
	private long lastUpdateCheckTime;
	private int updateCheckInterval;
	
	private static FragmentManager fragmentManager;
	private boolean newFound;
	private static boolean fragmentLoaded;
	private boolean fromWidget;
	
	public static boolean imageExpanded;
	public static View contentFrameColor;
	public static RelativeLayout container;
	public static ImageView expandedImageView;
	public static Animator mCurrentAnimator;
	public static int mShortAnimationDuration;
	
	private int savedBuild;
	private static boolean supportFragmentActive;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		db = DatabaseHelper.getInstance(ActivityMain.this);
		cntxt = ActivityMain.this;
		
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setIcon(R.drawable.ic_ab_logo);
		if (Data.BETA)
			actionBar.setSubtitle("BETA");
		
		prefs = getSharedPreferences(Data.PREFS_TAG, 0);
		savedBuild = prefs.getInt(Data.PREF_TAG_SAVED_BUILD, 0);
		lastUpdateCheckTime = prefs.getLong(Data.PREF_TAG_LAST_UPDATE_CHECK_TIME, 0);
		fragmentManager = getSupportFragmentManager();
		if (savedInstanceState == null)
		{
			emptyFeed = false;
			lastHomeScrollPosition = 0;
			lastHomeTopOffset = 0;
			backPress = 0;
			supportFragmentActive = false;
			currentFragment = "Loading";
			fragmentLoaded = false;
			fromWidget = false;
		}
		
		String UpdateInterval = prefs.getString(Data.PREF_TAG_UPDATE_INTERVAL, "Hourly");
		if (UpdateInterval.equals("Manual"))
			updateCheckInterval = 0;
		else if (UpdateInterval.equals("15 Minutes"))
			updateCheckInterval = 15 * 60 * 1000;
		else if (UpdateInterval.equals("30 Minutes"))
			updateCheckInterval = 30 * 60 * 1000;
		else if (UpdateInterval.equals("Hourly"))
			updateCheckInterval = 60 * 60 * 1000;
		else if (UpdateInterval.equals("6 Hours"))
			updateCheckInterval = 6 * 60 * 60 * 1000;
		else if (UpdateInterval.equals("Daily"))
			updateCheckInterval = 24 * 60 * 60 * 1000;
		
		contentFrameColor = findViewById(R.id.content_frame_color);
		container = (RelativeLayout) findViewById(R.id.container);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		Loading = (LinearLayout) findViewById(R.id.loading);
		LoadingText = (TextView) findViewById(R.id.loadingText);
		ProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		ProgressFinished = findViewById(R.id.progressFinished);
		expandedImageView = (ImageView) findViewById(R.id.expanded_image);
		
		mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
		mTitle = mDrawerTitle = getTitle();
		mListNames = getResources().getStringArray(R.array.drawer_items);
		initializeNavigationDrawer();
		aq = new AQuery(ActivityMain.this);
		newFound = false;
		imageExpanded = false;
		inBackground = false;
		
		if (getIntent().hasExtra(Data.EXTRA_ID))
		{
			Toast.makeText(ActivityMain.this, "EXTRA_ID: " + getIntent().getExtras().getInt(Data.EXTRA_ID), Toast.LENGTH_SHORT).show();
			Article article = db.getArticle(getIntent().getExtras().getInt(Data.EXTRA_ID));
			Toast.makeText(ActivityMain.this, "ID: " + article.getID(), Toast.LENGTH_SHORT).show();
			fromWidget = true;
			callArticle(article, 0, 0);
		}
		if (savedInstanceState == null)
		{
			mHandler = new Handler();
			
			if (!fromWidget && !supportFragmentActive)
				selectItem(225);
			
			if (savedBuild < 1)
				ask4Money();
			
			if (db.getArticleCount() < 5)
				emptyFeed = true;
			else
				emptyFeed = false;
			
			if (!emptyFeed)
			{
				if (feedThread == null)
				{
					initializeFeedThread();
					feedThread.start();
				}
				else if (!feedThread.isAlive())
				{
					initializeFeedThread();
					feedThread.start();
				}
			}
			else
			{
				initializeEmptyFeedThread();
				emptyFeedThread.start();
			}
			
			saveCurrentBuild();
		}
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		inBackground = true;
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		inBackground = false;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		if (articleShowing)
			FragmentArticle.menuVisibility(mDrawerLayout.isDrawerOpen(mDrawerList));
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (currentFragment.equals("Home") && articleShowing)
			{
				if (imageExpanded)
				{
					expandedImageView.performClick();
				}
				else
				{
					Fragment fragment = FragmentHome.newInstance(lastHomeScrollPosition, lastHomeTopOffset);
					mTitle = "Home";
					actionBar.setTitle(mTitle);
					articleShowing = false;
					FragmentTransaction transaction = fragmentManager.beginTransaction();
					transaction.setCustomAnimations(R.anim.plus_page_in_left, R.anim.plus_page_out_left);
					transaction.replace(R.id.content_frame, fragment);
					transaction.commit();
				}
				
				return true;
			}
			else if (currentFragment.equals("Settings") || currentFragment.equals("Search"))
			{
				Fragment fragment = new FragmentHome();
				mTitle = "Home";
				actionBar.setTitle(mTitle);
				currentFragment = "Home";
				articleShowing = false;
				FragmentTransaction transaction = fragmentManager.beginTransaction();
				transaction.setCustomAnimations(R.anim.in_from_up, R.anim.out_to_down);
				transaction.replace(R.id.content_frame, fragment);
				transaction.commit();
				return true;
			}
			else if (currentFragment.equals("Android Dissected"))
			{
				return true;
			}
			else
			{
				if (backPress < 1)
				{
					backPress++;
					Toast.makeText(ActivityMain.this, "Press back once more to exit", Toast.LENGTH_SHORT).show();
					return true;
				}
				else
					finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		if (emptyFeed)
		{
			Toast.makeText(ActivityMain.this, "Wait for everything to finish loading first!", Toast.LENGTH_LONG).show();
			mDrawerLayout.closeDrawers();
		}
		else
		{
			selectItem(position);
		}
	}
	
	private void initializeNavigationDrawer()
	{
		// Set up custom shadow overlay
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		
		// Set up drawer content & listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mListNames));
		mDrawerList.setOnItemClickListener(this);
		
		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		)
		{
			public void onDrawerClosed(View view)
			{
				actionBar.setTitle(mTitle);
				invalidateOptionsMenu();
			}
			
			public void onDrawerOpened(View drawerView)
			{
				actionBar.setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}
	
	private void selectItem(int position)
	{
		Fragment fragment = null;
		
		switch (position)
		{
			case 0:
				fragment = new FragmentHome();
				break;
			case 1:
				fragment = new FragmentCustomization();
				break;
			case 2:
				fragment = new FragmentDeveloperFocus();
				break;
			case 3:
				fragment = new FragmentMerchandise();
				break;
			case 4:
				fragment = new FragmentReviews();
				break;
			case 5:
				fragment = new FragmentTutorials();
				break;
			case 225:
				fragment = new FragmentLoading();
				currentFragment = "Loading";
				break;
		}
		
		articleShowing = false;
		backPress = 0;
		fragmentManager.beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.content_frame, fragment).commit();
		if (position != 225 && !supportFragmentActive)
		{
			mDrawerList.setItemChecked(position, true);
			setTitle(mListNames[position]);
			currentFragment = mListNames[position];
			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}
	
	@Override
	public void setTitle(CharSequence title)
	{
		mTitle = title;
		actionBar.setTitle(mTitle);
	}
	
	/** Needed for update purposes **/
	private void saveCurrentBuild()
	{
		Editor editor = prefs.edit();
		editor.putInt(Data.PREF_TAG_SAVED_BUILD, Data.BUILD_TAG);
		editor.commit();
	}
	
	public static void checkNewContent(Context context)
	{
		if (Data.isNetworkConnected(context))
		{
			AjaxCallback<XmlDom> cb = new AjaxCallback<XmlDom>();
			cb.url(Data.FEED_URL).type(XmlDom.class).handler(cntxt, "checkNewContent");
			aq.ajax(cb);
		}
		else
			Toast.makeText(context, "Unable to establish a network connection!", Toast.LENGTH_SHORT).show();
	}
	
	public static void callArticle(Article article, int scrollPosition, int topOffset)
	{
		Fragment fragment = FragmentArticle.newInstance(article);
		mTitle = article.getTitle();
		articleShowing = true;
		backPress = 0;
		lastHomeScrollPosition = scrollPosition;
		lastHomeTopOffset = topOffset;
		
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.plus_page_in_right, R.anim.plus_page_out_right);
		transaction.replace(R.id.content_frame, fragment);
		transaction.commit();
	}
	
	public static void callSettings()
	{
		Fragment fragment = new FragmentSettings();
		mTitle = "Settings";
		actionBar.setTitle(mTitle);
		currentFragment = "Settings";
		articleShowing = false;
		backPress = 0;
		
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.in_from_down, R.anim.out_to_up);
		transaction.replace(R.id.content_frame, fragment);
		transaction.commit();
	}
	
	public static void callSearch(String query)
	{
		Fragment fragment = FragmentSearch.newInstance(query);
		mTitle = "Search";
		actionBar.setTitle(mTitle);
		currentFragment = "Search";
		articleShowing = false;
		backPress = 0;
		
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.in_from_down, R.anim.out_to_up);
		transaction.replace(R.id.content_frame, fragment);
		transaction.commit();
	}
	
	private void ask4Money()
	{
		supportFragmentActive = true;
		// Toast.makeText(ActivityMain.this, "Spare Change?", Toast.LENGTH_SHORT).show();
		Fragment fragSupport = new FragmentSupport();
		mTitle = "Android Dissected";
		actionBar.setTitle(mTitle);
		currentFragment = "Android Dissected";
		articleShowing = false;
		backPress = 0;
		
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.in_from_down, R.anim.none);
		transaction.replace(R.id.content_frame, fragSupport);
		transaction.commit();
	}
	
	public static void showCurrentFragment()
	{
		Fragment fragment = null;
		supportFragmentActive = false;
		
		if (!fragmentLoaded || emptyFeed == true)
		{
			fragment = new FragmentLoading();
			mTitle = "Loading";
			actionBar.setTitle(mTitle);
			currentFragment = "Loading";
		}
		else
		{
			fragment = new FragmentHome();
			mTitle = "Home";
			actionBar.setTitle(mTitle);
			currentFragment = "Home";
		}
		articleShowing = false;
		backPress = 0;
		
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.none, R.anim.out_to_up);
		transaction.replace(R.id.content_frame, fragment);
		transaction.commit();
	}
	
	/*
	 * public void downloadFeed() { JSONObject jObject = Data.getJSON(); try { JSONArray jArray = jObject.getJSONArray("item"); } catch (JSONException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 */
	
	public void downloadFeed(String url, XmlDom xml, AjaxStatus status)
	{
		try
		{
			List<XmlDom> entries = xml.tags("item");
			int count = 0;
			
			for (XmlDom item : entries)
			{
				String Title = item.text("title");
				String Description = Html.fromHtml(item.text("description").replaceAll("<img.+?>", "")).toString();
				String Content = item.text("content:encoded");
				String CommentFeed = item.text("wfw:commentRss");
				String Author = item.text("dc:creator");
				String Date = item.text("pubDate");
				String Category = item.text("category");
				String Image = Data.pullLinks(item.text("description"));
				String URL = item.text("link");
				
				articleList.add(new Article(count, Title, Description, Content, CommentFeed, Author, Date, Category, Image, URL, false, false));
				count++;
			}
		}
		catch (Exception e)
		{
			Log.v("Error downloading feed.", "Unstable internet connection.");
		}
		
		return;
	}
	
	public void checkNewContent(String url, XmlDom xml, AjaxStatus status)
	{
		Log.v("RAWR", "DID THIS WORK?!?!");
		mHandler.post(new showProgress("Checking for new content...", true, true, false));
	}
	
	public void checkNew(String url, XmlDom xml, AjaxStatus status)
	{
		List<XmlDom> entries = xml.tags("item");
		
		for (XmlDom item : entries)
		{
			String date = item.text("pubDate");
			if (Data.isNewerDate(date, articleList.get(0).getDate()))
			{
				newFound = true;
				Toast.makeText(ActivityMain.this, "I found a new guy! :D", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}
	
	private void initializeEmptyFeedThread()
	{
		emptyFeedThread = new Thread()
		{
			public void run()
			{
				try
				{
					if (Data.hasActiveInternetConnection(ActivityMain.this))
					{
						if (!inBackground && !supportFragmentActive)
							mHandler.post(new showProgress2("Downloading content..."));
						
						try
						{
							articleList = new ArrayList<Article>();
							AjaxCallback<XmlDom> cb = new AjaxCallback<XmlDom>();
							cb.url(Data.FEED_URL).type(XmlDom.class).handler(ActivityMain.this, "downloadFeed");
							aq.sync(cb);
							
							if (!inBackground && !supportFragmentActive)
								mHandler.post(new showProgress2("Writing content..."));
							for (int x = 0; x < articleList.size(); x++)
								db.addArticle(articleList.get(x));
							
							if (!inBackground && !supportFragmentActive)
							{
								mHandler.post(new showProgress2("Everything is up to date!"));
								mHandler.postDelayed(new showProgress2(""), 3500);
							}
						}
						catch (Exception e)
						{
							if (!inBackground && !supportFragmentActive)
								mHandler.post(new showProgress2("Error downloading feed!"));
						}
					}
					else if (Data.isNetworkConnected(ActivityMain.this) && !inBackground && !supportFragmentActive)
						mHandler.post(new showProgress2("Error connecting to AndroidDissected.com!"));
					else if (!inBackground && !supportFragmentActive)
						mHandler.post(new showProgress2("An internet connection is required!!!"));
				}
				catch (Exception e)
				{
					Log.v("DownloadFile", "ERROR: " + e.getMessage());
					
					try
					{
						if (!inBackground && !supportFragmentActive)
							mHandler.post(new showProgress2("Oh noez!\nAn unknown error occurred!!!"));
					}
					catch (Exception ee)
					{
						Log.v("Show Message", "ERROR: " + e.getMessage());
					}
				}
				
				stopThread(this);
			}
		};
	}
	
	private void initializeFeedThread()
	{
		feedThread = new Thread()
		{
			public void run()
			{
				try
				{
					Log.v("Loading Feed!", "");
					if (!inBackground)
						mHandler.post(new showProgress("Loading feed...", true, true, false));
					
					articleList = db.getAllArticles();
					Log.v("Feed Loaded! ", "" + articleList.size());
					
					if (!fromWidget && !inBackground)
						mHandler.post(new showHome());
					/** Fetch Website Data **/
					
					if (updateCheckInterval > 0 && lastUpdateCheckTime + updateCheckInterval < System.currentTimeMillis() && Data.hasActiveInternetConnection(ActivityMain.this))
					{
						lastUpdateCheckTime = System.currentTimeMillis();
						Editor editor = prefs.edit();
						editor.putLong(Data.PREF_TAG_LAST_UPDATE_CHECK_TIME, lastUpdateCheckTime);
						editor.commit();
						
						if (!inBackground)
							mHandler.post(new showProgress("Checking for new content...", true, false, false));
						
						try
						{
							AjaxCallback<XmlDom> cb = new AjaxCallback<XmlDom>();
							cb.url(Data.FEED_URL).type(XmlDom.class).handler(ActivityMain.this, "checkNew");
							aq.sync(cb);
							
							if (newFound)
							{
								if (!inBackground)
									mHandler.post(new showProgress("Updating content...", true, false, false));
								
								// TODO Make sure read/favorite params don't get overwritten
								AjaxCallback<XmlDom> cbs = new AjaxCallback<XmlDom>();
								cbs.url(Data.FEED_URL).type(XmlDom.class).handler(ActivityMain.this, "downloadFeed");
								aq.sync(cbs);
								
								if (!inBackground)
									mHandler.post(new showProgress("Writing content...", true, false, false));
								
								for (int x = 0; x < articleList.size(); x++)
								{
									db.addArticle(articleList.get(x));
								}
								Log.v("Happy Face", " New stuff found!");
							}
							else
								Log.v("Sad Face", " No new found...");
							
							if (!inBackground)
								mHandler.post(new showProgress("Everything is up to date!", true, false, true));
							
							if (newFound)
							{
								while (true)
								{
									if (fragmentLoaded)
									{
										if (!inBackground)
											mHandler.post(new showProgress("", false, false, false));
										break;
									}
								}
							}
							
							if (!inBackground)
								mHandler.postDelayed(new showProgress("Everything is up to date!", false, true, true), 4000);
						}
						catch (Exception e)
						{
							if (!inBackground)
							{
								mHandler.post(new showProgress("Error updating feed!", true, false, true));
								mHandler.postDelayed(new showProgress("Error updating feed!", false, true, true), 2500);
							}
						}
					}
					else if (!inBackground)
					{
						mHandler.post(new showProgress("Loading feed...", true, false, true));
						mHandler.postDelayed(new showProgress("Loading feed...", false, true, true), 2500);
					}
				}
				catch (Exception e)
				{
					Log.v("DownloadFile", "ERROR: " + e.getMessage());
				}
				
				stopThread(this);
			}
		};
	}
	
	private synchronized void stopThread(Thread theThread)
	{
		if (theThread != null)
		{
			theThread = null;
		}
	}
	
	public class showProgress implements Runnable
	{
		boolean Active;
		boolean Animate;
		boolean Finished;
		String Progress;
		
		public showProgress(String p, boolean a, boolean aa, boolean f)
		{
			this.Active = a;
			this.Animate = aa;
			this.Finished = f;
			this.Progress = p;
		}
		
		@Override
		public void run()
		{
			if (Progress.length() > 0)
			{
				LoadingText.setText(Progress);
				
				if (Active)
				{
					Loading.setVisibility(View.VISIBLE);
					if (Animate)
					{
						Animation a = AnimationUtils.loadAnimation(ActivityMain.this, R.anim.loading_slide_up);
						Loading.startAnimation(a);
					}
					if (Finished)
					{
						ProgressBar.setVisibility(View.GONE);
						ProgressFinished.setVisibility(View.VISIBLE);
					}
				}
				else
				{
					Loading.setVisibility(View.GONE);
					if (Animate)
					{
						Animation a = AnimationUtils.loadAnimation(ActivityMain.this, R.anim.loading_slide_down);
						Loading.startAnimation(a);
					}
					if (Finished)
					{
						ProgressBar.setVisibility(View.GONE);
						ProgressFinished.setVisibility(View.VISIBLE);
					}
				}
			}
			else
			{
				if (currentFragment.equals("Home") && !articleShowing && !fromWidget)
				{
					if (!inBackground)
						FragmentHome.updateState();
				}
			}
		}
	}
	
	public class showProgress2 implements Runnable
	{
		String Progress;
		
		public showProgress2(String p)
		{
			this.Progress = p;
		}
		
		@Override
		public void run()
		{
			if (Progress.length() > 0)
			{
				FragmentLoading.setLoadingText(Progress);
			}
			else
			{
				fragmentLoaded = true;
				emptyFeed = false;
				selectItem(0);
			}
		}
	}
	
	public class showHome implements Runnable
	{
		@Override
		public void run()
		{
			selectItem(0);
			fragmentLoaded = true;
		}
	}
}
