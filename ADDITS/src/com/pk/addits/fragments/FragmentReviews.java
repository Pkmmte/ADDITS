package com.pk.addits.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pk.addits.R;

public class FragmentReviews extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_customization, container, false);
		
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
	}
}
