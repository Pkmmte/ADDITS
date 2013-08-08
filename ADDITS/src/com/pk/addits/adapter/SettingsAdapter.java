package com.pk.addits.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
		
		TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
		TextView txtDescription = (TextView) view.findViewById(R.id.txtDescription);
		ImageView checkBox = (ImageView) view.findViewById(R.id.checkBox);
		TextView txtValue = (TextView) view.findViewById(R.id.txtValue);
		
		txtTitle.setText(entry.getName());
		txtDescription.setText(entry.getDescription());
		
		if (entry.getType().equals("CheckBox"))
		{
			checkBox.setVisibility(View.VISIBLE);
			txtValue.setVisibility(View.GONE);
			
			boolean selected = Boolean.parseBoolean(entry.getValue());
			if (selected)
				checkBox.setImageResource(R.drawable.checkbox_on);
			else
				checkBox.setImageResource(R.drawable.checkbox_off);
		}
		else if (entry.getType().equals("Text"))
		{
			checkBox.setVisibility(View.GONE);
			txtValue.setVisibility(View.VISIBLE);
			
			txtValue.setText(entry.getValue());
		}
		else
		{
			checkBox.setVisibility(View.GONE);
			txtValue.setVisibility(View.GONE);
		}
		
		return view;
	}
}
