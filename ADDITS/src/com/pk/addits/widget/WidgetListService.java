package com.pk.addits.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetListService extends RemoteViewsService
{
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent)
	{
		return (new WidgetListViewsFactory(this.getApplicationContext(), intent));
	}
}