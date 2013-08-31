package com.pk.addits.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import com.pk.addits.R;

public class FragmentDeveloperFocus extends Fragment
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
		
		Tracker easyTracker = EasyTracker.getInstance(getActivity());

		// This screen name value will remain set on the tracker and sent with
		// hits until it is set to a new value or to null.
		easyTracker.set(Fields.SCREEN_NAME, "Developer Focus Screen");

		easyTracker.send(MapBuilder
		    .createAppView()
		    .build()
		);

	}
}
