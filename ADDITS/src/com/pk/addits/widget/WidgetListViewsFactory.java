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
import com.pk.addits.data.DatabaseHelper;
import com.pk.addits.models.Article;
import com.squareup.picasso.Picasso;

public class WidgetListViewsFactory implements RemoteViewsService.RemoteViewsFactory
{
	private DatabaseHelper db = null;
	private List<Article> articleList = new ArrayList<Article>();
	private Context cntxt = null;
	
	public WidgetListViewsFactory(Context context, Intent intent)
	{
		this.cntxt = context;
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
		return articleList.size();
	}
	
	@Override
	public RemoteViews getViewAt(int position)
	{
		RemoteViews row = new RemoteViews(cntxt.getPackageName(), R.layout.widget_list_item);
		
		Article currentArticle = articleList.get(position);
		row.setTextViewText(R.id.txtTitle, currentArticle.getTitle());
		row.setTextViewText(R.id.txtDescription, currentArticle.getDescription());
		
		Intent i = new Intent();
		Bundle extras = new Bundle();
		extras.putInt(WidgetListProvider.EXTRA_ID, currentArticle.getID());
		i.putExtras(extras);
		
		row.setOnClickFillInIntent(R.id.txtTitle, i);
		row.setOnClickFillInIntent(R.id.txtDescription, i);
		row.setOnClickFillInIntent(R.id.imgPreview, i);
		
		if (articleList.get(position).getImage().length() > 0)
		{
			try
			{
				String IMG = currentArticle.getImage();
				String imgURL = IMG.substring(0, IMG.lastIndexOf(".")) + "-150x150" + IMG.substring(IMG.lastIndexOf("."), IMG.length());
				
				Bitmap bm = Picasso.with(cntxt).load(imgURL).resize(150, 150).get();
				row.setImageViewBitmap(R.id.imgPreview, bm);
				Thread.sleep(500);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
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