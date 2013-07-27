package com.pk.addits.widget;

import java.util.ArrayList;
import java.util.List;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.pk.addits.R;
import com.pk.addits.data.DatabaseHelper;
import com.pk.addits.models.Article;

public class WidgetListViewsFactory implements RemoteViewsService.RemoteViewsFactory
{
	public static DatabaseHelper db = null;
	private List<Article> articleList = new ArrayList<Article>();
	private static final String[] items = { "lorem", "ipsum", "dolor", "sit", "amet", "consectetuer", "adipiscing", "elit", "morbi", "vel", "ligula", "vitae", "arcu", "aliquet", "mollis", "etiam", "vel", "erat", "placerat", "ante", "porttitor", "sodales", "pellentesque", "augue", "purus" };
	private Context ctxt = null;
	private int appWidgetId;
	
	public WidgetListViewsFactory(Context context, Intent intent)
	{
		this.ctxt = context;
		appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		db = new DatabaseHelper(context);
	}
	
	@Override
	public void onCreate()
	{
		articleList = db.getAllArticles();
	}
	
	@Override
	public void onDestroy()
	{
		// no-op
	}
	
	@Override
	public int getCount()
	{
		return 5;
	}
	
	@Override
	public RemoteViews getViewAt(int position)
	{
		RemoteViews row = new RemoteViews(ctxt.getPackageName(), R.layout.widget_list_item);
		
		row.setTextViewText(R.id.txtTitle, "Hi");
		
		Intent i = new Intent();
		Bundle extras = new Bundle();
		
		extras.putString(WidgetListProvider.EXTRA_WORD, "Hi");
		i.putExtras(extras);
		row.setOnClickFillInIntent(R.id.txtTitle, i);
		
		return (row);
	}
	
	@Override
	public RemoteViews getLoadingView()
	{
		return (null);
	}
	
	@Override
	public int getViewTypeCount()
	{
		return (1);
	}
	
	@Override
	public long getItemId(int position)
	{
		return (position);
	}
	
	@Override
	public boolean hasStableIds()
	{
		return (true);
	}
	
	@Override
	public void onDataSetChanged()
	{
		// no-op
	}
}