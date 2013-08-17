package com.pk.addits.service;

import com.pk.addits.widget.WidgetListViewsFactory;

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