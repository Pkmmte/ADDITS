package com.pk.addits;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manuelpeinado.fadingactionbar.FadingActionBarHelper;

public class FragmentHome extends Fragment
{
	private FadingActionBarHelper mFadingHelper;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = mFadingHelper.createView(inflater);
		
		//if (mArguments != null)
		//{
		//	ImageView img = (ImageView) view.findViewById(R.id.image_header);
		//	img.setImageResource(mArguments.getInt(ARG_IMAGE_RES));
		//}
		
		return view;
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		mFadingHelper = new FadingActionBarHelper()
						.actionBarBackground(R.drawable.ab_background)
						.headerLayout(R.layout.header_light)
						.contentLayout(R.layout.activity_scrollview)
						.lightActionBar(R.drawable.ab_background == R.drawable.ab_background_light);
		mFadingHelper.initActionBar(activity);
	}
}
