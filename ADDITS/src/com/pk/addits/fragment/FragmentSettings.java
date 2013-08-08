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
import android.widget.ListView;

import com.pk.addits.R;
import com.pk.addits.adapter.SettingsAdapter;
import com.pk.addits.data.Data;
import com.pk.addits.model.SettingsItem;

public class FragmentSettings extends Fragment
{
	private SharedPreferences prefs;
	private ListView list;
	private List<SettingsItem> settingList;
	private SettingsAdapter adapter;
	
	private boolean parseContent;
	private String updateInterval;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_settings, container, false);
		
		list = (ListView) view.findViewById(R.id.ListView);
		
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
		settingList.add(new SettingsItem("Parse Content (Experimental)", "Parse article content to show dynamic content", "Setting", "" + parseContent, "CheckBox"));
		settingList.add(new SettingsItem("Update Interval", "How often to check for new articles", "Setting", updateInterval, "Text"));
		settingList.add(new SettingsItem("Text", "Clear App Data"));
		
		adapter = new SettingsAdapter(getActivity(), settingList);
		list.setAdapter(adapter);
		
		list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long index)
			{
				String ID = settingList.get(position).getName();
				String IDType = settingList.get(position).getType();
				String IDValue = settingList.get(position).getValue();
				
				
				if (IDType.equals("Setting"))
				{
					if (ID.equals("Update Interval"))
					{
						//callDialog(ID, position);
					}
					else if (ID.equals("Parse Content (Experimental)"))
					{
						parseContent = !parseContent;
						
						settingList.remove(position);
						settingList.add(position, new SettingsItem("Parse Content (Experimental)", "Parse article content to show dynamic content", "Setting", "" + parseContent, "CheckBox"));
						adapter.notifyDataSetChanged();
						
						Editor editor = prefs.edit();
						editor.putBoolean(Data.PREF_TAG_PARSE_ARTICLE_CONTENT, parseContent);
						editor.commit();
					}
				}
				else if (IDType.equals("Text"))
				{
					//if (IDValue.equals("About"))
					//	Data.getAboutDialog(getActivity()).show();
					//else if (IDValue.equals("Changelog"))
					//	Data.getChangeDialog(getActivity()).show();
					//if (IDValue.equals("Clear App Data"))
					//	Data.getClearDataDialog(getActivity()).show();
					//else if(IDValue.equals("Check for updates"))
					//{
							//ActivityMain.checkUpdate(getActivity());
					//}
				}
			}
		});
	}
}
