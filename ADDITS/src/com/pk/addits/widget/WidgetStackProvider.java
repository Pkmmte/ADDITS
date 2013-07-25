package com.pk.addits.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.pk.addits.ActivityMain;
import com.pk.addits.R;

public class WidgetStackProvider extends AppWidgetProvider
{
	public static final String ARTICLE_ACTION = "com.pk.addits.widget.ARTICLE_ACTION";
	public static final String EXTRA_ID = "com.pk.addits.widget.EXTRA_ID";
	
	public static final String TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION";
	public static final String EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM";
	
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
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		//AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		if (intent.getAction().equals(TOAST_ACTION))
		{
			//int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
			int viewID = intent.getIntExtra(EXTRA_ID, 0);
			Toast.makeText(context, "Touched view " + viewIndex + "\nID: " + viewID, Toast.LENGTH_SHORT).show();
		}
		if (intent.getAction().equals(ARTICLE_ACTION))
		{
			//int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
			Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_SHORT).show();

			int viewID = intent.getIntExtra(EXTRA_ID, 0);
			Intent articleIntent = new Intent(context, ActivityMain.class);
			articleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			articleIntent.putExtra(EXTRA_ID, viewID);
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
			
			// Here we setup the intent which points to the StackViewService which will
			// provide the views for this collection.
			Intent intent = new Intent(context, WidgetStackService.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			// When intents are compared, the extras are ignored, so we need to embed the extras
			// into the data so that the extras will not be ignored.
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
			rv.setRemoteAdapter(R.id.stack_view, intent);
			
			// The empty view is displayed when the collection has no items. It should be a sibling
			// of the collection view.
			rv.setEmptyView(R.id.stack_view, R.id.empty_view);
			
			// Here we setup the a pending intent template. Individuals items of a collection
			// cannot setup their own pending intents, instead, the collection as a whole can
			// setup a pending intent template, and the individual items can set a fillInIntent
			// to create unique before on an item to item basis.
			Intent toastIntent = new Intent(context, WidgetStackProvider.class);
			toastIntent.setAction(WidgetStackProvider.ARTICLE_ACTION);
			toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);
			
			appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}