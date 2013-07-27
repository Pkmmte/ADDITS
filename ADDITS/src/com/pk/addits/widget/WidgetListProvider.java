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

public class WidgetListProvider extends AppWidgetProvider
{
	public static String EXTRA_WORD = "com.commonsware.android.appwidget.lorem.WORD";
	public static int CHOSEN_PROFILE;
	
	@Override
	public void onEnabled(Context context)
	{
		// super.onEnabled(context);
		
		Toast.makeText(context, "OnEnabled!", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		super.onReceive(context, intent);
		String strAction = intent.getAction();
		if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(strAction))
		{
			/* Do update */
		}
	}
	
	@Override
	public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		for (int i = 0; i < appWidgetIds.length; i++)
		{
			Intent svcIntent = new Intent(ctxt, WidgetListService.class);
			
			svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
			
			RemoteViews widget = new RemoteViews(ctxt.getPackageName(), R.layout.widget_list);
			
			widget.setRemoteAdapter(R.id.articles, svcIntent);
			
			Intent clickIntent = new Intent(ctxt, ActivityMain.class);
			PendingIntent clickPI = PendingIntent.getActivity(ctxt, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			widget.setPendingIntentTemplate(R.id.articles, clickPI);
			
			appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
		}
		
		super.onUpdate(ctxt, appWidgetManager, appWidgetIds);
	}
}