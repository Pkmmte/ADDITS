package com.pk.addits;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manuelpeinado.fadingactionbar.FadingActionBarHelper;

public class FragmentArticle extends Fragment
{
	View view;
	static FadingActionBarHelper mFadingHelper;
	
	public static FragmentArticle newInstance(Feed article)
	{
		FragmentArticle f = new FragmentArticle();
		Bundle bundle = new Bundle();
		
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = mFadingHelper.createView(inflater);
		
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		mFadingHelper = new FadingActionBarHelper().actionBarBackground(R.drawable.ab_background).headerLayout(R.layout.header_light).contentLayout(R.layout.activity_scrollview).lightActionBar(true);
		mFadingHelper.initActionBar(activity);
	}
}
