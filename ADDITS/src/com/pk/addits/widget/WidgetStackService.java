package com.pk.addits.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.pk.addits.R;
import com.pk.addits.data.Data;
import com.pk.addits.data.DatabaseHelper;
import com.pk.addits.model.Article;
import com.squareup.picasso.Picasso;

public class WidgetStackService extends RemoteViewsService
{
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent)
	{
		return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
	}
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory
{
	private static final int mCount = 10;
	public static DatabaseHelper db = null;
	private List<Article> articleList = new ArrayList<Article>();
	//private int Count = 0;
	private Context mContext;
	//private int mAppWidgetId;
	
	public StackRemoteViewsFactory(Context context, Intent intent)
	{
		mContext = context;
		//mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		db = new DatabaseHelper(context);
	}
	
	public void onCreate()
	{
		// In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
		// for example downloading or creating content etc, should be deferred to onDataSetChanged()
		// or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
		/**boolean emptyFeed;
		
		if(db.getArticleCount() < 5)
			emptyFeed = true;
		else
			emptyFeed = false;
		
		if (!emptyFeed)
		{
			articleList = db.getAllArticles();
		}*/
		articleList = db.getAllArticles();
		
		// We sleep for 3 seconds here to show how the empty view appears in the interim.
		// The empty view is set in the StackWidgetProvider and should be a sibling of the
		// collection view.
		try
		{
			Thread.sleep(3000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public void onDestroy()
	{
		// In onDestroy() you should tear down anything that was setup for your data source,
		// eg. cursors, connections, etc.
		articleList.clear();
		db.close();
	}
	
	public int getCount()
	{
		return mCount;
	}
	
	public RemoteViews getViewAt(int position)
	{
		// position will always range from 0 to getCount() - 1.
		
		// We construct a remote views item based on our widget item xml file, and set the
		// text based on the position.
		RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_stack_item);
		rv.setTextViewText(R.id.widget_item, articleList.get(position).getTitle());
		
		// Next, we set a fill-intent which will be used to fill-in the pending intent template
		// which is set on the collection view in StackWidgetProvider.
		Bundle extras = new Bundle();
		extras.putInt(Data.EXTRA_ID, articleList.get(position).getID());
		Intent fillInIntent = new Intent();
		fillInIntent.putExtras(extras);
		rv.setOnClickFillInIntent(R.id.widget_parent, fillInIntent);
		
		// You can do heaving lifting in here, synchronously. For example, if you need to
		// process an image, fetch something from the network, etc., it is ok to do it here,
		// synchronously. A loading view will show up in lieu of the actual contents in the
		// interim.
		try
		{
			String IMG = articleList.get(position).getImage();
			String imgURL = IMG.substring(0, IMG.lastIndexOf(".")) + "-150x150" + IMG.substring(IMG.lastIndexOf("."), IMG.length());
			
			Bitmap bm = Picasso.with(mContext).load(imgURL).get();
			rv.setImageViewBitmap(R.id.widget_image, bm);
			Thread.sleep(500);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// Return the remote views object.
		return rv;
	}
	
	public RemoteViews getLoadingView()
	{
		// You can create a custom loading view (for instance when getViewAt() is slow.) If you
		// return null here, you will get the default loading view.
		return null;
	}
	
	public int getViewTypeCount()
	{
		return 1;
	}
	
	public long getItemId(int position)
	{
		return position;
	}
	
	public boolean hasStableIds()
	{
		return true;
	}
	
	public void onDataSetChanged()
	{
		// This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
		// on the collection view corresponding to this factory. You can do heaving lifting in
		// here, synchronously. For example, if you need to process an image, fetch something
		// from the network, etc., it is ok to do it here, synchronously. The widget will remain
		// in its current state while work is being done here, so you don't need to worry about
		// locking up the widget.
	}
}