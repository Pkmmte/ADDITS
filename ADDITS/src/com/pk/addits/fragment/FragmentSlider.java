package com.pk.addits.fragment;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pk.addits.R;
import com.pk.addits.activity.ActivityMain;
import com.pk.addits.model.Article;
import com.squareup.picasso.Picasso;

public class FragmentSlider extends Fragment
{
	private int ID;
	private String Title;
	private String Author;
	private String Date;
	private String ImageURL;
	
	private FrameLayout Parent;
	private ImageView imgImage;
	private TextView txtTitle;
	private TextView txtAuthor;
	private TextView txtDate;
	private LinearLayout Content;
	
	public static final FragmentSlider newInstance(int ID, String Title, String Author, String Date, String ImageURL)
	{
		FragmentSlider f = new FragmentSlider();
		Bundle bdl = new Bundle(5);
		
		bdl.putInt("ID", ID);
		bdl.putString("Title", Title);
		bdl.putString("Author", Author);
		bdl.putString("Date", Date);
		bdl.putString("ImageURL", ImageURL);
		
		f.setArguments(bdl);
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_home_slider, container, false);
		
		Parent = (FrameLayout) view.findViewById(R.id.Parent);
		imgImage = (ImageView) view.findViewById(R.id.Image);
		txtTitle = (TextView) view.findViewById(R.id.txtTitle);
		txtAuthor = (TextView) view.findViewById(R.id.txtAuthor);
		txtDate = (TextView) view.findViewById(R.id.txtDate);
		Content = (LinearLayout) view.findViewById(R.id.Content);
		
		Typeface fontTitle = Typeface.createFromAsset(getActivity().getAssets(), "RobotoSlab-Bold.ttf");
		Typeface fontAuthor = Typeface.createFromAsset(getActivity().getAssets(), "RobotoSlab-Light.ttf");
		Typeface fontDate = Typeface.createFromAsset(getActivity().getAssets(), "RobotoSlab-Light.ttf");
		
		txtTitle.setTypeface(fontTitle);
		txtAuthor.setTypeface(fontAuthor);
		txtDate.setTypeface(fontDate);
		
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		retrieveData();
		
		txtTitle.setText(Title);
		txtAuthor.setText(Author);
		txtDate.setText(Date);
		
		if (Title.length() < 1 && Author.length() < 1 && Date.length() < 1)
			Content.setVisibility(View.GONE);
		else
			Content.setVisibility(View.VISIBLE);
		
		setImage();
		Parent.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Article article = ActivityMain.articleList.get(ID);
				ActivityMain.callArticle(article, 0, 0);
			}
		});
	}
	
	public void retrieveData()
	{
		Bundle args = getArguments();
		ID = args.getInt("ID");
		Title = args.getString("Title");
		Author = args.getString("Author");
		Date = args.getString("Date");
		ImageURL = args.getString("ImageURL");
	}
	
	public void setImage()
	{
		DownloadImage task = new DownloadImage();
		if (ImageURL.length() > 0)
			task.execute(ImageURL);
		else
			Picasso.with(getActivity()).load(R.drawable.loading_image_banner).fit().into(imgImage);
	}
	
	private class DownloadImage extends AsyncTask<String, Void, Bitmap>
	{
		@Override
		protected Bitmap doInBackground(String... param)
		{
			try
			{
				return Picasso.with(getActivity()).load(param[0]).skipCache().get();
			}
			catch (Exception e)
			{
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Bitmap result)
		{
			if (result != null)
				imgImage.setImageBitmap(result);
		}
	}
}
