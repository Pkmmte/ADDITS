package com.pk.addits.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pk.addits.R;
import com.pk.addits.data.Data;
import com.pk.addits.model.Article;
import com.squareup.picasso.Picasso;

public class FeedAdapter extends BaseAdapter
{
	private Context context;
	private List<Article> listItem;
	private Typeface fontRegular;
	private Typeface fontBold;
	private Typeface fontLight;
	
	public FeedAdapter(Context context, List<Article> listItem)
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
	
	public Article getItem(int position)
	{
		return listItem.get(position);
	}
	
	public long getItemId(int position)
	{
		return position;
	}
	
	public View getView(int position, View view, ViewGroup viewGroup)
	{
		final ViewHolder holder;
		Article entry = listItem.get(position);
		if (view == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.feed_item, null);
			
			holder = new ViewHolder();
			holder.Card = (LinearLayout) view.findViewById(R.id.Card);
			holder.lblUnread = view.findViewById(R.id.lblUnread);
			holder.drkRead = (FrameLayout) view.findViewById(R.id.drkRead);
			holder.txtTitle = (TextView) view.findViewById(R.id.txtTitle);
			holder.txtDescription = (TextView) view.findViewById(R.id.txtDescription);
			holder.txtAuthor = (TextView) view.findViewById(R.id.txtAuthor);
			holder.txtDate = (TextView) view.findViewById(R.id.txtDate);
			holder.txtCategory = (TextView) view.findViewById(R.id.txtCategory);
			holder.imgPreview = (ImageView) view.findViewById(R.id.imgPreview);
			
			holder.txtTitle.setTypeface(fontBold);
			holder.txtDescription.setTypeface(fontRegular);
			holder.txtAuthor.setTypeface(fontLight);
			holder.txtDate.setTypeface(fontLight);
			holder.txtCategory.setTypeface(fontRegular);
			
			view.setTag(holder);
		}
		else
			holder = (ViewHolder) view.getTag();
		
		holder.txtTitle.setText(entry.getTitle());
		holder.txtDescription.setText(entry.getDescription());
		holder.txtAuthor.setText("Posted by " + entry.getAuthor());
		holder.txtDate.setText(Data.parseDate(context, entry.getDate()));
		holder.txtCategory.setText(entry.getCategory());
		
		try
		{
			if (entry.getImage().length() > 0)
				Picasso.with(context).load(entry.getImage()).placeholder(R.drawable.loading_image_banner).error(R.drawable.loading_image_error).fit().skipMemoryCache().into(holder.imgPreview);
			else
				holder.imgPreview.setVisibility(View.GONE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		if (entry.isRead())
		{
			holder.lblUnread.setVisibility(View.INVISIBLE);
			if (entry.getImage().length() > 0)
				holder.drkRead.setForeground(new ColorDrawable(context.getResources().getColor(R.color.black_trans)));
		}
		else
		{
			holder.lblUnread.setVisibility(View.VISIBLE);
			holder.drkRead.setForeground(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
		}
		
		Animation cardAnimation = AnimationUtils.loadAnimation(context, R.anim.card_anim_list);
		holder.Card.startAnimation(cardAnimation);
		
		return view;
	}
	
	public class ViewHolder
	{
		public LinearLayout Card;
		public View lblUnread;
		public FrameLayout drkRead;
		public TextView txtTitle;
		public TextView txtDescription;
		public TextView txtAuthor;
		public TextView txtDate;
		public TextView txtCategory;
		public ImageView imgPreview;
	}
	
}