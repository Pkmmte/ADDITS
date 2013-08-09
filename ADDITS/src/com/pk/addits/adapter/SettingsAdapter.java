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
import com.pk.addits.viewholder.SettingsViewHolder;

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
		SettingsViewHolder holder;
		
		if (view == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.settings_item, null);
			
			holder = new SettingsViewHolder();
			holder.txtTitle = (TextView) view.findViewById(R.id.txtTitle);
			holder.txtDescription = (TextView) view.findViewById(R.id.txtDescription);
			holder.txtValue = (TextView) view.findViewById(R.id.txtValue);
			holder.checkBox = (ImageView) view.findViewById(R.id.checkBox);
			view.setTag(holder);
		}
		else
			holder = (SettingsViewHolder) view.getTag();
		
		holder.txtTitle.setText(entry.getName());
		holder.txtDescription.setText(entry.getDescription());
		
		switch (entry.getType())
		{
			case 1: // TEXT
				holder.checkBox.setVisibility(View.GONE);
				holder.txtValue.setVisibility(View.VISIBLE);
				holder.txtValue.setText(entry.getValue());
				break;
			case 2: // CHECK BOX
				holder.checkBox.setVisibility(View.VISIBLE);
				holder.txtValue.setVisibility(View.GONE);
				if (Boolean.parseBoolean(entry.getValue()))
					holder.checkBox.setImageResource(R.drawable.checkbox_on);
				else
					holder.checkBox.setImageResource(R.drawable.checkbox_off);
				break;
			default: // OTHER
				holder.checkBox.setVisibility(View.GONE);
				holder.txtValue.setVisibility(View.GONE);
				break;
		}
		
		return view;
	}
}
