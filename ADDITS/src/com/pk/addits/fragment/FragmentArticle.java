package com.pk.addits.fragment;

import java.util.List;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.pk.addits.R;
import com.pk.addits.URLImageParser;
import com.pk.addits.adapter.ArticleContentAdapter;
import com.pk.addits.adapter.CommentsAdapter;
import com.pk.addits.data.Data;
import com.pk.addits.model.Article;
import com.pk.addits.model.ArticleContent;
import com.pk.addits.model.CommentFeed;
import com.pk.addits.view.PkListView;
import com.squareup.picasso.Picasso;

public class FragmentArticle extends Fragment
{
	private SharedPreferences prefs;
	ActionBar actionBar;
	static ShareActionProvider mShareActionProvider;
	static Article Article;
	private Thread loadHeaderImageThread;
	private Thread markReadThread;
	private Thread loadContentThread;
	private Thread loadCommentsThread;
	private Handler mHandler;
	static MenuItem shareItem;
	static Menu optionsMenu;
	URLImageParser p;
	private Bitmap bm;
	
	//ImageView imgHeader;
	TextView txtTitle;
	TextView txtAuthor;
	TextView txtDate;
	TextView txtContent;
	PkListView lstContent;
	private List<ArticleContent> contentList;
	private ArticleContentAdapter contentAdapter;
	
	FrameLayout commentCard;
	TextView txtLoadComments;
	ProgressBar progressBar;
	ListView comments;
	private List<CommentFeed> commentList;
	private CommentsAdapter commentAdapter;
	
	private Typeface fontRegular;
	private Typeface fontBold;
	private Typeface fontLight;
	
	private boolean parseContent;
	
	public static FragmentArticle newInstance(Article article)
	{
		FragmentArticle f = new FragmentArticle();
		Bundle bundle = new Bundle();
		
		bundle.putInt("ID", article.getID());
		bundle.putString("Title", article.getTitle());
		bundle.putString("Description", article.getDescription());
		bundle.putString("Content", article.getContent());
		bundle.putString("Comment Feed", article.getCommentFeed());
		bundle.putString("Author", article.getAuthor());
		bundle.putString("Date", article.getDate());
		bundle.putString("Category", article.getCategory());
		bundle.putString("Image", article.getImage());
		bundle.putString("URL", article.getURL());
		bundle.putBoolean("Favorite", article.isFavorite());
		bundle.putBoolean("Read", article.isRead());
		
		f.setArguments(bundle);
		
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_article, container, false);
		setHasOptionsMenu(true);
		
		//imgHeader = (ImageView) view.findViewById(R.id.image_header);
		txtTitle = (TextView) view.findViewById(R.id.txtTitle);
		txtAuthor = (TextView) view.findViewById(R.id.txtAuthor);
		txtDate = (TextView) view.findViewById(R.id.txtDate);
		txtContent = (TextView) view.findViewById(R.id.txtContent);
		lstContent = (PkListView) view.findViewById(R.id.ArticleContent);
		// p = new URLImageParser(txtContent, getActivity());
		
		commentCard = (FrameLayout) view.findViewById(R.id.commentCard);
		txtLoadComments = (TextView) view.findViewById(R.id.txtLoadComments);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		comments = (ListView) view.findViewById(R.id.ListView);
		
		fontRegular = Typeface.createFromAsset(getActivity().getAssets(), "RobotoSlab-Regular.ttf");
		fontBold = Typeface.createFromAsset(getActivity().getAssets(), "RobotoSlab-Bold.ttf");
		fontLight = Typeface.createFromAsset(getActivity().getAssets(), "RobotoSlab-Light.ttf");
		
		txtTitle.setTypeface(fontBold);
		txtAuthor.setTypeface(fontLight);
		txtDate.setTypeface(fontLight);
		txtContent.setTypeface(fontRegular);
		
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		prefs = getActivity().getSharedPreferences(Data.PREFS_TAG, 0);
		parseContent = prefs.getBoolean(Data.PREF_TAG_PARSE_ARTICLE_CONTENT, false);
		actionBar = getActivity().getActionBar();
		retrieveArguments();
		mHandler = new Handler();
		
		actionBar.setTitle(Article.getTitle());
		//imgHeader.setAdjustViewBounds(false);
		//imgHeader.setScaleType(ScaleType.CENTER_CROP);
		//if (Article.getImage().length() > 0)
		//{
		//	initializeLoadHeaderImageThread();
		//	loadHeaderImageThread.start();
		//	
		//}
		//else
		//	Picasso.with(getActivity()).load(R.drawable.loading_image_error).fit().into(imgHeader);
		
		txtTitle.setText(Article.getTitle());
		txtAuthor.setText("Posted by " + Article.getAuthor());
		txtDate.setText(Data.parseRelativeDate(Article.getDate()));
		
		if (parseContent)
		{
			lstContent.setVisibility(View.VISIBLE);
			txtContent.setVisibility(View.GONE);
			initializeLoadContentThread();
			loadContentThread.start();
		}
		else
		{
			lstContent.setVisibility(View.GONE);
			txtContent.setVisibility(View.VISIBLE);

			txtContent.setText(Html.fromHtml(Article.getContent()));
			/** EXPERIMENTAL: Uncomment this for images **/
			// txtContent.setText(Html.fromHtml(Article.getContent(), p, null));
		}
		
		commentCard.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				progressBar.setVisibility(View.VISIBLE);
				txtLoadComments.setText("Loading Comments...");
				commentCard.setClickable(false);
				
				if (loadCommentsThread == null)
				{
					initializeLoadCommentsThread();
					loadCommentsThread.start();
				}
				else if (!loadCommentsThread.isAlive())
				{
					initializeLoadCommentsThread();
					loadCommentsThread.start();
				}
			}
		});
		
		if (!Article.isRead())
			markRead();
		
		configureShare();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		menu.clear();
		inflater.inflate(R.menu.article, menu);
		optionsMenu = menu;
		
		shareItem = menu.findItem(R.id.Share_Label);
		mShareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
	}
	
	public static void menuVisibility(boolean drawerOpen)
	{
		if (shareItem != null && optionsMenu != null)
			shareItem.setVisible(!drawerOpen);
		if (!drawerOpen && Article != null)
			configureShare();
	}
	
	public void retrieveArguments()
	{
		Bundle args = getArguments();
		
		int ID = args.getInt("ID");
		String Title = args.getString("Title");
		String Description = args.getString("Description");
		String Content = args.getString("Content");
		String CommentFeed = args.getString("Comment Feed");
		String Author = args.getString("Author");
		String Date = args.getString("Date");
		String Category = args.getString("Category");
		String Image = args.getString("Image");
		String URL = args.getString("URL");
		boolean Favorite = args.getBoolean("Favorite");
		boolean Read = args.getBoolean("Read");
		
		Article = new Article(ID, Title, Description, Content, CommentFeed, Author, Date, Category, Image, URL, Favorite, Read);
	}
	
	public static void configureShare()
	{
		String shareBody = Article.getTitle() + "\n\n" + Article.getURL();
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		if (mShareActionProvider != null)
			mShareActionProvider.setShareIntent(shareIntent);
	}
	
	private void markRead()
	{
		if (markReadThread == null)
		{
			initializeMarkReadThread();
			markReadThread.start();
		}
		else if (!markReadThread.isAlive())
		{
			initializeMarkReadThread();
			markReadThread.start();
		}
	}
	
	private void initializeMarkReadThread()
	{
		markReadThread = new Thread()
		{
			public void run()
			{
				// ActivityMain.NewsFeed[Article.getID()].setRead(true);
				// ActivityMain.overwriteFeedXML();
				
				stopThread(this);
			}
		};
	}
	
	private void initializeLoadHeaderImageThread()
	{
		loadHeaderImageThread = new Thread()
		{
			public void run()
			{
				try
				{
					Bitmap bp = Picasso.with(getActivity()).load(Article.getImage()).skipCache().get();
					bm = Bitmap.createScaledBitmap(bp, Data.getWidthByPercent(getActivity(), 1.0), Data.getHeightByPercent(getActivity(), 0.3), false);
					mHandler.post(loadImage);
				}
				catch (Exception e)
				{
					// mHandler.post(loadFail);
				}
				
				stopThread(this);
			}
		};
	}
	
	Runnable loadImage = new Runnable()
	{
		public void run()
		{
			//imgHeader.setImageBitmap(bm);
			//mFadingHelper.updateHeaderHeight(mFadingHelper.mHeaderView.getMeasuredHeight());
		}
	};
	
	Runnable loadFail = new Runnable()
	{
		public void run()
		{
			Toast.makeText(getActivity(), "FAIL...", Toast.LENGTH_SHORT).show();
		}
	};
	
	private void initializeLoadContentThread()
	{
		loadContentThread = new Thread()
		{
			public void run()
			{
				contentList = Data.generateArticleContent(Article.getContent());
				contentAdapter = new ArticleContentAdapter(getActivity(), contentList);
				mHandler.postDelayed(loadContent, 500);
				
				stopThread(this);
			}
		};
	}
	
	private void initializeLoadCommentsThread()
	{
		loadCommentsThread = new Thread()
		{
			public void run()
			{
				try
				{
					Data.downloadCommentFeed(Article.getCommentFeed());
					commentList = Data.retrieveCommentFeed(getActivity());
					mHandler.postDelayed(loadComments, 250);
				}
				catch (Exception e)
				{
					Log.v("Download Comments", "ERROR: " + e.getMessage());
				}
				
				stopThread(this);
			}
		};
	}
	
	private synchronized void stopThread(Thread theThread)
	{
		if (theThread != null)
		{
			theThread = null;
		}
	}
	
	Runnable loadComments = new Runnable()
	{
		public void run()
		{
			commentAdapter = new CommentsAdapter(getActivity(), commentList);
			comments.setAdapter(commentAdapter);
			commentAdapter.notifyDataSetChanged();
			
			txtLoadComments.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);
		}
	};
	
	Runnable loadContent = new Runnable()
	{
		public void run()
		{
			lstContent.setAdapter(contentAdapter);
			contentAdapter.notifyDataSetChanged();
			lstContent.setExpanded(true);
			lstContent.setDividerHeight(0);
			
		}
	};
}
