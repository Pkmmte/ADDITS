package com.pk.addits;

import com.pk.addits.FragmentHome.FeedItem;

import android.app.ActionBar;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ActivityMain extends FragmentActivity implements AdapterView.OnItemClickListener
{
	ActionBar actionBar;
	public Thread feedThread;
	static Handler mHandler;
	public showProgress showP;
	FeedItem[] NewsFeed;
	static String currentFragment;
	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mListNames;
	private int[] mListImages;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		mTitle = mDrawerTitle = getTitle();
		mListNames = getResources().getStringArray(R.array.drawer_items);
		TypedArray typedArray = getResources().obtainTypedArray(R.array.city_images);
		mListImages = new int[typedArray.length()];
		for (int i = 0; i < typedArray.length(); ++i)
		{
			mListImages[i] = typedArray.getResourceId(i, 0);
		}
		typedArray.recycle();
		
		initializeNavigationDrawer();
		
		if (savedInstanceState == null)
		{
			selectItem(0);
			
			mHandler = new Handler();
			initializeFeedThread();
			feedThread.start();
		}
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		selectItem(position);
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
			}
			
			public void onDrawerOpened(View drawerView)
			{
				actionBar.setTitle(mDrawerTitle);
			}
		};
		
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}
	
	private void selectItem(int position)
	{
		// update the main content by replacing fragments
		Fragment fragment = null;
		
		switch(position)
		{
			case 0:
				fragment = new FragmentHome();
				break;
			default:
				fragment = new FragmentSample();
				Bundle args = new Bundle();
				args.putInt(FragmentSample.ARG_IMAGE_RES, mListImages[position]);
				args.putInt(FragmentSample.ARG_ACTION_BG_RES, R.drawable.ab_background);
				fragment.setArguments(args);
				break;
		}
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
		
		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(mListNames[position]);
		currentFragment = mListNames[position];
		mDrawerLayout.closeDrawer(mDrawerList);
	}
	
	@Override
	public void setTitle(CharSequence title)
	{
		mTitle = title;
		actionBar.setTitle(mTitle);
	}
	
	public void initializeFeedThread()
	{
		feedThread = new Thread()
		{
			public void run()
			{
				try
				{
					/** Fetch Server Data **/
					showP = new showProgress("Checking for updates...");
					mHandler.post(showP);
					
					Data.downloadFeed();

					/** Feed Downloaded **/
					showP = new showProgress("Updated!");
					mHandler.post(showP);
					
					NewsFeed = Data.retrieveFeed();

					/** Feed Downloaded **/
					showP = new showProgress("Retrieved!!!!!!\n" + 
					"Count: " + NewsFeed.length);
					mHandler.post(showP);
					
					for(int x = 0; x < NewsFeed.length; x++)
					{
						showP = new showProgress("NewsFeed[" + x + "]\n" + 
								"Title: " + NewsFeed[x].getTitle() + "\n" +
								"Category: " + NewsFeed[x].getCategory() + "\n" +
								"Author: " + NewsFeed[x].getAuthor() + "\n" +
								"Image: " + NewsFeed[x].getImage() + "\n" + 
								"Date: " + NewsFeed[x].getDate() + "\n" +
								"Description: " + NewsFeed[x].getDescription());
						mHandler.post(showP);
					}
					
					// Remove these during production
				}
				catch (Exception e)
				{
					Log.v("DownloadFile", "ERROR: " + e.getMessage());
				}
				
				stopThread(this);
			}
		};
	}
	
	public static synchronized void stopThread(Thread theThread)
	{
		if (theThread != null)
		{
			theThread = null;
		}
	}
	
	public class showProgress implements Runnable
	{
		String Progress;
		
		public showProgress(String p)
		{
			this.Progress = p;
		}
		
		@Override
		public void run()
		{
			Toast.makeText(ActivityMain.this, Progress, Toast.LENGTH_SHORT).show();
		}
	}
}
