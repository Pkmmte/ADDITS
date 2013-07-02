package com.pk.addits;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.ByteArrayBuffer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Display;

import com.pk.addits.FragmentArticle.CommentFeed;
import com.pk.addits.FragmentHome.SlideItem;

public class Data
{
	public static final String PREFS_TAG = "AndroidDissectedPreferences";
	public static final String PREF_TAG_LAST_UPDATE_CHECK_TIME = "Last Update Check Time";
	public static final String PACKAGE_TAG = "com.pk.addits";
	public static final String FEED_TAG = "feed.xml";
	public static final String FEED_URL = "http://addits.androiddissected.com/feed/";
	public static final String MAIN_URL = "http://addits.androiddissected.com/";
	public static final String TEMP_TAG = "temporary.xml";
	
	public static int getHeightByPercent(Context context, double percent)
	{
		Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int height = (int) (size.y * percent);
		return height;
	}
	
	public static SlideItem[] generateSlides(Feed[] Feeeeedz)
	{
		SlideItem[] Slides = new SlideItem[5];
		List<Integer> usedArticles = new ArrayList<Integer>();
		
		if (Feeeeedz == null)
		{
			for (int x = 0; x < Slides.length; x++)
				Slides[x] = new SlideItem("", "", "", MAIN_URL);
		}
		else
		{
			int count = 0;
			while (count < Slides.length)
			{
				Random generator = new Random();
				int r = generator.nextInt(Slides.length);
				if (!usedArticles.contains(r))
				{
					Slides[count] = new SlideItem(Feeeeedz[r].getTitle(), Feeeeedz[r].getDescription(), Feeeeedz[r].getImage(), Feeeeedz[r].getURL());
					usedArticles.add(r);
					count++;
				}
			}
		}
		
		return Slides;
	}
	
	public static void downloadFeed()
	{
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/Android/data/" + PACKAGE_TAG);
		dir.mkdirs();
		File file = new File(dir, TEMP_TAG);
		
		// Establish Connection
		try
		{
			URL updateURL = new URL(FEED_URL);
			URLConnection conn = updateURL.openConnection();
			InputStream is = conn.getInputStream();
			is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			
			int current = 0;
			while ((current = bis.read()) != -1)
			{
				baf.append((byte) current);
			}
			
			final String s = new String(baf.toByteArray());
			
			FileOutputStream f = new FileOutputStream(file);
			f.write(s.getBytes());
			f.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void writeFeed()
	{
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/Android/data/" + PACKAGE_TAG);
		dir.mkdirs();
		File tempFile = new File(dir, TEMP_TAG);
		
		dir.mkdirs();
		File file = new File(dir, FEED_TAG);
		tempFile.renameTo(file);
	}
	
	public static boolean compareFeed(Context context)
	{
		Feed[] tempFeed = retrieveFeed(context, false).clone();
		Feed[] realFeed = retrieveFeed(context, true).clone();
		
		if (tempFeed.length != realFeed.length)
			return true;
		
		for (int x = 0; x < tempFeed.length; x++)
		{
			if (!tempFeed[x].getAuthor().equalsIgnoreCase(realFeed[x].getAuthor()))
				return true;
			if (!tempFeed[x].getCategory().equalsIgnoreCase(realFeed[x].getCategory()))
				return true;
			if (!tempFeed[x].getCommentFeed().equalsIgnoreCase(realFeed[x].getCommentFeed()))
				return true;
			if (!tempFeed[x].getContent().equalsIgnoreCase(realFeed[x].getContent()))
				return true;
			if (!tempFeed[x].getDescription().equalsIgnoreCase(realFeed[x].getDescription()))
				return true;
			if (!tempFeed[x].getImage().equalsIgnoreCase(realFeed[x].getImage()))
				return true;
			if (!tempFeed[x].getTitle().equalsIgnoreCase(realFeed[x].getTitle()))
				return true;
			if (!tempFeed[x].getURL().equalsIgnoreCase(realFeed[x].getURL()))
				return true;
		}
		
		return false;
	}
	
	public static boolean compareFeedItem(Feed f1, Feed f2)
	{
		if (!f1.getAuthor().equalsIgnoreCase(f2.getAuthor()))
			return true;
		if (!f1.getCategory().equalsIgnoreCase(f2.getCategory()))
			return true;
		if (!f1.getCommentFeed().equalsIgnoreCase(f2.getCommentFeed()))
			return true;
		if (!f1.getContent().equalsIgnoreCase(f2.getContent()))
			return true;
		if (!f1.getDescription().equalsIgnoreCase(f2.getDescription()))
			return true;
		if (!f1.getImage().equalsIgnoreCase(f2.getImage()))
			return true;
		if (!f1.getTitle().equalsIgnoreCase(f2.getTitle()))
			return true;
		if (!f1.getURL().equalsIgnoreCase(f2.getURL()))
			return true;
		
		return false;
	}
	
	public static Feed[] retrieveFeed(Context context, boolean realFeed)
	{
		int count = 0;
		
		try
		{
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = null;
			if (realFeed)
				dir = new File(sdCard.getAbsolutePath() + "/Android/data/" + PACKAGE_TAG + "/" + FEED_TAG);
			else
				dir = new File(sdCard.getAbsolutePath() + "/Android/data/" + PACKAGE_TAG + "/" + TEMP_TAG);
			FileInputStream istr = new FileInputStream(dir);
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			
			XmlPullParser xrp = factory.newPullParser();
			xrp.setInput(istr, "UTF-8");
			xrp.next();
			int eventType = xrp.getEventType();
			
			while (eventType != XmlPullParser.END_DOCUMENT)
			{
				if (eventType == XmlPullParser.START_TAG)
				{
					String elemName = xrp.getName();
					if (elemName.equals("item"))
						count++;
				}
				eventType = xrp.next();
			}
		}
		catch (Exception e)
		{
			Log.w("[Feed Count] XML Parse Error", e);
		}
		
		Feed[] Feeeeedz = new Feed[count];
		
		try
		{
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = null;
			if (realFeed)
				dir = new File(sdCard.getAbsolutePath() + "/Android/data/" + PACKAGE_TAG + "/" + FEED_TAG);
			else
				dir = new File(sdCard.getAbsolutePath() + "/Android/data/" + PACKAGE_TAG + "/" + TEMP_TAG);
			FileInputStream istr = new FileInputStream(dir);
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			
			XmlPullParser xrp = factory.newPullParser();
			xrp.setInput(istr, "UTF-8");
			xrp.next();
			int eventType = xrp.getEventType();
			
			// Attributes
			String Title = "";
			String URL = "";
			String Date = "";
			String Author = "";
			String Category = "";
			String Image = "";
			String Description = "";
			String Content = "";
			String CommentFeed = "";
			int Comments = 0;
			boolean Favorite = false;
			boolean Read = false;
			
			// Flags
			boolean itemActive = false;
			boolean categoryFound = false;
			int feedCount = 0;
			
			while (eventType != XmlPullParser.END_DOCUMENT)
			{
				if (eventType == XmlPullParser.START_TAG)
				{
					String elemName = xrp.getName();
					if (!itemActive && elemName.equals("item"))
					{
						itemActive = true;
					}
					else if (itemActive && elemName.equals("title"))
					{
						if (xrp.next() == XmlPullParser.TEXT)
							Title = xrp.getText();
					}
					else if (itemActive && elemName.equals("link"))
					{
						if (xrp.next() == XmlPullParser.TEXT)
							URL = xrp.getText();
					}
					else if (itemActive && elemName.equals("category") && !categoryFound)
					{
						if (xrp.next() == XmlPullParser.TEXT)
						{
							Category = xrp.getText();
							categoryFound = true;
						}
					}
					else if (itemActive && elemName.equals("pubDate"))
					{
						if (xrp.next() == XmlPullParser.TEXT)
							Date = parseDate(context, xrp.getText());
					}
					else if (itemActive && elemName.equals("dc:creator"))
					{
						if (xrp.next() == XmlPullParser.TEXT)
							Author = xrp.getText();
					}
					else if (itemActive && elemName.equals("description"))
					{
						if (xrp.next() == XmlPullParser.TEXT)
						{
							String des = android.text.Html.fromHtml(xrp.getText()).toString();
							Description = des.substring(3, des.length());
							Image = pullLinks(xrp.getText());
						}
					}
					else if (itemActive && elemName.equals("content:encoded"))
					{
						if (xrp.next() == XmlPullParser.TEXT)
						{
							String con = android.text.Html.fromHtml(xrp.getText()).toString();
							Content = con.substring(3, con.length());
						}
					}
					else if (itemActive && elemName.equals("wfw:commentRss"))
					{
						if (xrp.next() == XmlPullParser.TEXT)
							CommentFeed = xrp.getText();
					}
					else if (itemActive && elemName.equals("slash:comments"))
					{
						if (xrp.next() == XmlPullParser.TEXT)
							Comments = Integer.parseInt(xrp.getText());
					}
					else if (itemActive && elemName.equals("favorite"))
					{
						if (xrp.next() == XmlPullParser.TEXT)
							Comments = Integer.parseInt(xrp.getText());
					}
					else if (itemActive && elemName.equals("read"))
					{
						if (xrp.next() == XmlPullParser.TEXT)
							Comments = Integer.parseInt(xrp.getText());
					}
				}
				else if (eventType == XmlPullParser.END_TAG && xrp.getName().equals("item"))
				{
					itemActive = false;
					categoryFound = false;
					
					Feeeeedz[feedCount] = new Feed(feedCount, Title, Description, Content, CommentFeed, Author, Date, Category, Image, URL, Comments, Favorite, Read);
					feedCount++;
				}
				eventType = xrp.next();
			}
			
		}
		catch (Exception e)
		{
			Log.w("[Feed] XML Parse Error", e);
		}
		
		// Return Combination
		return Feeeeedz;
	}
	
	public static void downloadCommentFeed(String feedURL)
	{
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/Android/data/" + PACKAGE_TAG);
		dir.mkdirs();
		File file = new File(dir, TEMP_TAG);
		
		// Establish Connection
		try
		{
			URL updateURL = new URL(feedURL);
			URLConnection conn = updateURL.openConnection();
			InputStream is = conn.getInputStream();
			is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			
			int current = 0;
			while ((current = bis.read()) != -1)
			{
				baf.append((byte) current);
			}
			
			final String s = new String(baf.toByteArray());
			
			FileOutputStream f = new FileOutputStream(file);
			f.write(s.getBytes());
			f.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static List<CommentFeed> retrieveCommentFeed(Context context)
	{
		List<CommentFeed> comments = new ArrayList<CommentFeed>();
		
		try
		{
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + "/Android/data/" + PACKAGE_TAG + "/" + TEMP_TAG);
			FileInputStream istr = new FileInputStream(dir);
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			
			XmlPullParser xrp = factory.newPullParser();
			xrp.setInput(istr, "UTF-8");
			xrp.next();
			int eventType = xrp.getEventType();
			
			// Attributes
			String Creator = "";
			String Content = "";
			String Date = "";
			
			// Flags
			boolean itemActive = false;
			
			while (eventType != XmlPullParser.END_DOCUMENT)
			{
				if (eventType == XmlPullParser.START_TAG)
				{
					String elemName = xrp.getName();
					if (!itemActive && elemName.equals("item"))
					{
						itemActive = true;
					}
					else if (itemActive && elemName.equals("dc:creator"))
					{
						if (xrp.next() == XmlPullParser.TEXT)
							Creator = xrp.getText();
					}
					else if (itemActive && elemName.equals("content:encoded"))
					{
						if (xrp.next() == XmlPullParser.TEXT)
							Content = android.text.Html.fromHtml(xrp.getText()).toString();
					}
					else if (itemActive && elemName.equals("pubDate"))
					{
						if (xrp.next() == XmlPullParser.TEXT)
							Date = parseDate(context, xrp.getText());
					}
				}
				else if (eventType == XmlPullParser.END_TAG && xrp.getName().equals("item"))
				{
					itemActive = false;
					
					comments.add(new CommentFeed(Creator, Content, Date));
				}
				eventType = xrp.next();
			}
			
		}
		catch (Exception e)
		{
			Log.w("[CommentFeed] XML Parse Error", e);
		}
		
		// Return Combination
		return comments;
	}
	
	private static String pullLinks(String text)
	{
		ArrayList<String> links = new ArrayList<String>();
		String link = "";
		try
		{
			String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(text);
			while (m.find())
			{
				String urlStr = m.group();
				if (urlStr.startsWith("(") && urlStr.endsWith(")"))
				{
					urlStr = urlStr.substring(1, urlStr.length() - 1);
				}
				String format = urlStr.substring(urlStr.length() - 4);
				if (format.equalsIgnoreCase(".png") || format.equalsIgnoreCase(".jpg") || format.equalsIgnoreCase(".jpeg") || format.equalsIgnoreCase(".gif"))
					links.add(urlStr);
			}
		}
		catch (Exception e)
		{
			Log.w("ImageFeed URL Parse Error", e);
		}
		
		if (links.size() > 0)
		{
			link = links.get(0).toString();
			link = link.replace("[", "").replace("]", "").replace("-150x150", "");
		}
		return link;
	}
	
	public static boolean isNetworkConnected(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null)
		{
			return false;
		}
		else
			return true;
	}
	
	public static void deleteTempFile()
	{
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/Android/data/" + PACKAGE_TAG);
		File file = new File(dir, TEMP_TAG);
		
		file.delete();
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String parseDate(Context context, String mDate)
	{
		SimpleDateFormat tFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
		Date date = new Date();
		try
		{
			date = tFormat.parse(mDate);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		
		final CharSequence ago = DateUtils.getRelativeDateTimeString(context, date.getTime(), System.currentTimeMillis(),DateUtils.SECOND_IN_MILLIS, 0);
		
		return ago.toString();
	}
}
