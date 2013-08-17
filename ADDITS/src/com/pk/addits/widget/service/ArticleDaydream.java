package com.pk.addits.widget.service;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.service.dreams.DreamService;

import com.pk.addits.R;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class ArticleDaydream extends DreamService
{
	private Context context;
	
	@Override
	public void onDreamingStarted()
	{
		// daydream started
		context = getApplicationContext();
	}
	
	@Override
	public void onDreamingStopped()
	{
		// daydream stopped
	}
	
	@Override
	public void onAttachedToWindow()
	{
		
		super.onAttachedToWindow();
		setInteractive(true);
		setFullscreen(true);
		setContentView(R.layout.daydream_article);
		
	}
}
