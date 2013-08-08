package com.pk.addits.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pk.addits.R;
import com.pk.addits.model.SettingsItem;

public class SettingsAdapter extends BaseAdapter
{
	private Context context;
	
	private List<SettingsItem> listItem;
	
	public SettingsAdapter(Context context, List<SettingsItem> listItem)
	{
		this.context = context;
		this.listItem = listItem;
	}
	
	public int getCount()
	{
		return listItem.size();
	}
	
	public Object getItem(int position)
	{
		return listItem.get(position);
	}
	
	public long getItemId(int position)
	{
		return position;
	}
	
	public View getView(int position, View view, ViewGroup viewGroup)
	{
		SettingsItem entry = listItem.get(position);
		if (view == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.settings_item, null);
		}
		
		// Get Values
		String Name = entry.getName();
		String Description = entry.getDescription();
		String Type = entry.getType();
		String Value = entry.getValue();
		String ValueType = entry.getValueType();
		
		// Types
		RelativeLayout LayoutSetting = (RelativeLayout) view.findViewById(R.id.Setting);
		TextView LayoutText = (TextView) view.findViewById(R.id.Text);
		
		// Setting
		TextView textName = (TextView) view.findViewById(R.id.Name);
		TextView textDescription = (TextView) view.findViewById(R.id.Description);
		CheckBox checkValue = (CheckBox) view.findViewById(R.id.checkBox);
		TextView textValue = (TextView) view.findViewById(R.id.textValue);
		
		if (Type.equals("Setting"))
		{
			LayoutSetting.setVisibility(View.VISIBLE);
			LayoutText.setVisibility(View.GONE);
			
			textName.setText(Name);
			textDescription.setText(Description);
			if (ValueType.equals("Text"))
			{
				checkValue.setVisibility(View.GONE);
				textValue.setVisibility(View.VISIBLE);
				
				textValue.setText(Value);
			}
			else
			{
				checkValue.setVisibility(View.VISIBLE);
				textValue.setVisibility(View.GONE);
				
				checkValue.setChecked(Boolean.parseBoolean(Value));
			}
		}
		else if (Type.equals("Text"))
		{
			LayoutSetting.setVisibility(View.GONE);
			LayoutText.setVisibility(View.VISIBLE);
			
			LayoutText.setText(Value);
		}
		
		return view;
	}
}
