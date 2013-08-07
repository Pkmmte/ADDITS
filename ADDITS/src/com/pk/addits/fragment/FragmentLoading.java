package com.pk.addits.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pk.addits.R;

public class FragmentLoading extends Fragment
{
	ImageView loadingImage;
	AnimationDrawable loadingAnimation;
	static TextView loadingText;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_loading, container, false);

		loadingImage = (ImageView) view.findViewById(R.id.loadingImage);
		loadingAnimation = (AnimationDrawable) loadingImage.getBackground();
		loadingText = (TextView) view.findViewById(R.id.textLoading);
		
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();

		loadingAnimation.start();
	}
	
	public static void setLoadingText(String text)
	{
		loadingText.setText(text);
	}
}
