package com.pk.addits.fragment;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.pk.addits.R;
import com.pk.addits.data.Data;

public class FragmentSettings extends Fragment
{
	SharedPreferences prefs;
	private CheckBox checkBox;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_settings, container, false);
		
		checkBox = (CheckBox) view.findViewById(R.id.checkBox);
		
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		prefs = getActivity().getSharedPreferences(Data.PREFS_TAG, 0);
		
		checkBox.setChecked(prefs.getBoolean(Data.PREF_TAG_PARSE_ARTICLE_CONTENT, false));
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				Editor editor = prefs.edit();
				editor.putBoolean(Data.PREF_TAG_PARSE_ARTICLE_CONTENT, isChecked);
				editor.commit();
			}
		});
	}
}
