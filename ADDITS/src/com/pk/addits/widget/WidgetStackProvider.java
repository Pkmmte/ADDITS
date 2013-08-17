package com.pk.addits.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.pk.addits.R;
import com.pk.addits.activity.ActivityMain;
import com.pk.addits.data.Data;
import com.pk.addits.data.DatabaseHelper;
import com.pk.addits.service.WidgetStackService;

public class WidgetStackProvider extends AppWidgetProvider
{
	public static DatabaseHelper db = null;
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds)
	{
		super.onDeleted(context, appWidgetIds);
	}
	
	@Override
	public void onDisabled(Context context)
	{
		super.onDisabled(context);
	}
	
	@Override
	public void onEnabled(Context context)
	{
		super.onEnabled(context);
		db = DatabaseHelper.getInstance(context);
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (intent.getAction().equals(Data.ARTICLE_ACTION))
		{
			int viewID = intent.getIntExtra(Data.EXTRA_ID, 0);
			Intent articleIntent = new Intent(context, ActivityMain.class);
			articleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			articleIntent.putExtra(Data.EXTRA_ID, viewID);
			context.startActivity(articleIntent);
		}
		super.onReceive(context, intent);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		// update each of the widgets with the remote adapter
		for (int i = 0; i < appWidgetIds.length; ++i)
		{
			RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_stack_empty);
			
			if (db.getArticleCount() > 0)
			{
				// Here we setup the intent which points to the StackViewService which will
				// provide the views for this collection.
				Intent intent = new Intent(context, WidgetStackService.class);
				intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
				// When intents are compared, the extras are ignored, so we need to embed the extras
				// into the data so that the extras will not be ignored.
				intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
				rv = new RemoteViews(context.getPackageName(), R.layout.widget_stack);
				rv.setRemoteAdapter(R.id.stack_view, intent);
				
				// The empty view is displayed when the collection has no items. It should be a sibling
				// of the collection view.
				rv.setEmptyView(R.id.stack_view, R.id.empty_view);
				
				// Here we setup the a pending intent template. Individuals items of a collection
				// cannot setup their own pending intents, instead, the collection as a whole can
				// setup a pending intent template, and the individual items can set a fillInIntent
				// to create unique before on an item to item basis.
				Intent toastIntent = new Intent(context, WidgetStackProvider.class);
				toastIntent.setAction(Data.ARTICLE_ACTION);
				toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
				intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
				PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);
			}
			else
			{
				Intent appIntent = new Intent(context, ActivityMain.class);
				appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent appPI = PendingIntent.getActivity(context, 0, appIntent, 0);
				rv.setOnClickPendingIntent(R.id.btnStart, appPI);
			}
			
			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}