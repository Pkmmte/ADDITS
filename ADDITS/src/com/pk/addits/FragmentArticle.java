package com.pk.addits;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.manuelpeinado.fadingactionbar.FadingActionBarHelper;
import com.squareup.picasso.Picasso;

public class FragmentArticle extends Fragment
{
	ActionBar actionBar;
	View view;
	static FadingActionBarHelper mFadingHelper;
	Feed Article;
	
	ImageView imgHeader;
	TextView txtTitle;
	TextView txtAuthor;
	TextView txtDate;
	TextView txtContent;
	
	public static FragmentArticle newInstance(Feed article)
	{
		FragmentArticle f = new FragmentArticle();
		Bundle bundle = new Bundle();
		
		bundle.putString("Title", article.getTitle());
		bundle.putString("Description", article.getDescription());
		bundle.putString("Content", article.getContent());
		bundle.putString("Comment Feed", article.getCommentFeed());
		bundle.putString("Author", article.getAuthor());
		bundle.putString("Date", article.getDate());
		bundle.putString("Category", article.getCategory());
		bundle.putString("Image", article.getImage());
		bundle.putString("URL", article.getURL());
		bundle.putInt("Comments", article.getComments());
		bundle.putBoolean("Read", article.isRead());
		
		f.setArguments(bundle);
		
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = mFadingHelper.createView(inflater);
		
		imgHeader = (ImageView) view.findViewById(R.id.image_header);
		txtTitle = (TextView) view.findViewById(R.id.txtTitle);
		txtAuthor = (TextView) view.findViewById(R.id.txtAuthor);
		txtDate = (TextView) view.findViewById(R.id.txtDate);
		txtContent = (TextView) view.findViewById(R.id.txtContent);
		
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		actionBar = getActivity().getActionBar();
		retrieveArguments();
		
		actionBar.setTitle(Article.getTitle());
		Picasso.with(getActivity()).load(Article.getImage()).fit().into(imgHeader);
		txtTitle.setText(Article.getTitle());
		txtAuthor.setText(Article.getAuthor());
		txtDate.setText(Article.getDate());
		txtContent.setText(Article.getContent());
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		mFadingHelper = new FadingActionBarHelper().actionBarBackground(R.drawable.ab_background).headerLayout(R.layout.header_light).contentLayout(R.layout.fragment_article).lightActionBar(false);
		mFadingHelper.initActionBar(activity);
	}
	
	public void retrieveArguments()
	{
		Bundle args = getArguments();
		
		String Title = args.getString("Title");
		String Description = args.getString("Description");
		String Content = args.getString("Content");
		String CommentFeed = args.getString("Comment Feed");
		String Author = args.getString("Author");
		String Date = args.getString("Date");
		String Category = args.getString("Category");
		String Image = args.getString("Image");
		String URL = args.getString("URL");
		int Comments = args.getInt("Comments");
		boolean Read = args.getBoolean("Read");
		
		Article = new Feed(Title, Description, Content, CommentFeed, Author, Date, Category, Image, URL, Comments, Read);
	}
}
