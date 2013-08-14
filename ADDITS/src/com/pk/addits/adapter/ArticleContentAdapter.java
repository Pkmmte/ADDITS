package com.pk.addits.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pk.addits.R;
import com.pk.addits.data.Data;
import com.pk.addits.misc.CustomMovementMethod;
import com.pk.addits.model.ArticleContent;
import com.pk.addits.view.ZoomImageView;
import com.pk.addits.viewholder.ContentViewHolder;
import com.squareup.picasso.Picasso;

public class ArticleContentAdapter extends BaseAdapter
{
	private Context context;
	private List<ArticleContent> listItem;
	private Typeface fontRegular;
	
	public ArticleContentAdapter(Context context, List<ArticleContent> listItem)
	{
		this.context = context;
		this.listItem = listItem;
		fontRegular = Typeface.createFromAsset(context.getAssets(), "RobotoSlab-Regular.ttf");
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
		final ContentViewHolder holder;
		ArticleContent entry = listItem.get(position);
		if (view == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.fragment_article_content, null);
			
			holder = new ContentViewHolder();
			holder.Text = (TextView) view.findViewById(R.id.Text);
			holder.Text.setTypeface(fontRegular);
			holder.Text.setMovementMethod(new CustomMovementMethod());
			holder.Image = (ZoomImageView) view.findViewById(R.id.Image);
			holder.Video = (FrameLayout) view.findViewById(R.id.Video);
			holder.VideoPreview = (ImageView) view.findViewById(R.id.VideoPreview);
			holder.App = (RelativeLayout) view.findViewById(R.id.App);
			
			view.setTag(holder);
		}
		else
			holder = (ContentViewHolder) view.getTag();
		
		final int Type = entry.getType();
		final String Content = entry.getContent();
		
		if (Type == Data.CONTENT_TYPE_TEXT)
		{
			holder.Text.setVisibility(View.VISIBLE);
			holder.Image.setVisibility(View.GONE);
			holder.Video.setVisibility(View.GONE);
			holder.App.setVisibility(View.GONE);
			
			holder.Text.setText(Html.fromHtml(Content));
		}
		else if (Type == Data.CONTENT_TYPE_IMAGE)
		{
			holder.Text.setVisibility(View.GONE);
			holder.Image.setVisibility(View.VISIBLE);
			holder.Video.setVisibility(View.GONE);
			holder.App.setVisibility(View.GONE);
			
			Picasso.with(context).load(Content).error(R.drawable.loading_image_error).skipCache().into(holder.Image);
			holder.Image.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					Data.zoomImageFromThumb(holder.Image, Content, context);
				}
			});
		}
		else if (Type == Data.CONTENT_TYPE_VIDEO)
		{
			holder.Text.setVisibility(View.GONE);
			holder.Image.setVisibility(View.GONE);
			holder.Video.setVisibility(View.VISIBLE);
			holder.App.setVisibility(View.GONE);
			
			String VideoPreviewURL = "http://img.youtube.com/vi/" + Content + "/hqdefault.jpg";
			Picasso.with(context).load(VideoPreviewURL).error(R.drawable.loading_image_error).skipCache().fit().into(holder.VideoPreview);
			holder.Video.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + Content)));
				}
			});
		}
		else if (Type == Data.CONTENT_TYPE_APP)
		{
			holder.Text.setVisibility(View.GONE);
			holder.Image.setVisibility(View.GONE);
			holder.Video.setVisibility(View.GONE);
			holder.App.setVisibility(View.VISIBLE);
			
			// TODO Add Application Support
		}
		else
		{
			holder.Text.setVisibility(View.GONE);
			holder.Image.setVisibility(View.GONE);
			holder.Video.setVisibility(View.GONE);
			holder.App.setVisibility(View.GONE);
		}
		
		return view;
	}
}