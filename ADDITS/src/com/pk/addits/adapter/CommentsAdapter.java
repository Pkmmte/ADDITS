package com.pk.addits.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pk.addits.R;
import com.pk.addits.model.CommentFeed;
import com.pk.addits.model.CommentsViewHolder;

public class CommentsAdapter extends BaseAdapter
{
	private Context context;
	private List<CommentFeed> listItem;
	private Typeface fontRegular;
	private Typeface fontBold;
	private Typeface fontLight;
	
	public CommentsAdapter(Context context, List<CommentFeed> listItem)
	{
		this.context = context;
		this.listItem = listItem;
		this.fontRegular = Typeface.createFromAsset(context.getAssets(), "RobotoSlab-Regular.ttf");
		this.fontBold = Typeface.createFromAsset(context.getAssets(), "RobotoSlab-Bold.ttf");
		this.fontLight = Typeface.createFromAsset(context.getAssets(), "RobotoSlab-Light.ttf");
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
		CommentsViewHolder holder;
		CommentFeed entry = listItem.get(position);
		if (view == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.commentfeed_item, null);
			
			holder = new CommentsViewHolder();
			holder.txtCreator = (TextView) view.findViewById(R.id.txtCreator);
			holder.txtContent = (TextView) view.findViewById(R.id.txtContent);
			holder.txtDate = (TextView) view.findViewById(R.id.txtDate);
			
			holder.txtCreator.setTypeface(fontBold);
			holder.txtDate.setTypeface(fontLight);
			holder.txtContent.setTypeface(fontRegular);
			
			view.setTag(holder);
		}
		else
			holder = (CommentsViewHolder) view.getTag();
		
		holder.txtCreator.setText(entry.getCreator());
		holder.txtContent.setText(entry.getContent());
		holder.txtDate.setText(entry.getDate());
		
		return view;
	}
}