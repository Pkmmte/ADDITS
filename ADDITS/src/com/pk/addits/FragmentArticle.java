package com.pk.addits;

import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.pk.addits.fadingactionbar.FadingActionBarHelper;
import com.squareup.picasso.Picasso;

public class FragmentArticle extends Fragment
{
	ActionBar actionBar;
	static ShareActionProvider mShareActionProvider;
	View view;
	static FadingActionBarHelper mFadingHelper;
	static Feed Article;
	private Thread loadCommentsThread;
	private Handler mHandler;
	static MenuItem shareItem;
	static Menu optionsMenu;
	URLImageParser p;
	
	ImageView imgHeader;
	TextView txtTitle;
	TextView txtAuthor;
	TextView txtDate;
	// TextView txtContent;
	PkListView lstContent;
	private List<ArticleContent> contentList;
	private ContentAdapter contentAdapter;
	
	FrameLayout commentCard;
	TextView txtLoadComments;
	ProgressBar progressBar;
	ListView comments;
	private List<CommentFeed> commentList;
	private CommentFeedAdapter commentAdapter;
	
	Typeface fontRegular;
	Typeface fontBold;
	Typeface fontLight;
	
	private static Animator mCurrentAnimator;
	private static int mShortAnimationDuration;
	static ImageView expandedImageView;
	FragmentManager fragMan;
	
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
		bundle.putBoolean("Favorite", article.isFavorite());
		bundle.putBoolean("Read", article.isRead());
		
		f.setArguments(bundle);
		
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = mFadingHelper.createView(inflater);
		setHasOptionsMenu(true);
		
		imgHeader = (ImageView) view.findViewById(R.id.image_header);
		txtTitle = (TextView) view.findViewById(R.id.txtTitle);
		txtAuthor = (TextView) view.findViewById(R.id.txtAuthor);
		txtDate = (TextView) view.findViewById(R.id.txtDate);
		// txtContent = (TextView) view.findViewById(R.id.txtContent);
		lstContent = (PkListView) view.findViewById(R.id.ArticleContent);
		expandedImageView = (ImageView) view.findViewById(R.id.expanded_image);
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
		// txtContent.setTypeface(fontRegular);
		fragMan = getChildFragmentManager();
		
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		actionBar = getActivity().getActionBar();
		retrieveArguments();
		mHandler = new Handler();
		configureShare();
		
		actionBar.setTitle(Article.getTitle());
		if (Article.getImage().length() > 0)
			Picasso.with(getActivity()).load(Article.getImage()).placeholder(R.drawable.loading_image_banner).error(R.drawable.loading_image_error).skipCache().fit().into(imgHeader);
		else
			Picasso.with(getActivity()).load(R.drawable.loading_image_error).fit().into(imgHeader);
		imgHeader.setAdjustViewBounds(true);
		
		txtTitle.setText(Article.getTitle());
		txtAuthor.setText("Posted by " + Article.getAuthor());
		txtDate.setText(Data.parseRelativeDate(Article.getDate()));
		// txtContent.setText(Html.fromHtml(Article.getContent()));
		/** Uncomment this for images **/
		// txtContent.setText(Html.fromHtml(Article.getContent(), p, null));
		
		contentList = Data.generateArticleContent2(Article.getContent());
		contentAdapter = new ContentAdapter(getActivity(), contentList);
		lstContent.setAdapter(contentAdapter);
		contentAdapter.notifyDataSetChanged();
		lstContent.setExpanded(true);
		
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
		
		YouTubePlayerSupportFragment fragment = new YouTubePlayerSupportFragment();
		FragmentTransaction fragmentTransaction = fragMan.beginTransaction();
		fragmentTransaction.add(R.id.VideoPlayer, fragment);
		fragmentTransaction.commit();

		fragment.initialize(Data.API_KEY_YOUTUBE, new OnInitializedListener()
		{
			@Override
			public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored)
			{
				if (!wasRestored)
				{
					player.loadVideo("N-xHcvug3WI");
				}
			}
			
			@Override
			public void onInitializationFailure(Provider provider, YouTubeInitializationResult arg1)
			{
				// ...
			}
		});
		
		// ActivityMain.overwriteFeedXML();
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		mFadingHelper = new FadingActionBarHelper().actionBarBackground(R.drawable.ab_background).headerLayout(R.layout.header_light).contentLayout(R.layout.fragment_article).lightActionBar(false);
		mFadingHelper.initActionBar(activity);
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
		if (!drawerOpen)
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
		
		Article = new Feed(ID, Title, Description, Content, CommentFeed, Author, Date, Category, Image, URL, Favorite, Read);
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
					mHandler.post(loadComments);
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
	
	public class ContentAdapter extends BaseAdapter
	{
		private Context context;
		
		private List<ArticleContent> listItem;
		
		public ContentAdapter(Context context, List<ArticleContent> listItem)
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
			final ContentViewHolder holder;
			ArticleContent entry = listItem.get(position);
			if (view == null)
			{
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.fragment_article_content, null);
				
				holder = new ContentViewHolder();
				holder.Container = (LinearLayout) view.findViewById(R.id.Container);
				holder.Text = (TextView) view.findViewById(R.id.Text);
				holder.Text.setTypeface(fontRegular);
				holder.Text.setMovementMethod(LinkMovementMethod.getInstance());
				holder.Image = (ZoomImageView) view.findViewById(R.id.Image);
				//holder.Video = view.f
				holder.Video = (FrameLayout) view.findViewById(R.id.Video);
				holder.App = (RelativeLayout) view.findViewById(R.id.App);
				
				view.setTag(holder);
			}
			else
			{
				holder = (ContentViewHolder) view.getTag();
			}
			
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
						FragmentArticle.zoomImageFromThumb(holder.Image, Content, holder.Container, context);
					}
				});
			}
			else if (Type == Data.CONTENT_TYPE_VIDEO)
			{
				holder.Text.setVisibility(View.GONE);
				holder.Image.setVisibility(View.GONE);
				holder.Video.setVisibility(View.VISIBLE);
				holder.App.setVisibility(View.GONE);
				
				YouTubePlayerSupportFragment fragment = YouTubePlayerSupportFragment.newInstance();
				//LinearLayout fragContainer = new LinearLayout(context);
				//fragContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				//fragContainer.setId(R.id.video_fragment);
				//holder.Video.addView(fragContainer);
				FragmentTransaction fragmentTransaction = fragMan.beginTransaction();
				fragmentTransaction.add(R.id.Video, fragment);
				//fragmentTransaction.add(R.id.Video, fragment);
				fragmentTransaction.commit();

				fragment.initialize(Data.API_KEY_YOUTUBE, new OnInitializedListener()
				{
					@Override
					public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored)
					{
						if (!wasRestored)
						{
							player.loadVideo("N-xHcvug3WI");
						}
					}
					
					@Override
					public void onInitializationFailure(Provider provider, YouTubeInitializationResult arg1)
					{
						// ...
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
	
	private static class ContentViewHolder
	{
		public LinearLayout Container;
		public TextView Text;
		public ZoomImageView Image;
		public FrameLayout Video;
		public RelativeLayout App;
	}
	
	public static class ArticleContent
	{
		int Type;
		String Content;
		
		public ArticleContent()
		{
			this.Type = 0;
			this.Content = "";
		}
		
		public ArticleContent(int Type, String Content)
		{
			this.Type = Type;
			this.Content = Content;
		}
		
		public int getType()
		{
			return Type;
		}
		
		public String getContent()
		{
			return Content;
		}
	}
	
	public static void zoomImageFromThumb(final View thumbView, String imgURL, View container, Context context)
	{
		// If there's an animation in progress, cancel it immediately and proceed with this one.
		if (mCurrentAnimator != null)
		{
			mCurrentAnimator.cancel();
		}
		
		// Load the high-resolution "zoomed-in" image.
		Picasso.with(context).load(imgURL).placeholder(R.drawable.loading_image_banner).error(R.drawable.loading_image_error).fit().into(expandedImageView);
		
		// Calculate the starting and ending bounds for the zoomed-in image. This step
		// involves lots of math. Yay, math.
		final Rect startBounds = new Rect();
		final Rect finalBounds = new Rect();
		final Point globalOffset = new Point();
		
		// The start bounds are the global visible rectangle of the thumbnail, and the
		// final bounds are the global visible rectangle of the container view. Also
		// set the container view's offset as the origin for the bounds, since that's
		// the origin for the positioning animation properties (X, Y).
		thumbView.getGlobalVisibleRect(startBounds);
		container.getGlobalVisibleRect(finalBounds, globalOffset);
		startBounds.offset(-globalOffset.x, -globalOffset.y);
		finalBounds.offset(-globalOffset.x, -globalOffset.y);
		
		// Adjust the start bounds to be the same aspect ratio as the final bounds using the
		// "center crop" technique. This prevents undesirable stretching during the animation.
		// Also calculate the start scaling factor (the end scaling factor is always 1.0).
		float startScale;
		if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width() / startBounds.height())
		{
			// Extend start bounds horizontally
			startScale = (float) startBounds.height() / finalBounds.height();
			float startWidth = startScale * finalBounds.width();
			float deltaWidth = (startWidth - startBounds.width()) / 2;
			startBounds.left -= deltaWidth;
			startBounds.right += deltaWidth;
		}
		else
		{
			// Extend start bounds vertically
			startScale = (float) startBounds.width() / finalBounds.width();
			float startHeight = startScale * finalBounds.height();
			float deltaHeight = (startHeight - startBounds.height()) / 2;
			startBounds.top -= deltaHeight;
			startBounds.bottom += deltaHeight;
		}
		
		// Hide the thumbnail and show the zoomed-in view. When the animation begins,
		// it will position the zoomed-in view in the place of the thumbnail.
		thumbView.setAlpha(0f);
		expandedImageView.setVisibility(View.VISIBLE);
		
		// Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
		// the zoomed-in view (the default is the center of the view).
		expandedImageView.setPivotX(0f);
		expandedImageView.setPivotY(0f);
		
		// Construct and run the parallel animation of the four translation and scale properties
		// (X, Y, SCALE_X, and SCALE_Y).
		AnimatorSet set = new AnimatorSet();
		set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left, finalBounds.left)).with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top, finalBounds.top)).with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
		set.setDuration(mShortAnimationDuration);
		set.setInterpolator(new DecelerateInterpolator());
		set.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator animation)
			{
				mCurrentAnimator = null;
			}
			
			@Override
			public void onAnimationCancel(Animator animation)
			{
				mCurrentAnimator = null;
			}
		});
		set.start();
		mCurrentAnimator = set;
		
		// Upon clicking the zoomed-in image, it should zoom back down to the original bounds
		// and show the thumbnail instead of the expanded image.
		final float startScaleFinal = startScale;
		expandedImageView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (mCurrentAnimator != null)
				{
					mCurrentAnimator.cancel();
				}
				
				// Animate the four positioning/sizing properties in parallel, back to their
				// original values.
				AnimatorSet set = new AnimatorSet();
				set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left)).with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top)).with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScaleFinal)).with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
				set.setDuration(mShortAnimationDuration);
				set.setInterpolator(new DecelerateInterpolator());
				set.addListener(new AnimatorListenerAdapter()
				{
					@Override
					public void onAnimationEnd(Animator animation)
					{
						thumbView.setAlpha(1f);
						expandedImageView.setVisibility(View.GONE);
						mCurrentAnimator = null;
					}
					
					@Override
					public void onAnimationCancel(Animator animation)
					{
						thumbView.setAlpha(1f);
						expandedImageView.setVisibility(View.GONE);
						mCurrentAnimator = null;
					}
				});
				set.start();
				mCurrentAnimator = set;
			}
		});
	}
	
	Runnable loadComments = new Runnable()
	{
		public void run()
		{
			commentAdapter = new CommentFeedAdapter(getActivity(), commentList);
			comments.setAdapter(commentAdapter);
			commentAdapter.notifyDataSetChanged();
			
			txtLoadComments.setVisibility(View.GONE);
			progressBar.setVisibility(View.GONE);
		}
	};
	
	public class CommentFeedAdapter extends BaseAdapter
	{
		private Context context;
		
		private List<CommentFeed> listItem;
		
		public CommentFeedAdapter(Context context, List<CommentFeed> listItem)
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
			CommentViewHolder holder;
			CommentFeed entry = listItem.get(position);
			if (view == null)
			{
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.commentfeed_item, null);
				
				holder = new CommentViewHolder();
				holder.txtCreator = (TextView) view.findViewById(R.id.txtCreator);
				holder.txtContent = (TextView) view.findViewById(R.id.txtContent);
				holder.txtDate = (TextView) view.findViewById(R.id.txtDate);
				
				holder.txtCreator.setTypeface(fontBold);
				holder.txtDate.setTypeface(fontLight);
				holder.txtContent.setTypeface(fontRegular);
				
				view.setTag(holder);
			}
			else
			{
				holder = (CommentViewHolder) view.getTag();
			}
			
			holder.txtCreator.setText(entry.getCreator());
			holder.txtContent.setText(entry.getContent());
			holder.txtDate.setText(entry.getDate());
			
			return view;
		}
	}
	
	private static class CommentViewHolder
	{
		public TextView txtCreator;
		public TextView txtContent;
		public TextView txtDate;
	}
	
	public static class CommentFeed
	{
		String Creator;
		String Content;
		String Date;
		
		public CommentFeed(String Creator, String Content, String Date)
		{
			this.Creator = Creator;
			this.Content = Content;
			this.Date = Date;
		}
		
		public String getCreator()
		{
			return Creator;
		}
		
		public String getContent()
		{
			return Content;
		}
		
		public String getDate()
		{
			return Date;
		}
	}
}
