package com.pk.addits.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.pk.addits.R;
import com.pk.addits.adapter.SettingsAdapter;
import com.pk.addits.data.Data;
import com.pk.addits.model.SettingsItem;

public class FragmentSettings extends Fragment
{
	private SharedPreferences prefs;
	private GridView grid;
	private List<SettingsItem> settingList;
	private SettingsAdapter adapter;
	
	private boolean parseContent;
	private String updateInterval;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_settings, container, false);
		
		grid = (GridView) view.findViewById(R.id.GridView);
		
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		prefs = getActivity().getSharedPreferences(Data.PREFS_TAG, 0);
		parseContent = prefs.getBoolean(Data.PREF_TAG_PARSE_ARTICLE_CONTENT, false);
		updateInterval = prefs.getString(Data.PREF_TAG_UPDATE_INTERVAL, "Hourly");
		
		settingList = new ArrayList<SettingsItem>();
		settingList.add(new SettingsItem("Parse Content (Experimental)", "Parse article content to show dynamic content", "CheckBox", "" + parseContent));
		settingList.add(new SettingsItem("Update Interval", "How often to check for new articles", "Text", updateInterval));
		settingList.add(new SettingsItem("Clear App Data", "Deletes all data on this app.\nUse only if you're experiencing issues.", "Other", ""));
		
		adapter = new SettingsAdapter(getActivity(), settingList);
		grid.setAdapter(adapter);
		
		grid.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long index)
			{
				String ID = settingList.get(position).getName();
				String IDType = settingList.get(position).getType();
				String IDValue = settingList.get(position).getValue();
				
				if (ID.equals("Update Interval"))
				{
					// callDialog(ID, position);
				}
				else if (ID.equals("Parse Content (Experimental)"))
				{
					parseContent = !parseContent;
					
					settingList.remove(position);
					settingList.add(position, new SettingsItem("Parse Content (Experimental)", "Parse article content to show dynamic content", "CheckBox", "" + parseContent));
					adapter.notifyDataSetChanged();
					
					Editor editor = prefs.edit();
					editor.putBoolean(Data.PREF_TAG_PARSE_ARTICLE_CONTENT, parseContent);
					editor.commit();
				}
				
			}
		});
	}
}
