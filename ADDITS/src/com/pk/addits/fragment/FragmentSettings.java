package com.pk.addits.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ListView;
import android.widget.Toast;

import com.pk.addits.R;
import com.pk.addits.activity.ActivityMain;
import com.pk.addits.adapter.SettingsAdapter;
import com.pk.addits.adapter.SimpleListAdapter;
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
		settingList.add(new SettingsItem("Parse Content [Experimental]", "Parse article content to show dynamic content. May cause issues.", "" + parseContent, Data.SETTING_TYPE_CHECKBOX));
		settingList.add(new SettingsItem("Update Interval", "How often to check for new content.", updateInterval, Data.SETTING_TYPE_TEXT));
		if(updateInterval.equals("Manual"))
			settingList.add(new SettingsItem("Check New", "Check for new content.\nWill update automatically if found.", "", Data.SETTING_TYPE_OTHER));
		settingList.add(new SettingsItem("Clear App Data", "Deletes all data on this app.\nUse only if you're experiencing issues.", "", Data.SETTING_TYPE_OTHER));
		
		adapter = new SettingsAdapter(getActivity(), settingList);
		grid.setAdapter(adapter);
		
		grid.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long index)
			{
				String ID = settingList.get(position).getName();
				// int IDType = settingList.get(position).getType();
				// String IDValue = settingList.get(position).getValue();
				
				if (ID.equals("Update Interval"))
				{
					callUpdateIntervalDialog(position);
				}
				else if (ID.equals("Parse Content [Experimental]"))
				{
					parseContent = !parseContent;
					
					settingList.remove(position);
					settingList.add(position, new SettingsItem("Parse Content [Experimental]", "Parse article content to show dynamic content. May cause issues.", "" + parseContent, Data.SETTING_TYPE_CHECKBOX));
					adapter.notifyDataSetChanged();
					
					Editor editor = prefs.edit();
					editor.putBoolean(Data.PREF_TAG_PARSE_ARTICLE_CONTENT, parseContent);
					editor.commit();
				}
				else if (ID.equals("Clear App Data"))
				{
					AlertDialog.Builder confirmDialogBuilder = new AlertDialog.Builder(getActivity());
					confirmDialogBuilder.setTitle("Confirm");
					confirmDialogBuilder.setMessage("Are you sure you wish to clear application data?\nThis action cannot be undone!");
					confirmDialogBuilder.setCancelable(false);
					confirmDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							dialog.cancel();
						}
					});
					confirmDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int id)
						{
							Toast.makeText(getActivity(), "Cleared App Data!", Toast.LENGTH_LONG).show();
							
							getActivity().getSharedPreferences(Data.PREFS_TAG, 0).edit().clear().commit();
							ActivityMain.db.removeAll();
							
							// Restart the app...
							Intent restartIntent = new Intent(getActivity(), ActivityMain.class);
							restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
							getActivity().startActivity(restartIntent);
						}
					});
					
					AlertDialog confirmDialog = confirmDialogBuilder.create();
					confirmDialog.show();
				}
			}
		});
	}
	
	private void callUpdateIntervalDialog(final int Pos)
	{
		final Dialog dialog = new Dialog(getActivity());
		dialog.setTitle("Update Interval");
		dialog.setContentView(R.layout.dialog_list);
		dialog.setCancelable(true);
		
		ListView list = (ListView) dialog.findViewById(R.id.ListView);
		final List<String> listOfChoices = new ArrayList<String>();
		listOfChoices.add("Manual");
		listOfChoices.add("15 Minutes");
		listOfChoices.add("30 Minutes");
		listOfChoices.add("Hourly");
		listOfChoices.add("Daily");
		list.setAdapter(new SimpleListAdapter(getActivity(), listOfChoices));
		
		list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long index)
			{
				String ID = listOfChoices.get(position);
				
				boolean isManual = false;
				if (updateInterval.equals("Manual"))
					isManual = true;
				updateInterval = ID;
				Editor editor = prefs.edit();
				editor.putString(Data.PREF_TAG_UPDATE_INTERVAL, updateInterval);
				editor.commit();
				
				settingList.remove(Pos);
				settingList.add(Pos, new SettingsItem("Update Interval", "How often to check for new content.", updateInterval, Data.SETTING_TYPE_TEXT));
				adapter.notifyDataSetChanged();
				
				if (ID.equals("Manual") && !isManual)
				{
					settingList.add(Pos + 1, new SettingsItem("Check New", "Check for new content.\nWill update automatically if found.", "", Data.SETTING_TYPE_OTHER));
					adapter.notifyDataSetChanged();
				}
				else if (!ID.equals("Manual") && isManual)
				{
					settingList.remove(Pos + 1);
					adapter.notifyDataSetChanged();
				}
				
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
}
