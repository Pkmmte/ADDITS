package com.pk.addits.fragment;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.pk.addits.R;
import com.pk.addits.activity.ActivityMain;
import com.pk.addits.data.Data;

public class FragmentSupport extends Fragment
{
	private SharedPreferences prefs;
	
	private ImageView imgAndroid;
	private Button btnDone;
	private RadioGroup radioGroup;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_support, container, false);
		
		imgAndroid = (ImageView) view.findViewById(R.id.imgAndroid);
		btnDone = (Button) view.findViewById(R.id.btnDone);
		radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
		
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		prefs = getActivity().getSharedPreferences(Data.PREFS_TAG, 0);
		
		btnDone.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int selectedID = radioGroup.getCheckedRadioButtonId();
				boolean enabled = true;
				if (selectedID == R.id.radio_disable)
					enabled = false;
				
				Editor editor = prefs.edit();
				editor.putBoolean(Data.PREF_TAG_ADS_ENABLED, enabled);
				editor.commit();
				
				ActivityMain.showCurrentFragment();
			}
		});
	}
}
