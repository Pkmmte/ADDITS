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
import com.pk.addits.service.WidgetListService;

public class WidgetListProvider extends AppWidgetProvider
{
	@Override
	public void onEnabled(Context context)
	{
		super.onEnabled(context);
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		super.onReceive(context, intent);
		
		if (intent.getAction().equals(Data.REFRESH_ACTION))
		{
			/* Do update */
		}
		else if(intent.getAction().equals(Data.ARTICLE_ACTION))
		{
			int viewID = intent.getIntExtra(Data.EXTRA_ID, 1);
			Intent articleIntent = new Intent(context, ActivityMain.class);
			articleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			articleIntent.putExtra(Data.EXTRA_ID, viewID);
			context.startActivity(articleIntent);
		}
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		for (int i = 0; i < appWidgetIds.length; i++)
		{
			Intent svcIntent = new Intent(context, WidgetListService.class);
			
			svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
			
			RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_list);
			
			widget.setRemoteAdapter(R.id.articles, svcIntent);
			
			Intent clickIntent = new Intent(context, WidgetListProvider.class);
			clickIntent.setAction(Data.ARTICLE_ACTION);
			clickIntent.setData(Uri.parse(clickIntent.toUri(Intent.URI_INTENT_SCHEME)));
			PendingIntent clickPI = PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			widget.setPendingIntentTemplate(R.id.articles, clickPI);
			
			appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
		}
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}