package com.pk.addits.service;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.pk.addits.widget.WidgetStackViewsFactory;

public class WidgetStackService extends RemoteViewsService
{
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent)
	{
		return new WidgetStackViewsFactory(this.getApplicationContext(), intent);
	}
}