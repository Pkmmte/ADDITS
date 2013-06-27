package com.pk.addits;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.ByteArrayBuffer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;
import android.view.Display;

import com.pk.addits.FragmentHome.FeedItem;
import com.pk.addits.FragmentHome.SlideItem;

public class Data
{
	public static final String PREFS_TAG = "AndroidDissectedPreferences";
	public static final String PACKAGE_TAG = "com.pk.addits";
	public static final String FEED_TAG = "feed.xml";
	public static final String FEED_URL = "http://addits.androiddissected.com/feed/";
	
	public static int getHeightByPercent(Context context, double percent)
	{
		Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int height = (int) (size.y * percent);
		return height;
	}
	
	public static SlideItem[] generateDummySlides()
	{
		SlideItem[] Slides = new SlideItem[5];
		Slides[0] = new SlideItem("Dummy Title", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		Slides[1] = new SlideItem("Dummy Title 2 ", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		Slides[2] = new SlideItem("Dummy Title 3 ", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		Slides[3] = new SlideItem("Dummy Title4 ", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		Slides[4] = new SlideItem("Dummy Title5 ", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		
		return Slides;
	}
	
	public static FeedItem[] generateDummyFeed()
	{
		FeedItem[] Feeeeedz = new FeedItem[7];
		Feeeeedz[0] = new FeedItem("Dumb Title", "Blah blah blah blah blah blajsaasdsdasdasdas", "Content", "FEED", "Cliff Wade", "June 22, 2013", "APP REVIEWS", "http://addits.androiddissected.com/wp-content/uploads/2013/06/Filmgrain.png", "http://addits.androiddissected.com/2013/06/22/meet-my-new-movie-companion-filmgrain/", 0, false);
		Feeeeedz[1] = new FeedItem("Dumber Title", "Lorem ipsum stuff", "Content", "FEED", "Cliff Wade", "June 22, 2013", "GAME REVIEWS", "http://addits.androiddissected.com/wp-content/uploads/2013/06/ManOfSteelHeader.jpg", "http://addits.androiddissected.com/2013/06/22/man-of-steel-i-loved-the-movie-can-the-android-game-match-it/", 0, false);
		Feeeeedz[2] = new FeedItem("Retard Title", "sdfsdfiounwsdei3wne iwnr f dsfdsdasdasdas", "Content", "FEED", "Cliff Wade", "June 22, 2013", "APP REVIEWS", "http://addits.androiddissected.com/wp-content/uploads/2013/06/Filmgrain.png", "http://addits.androiddissected.com/2013/06/22/meet-my-new-movie-companion-filmgrain/", 0, false);
		Feeeeedz[3] = new FeedItem("Dummy Title", "Blah blah blah blah blah", "Content", "FEED", "Cliff Wade", "June 22, 2013", "APP REVIEWS", "http://addits.androiddissected.com/wp-content/uploads/2013/06/Filmgrain.png", "http://addits.androiddissected.com/2013/06/22/meet-my-new-movie-companion-filmgrain/", 0, false);
		Feeeeedz[4] = new FeedItem("Smart Title", "Insert something smart here", "Content", "FEED", "Roberto Mezquia Jr", "June 22, 2013", "APP REVIEWS", "http://addits.androiddissected.com/wp-content/uploads/2013/06/Filmgrain.png", "http://addits.androiddissected.com/2013/06/22/meet-my-new-movie-companion-filmgrain/", 0, false);
		Feeeeedz[5] = new FeedItem("titllle", "A preview of your article will appear here", "Content", "FEED", "Cliff Wade", "June 22, 2013", "APP REVIEWS", "http://addits.androiddissected.com/wp-content/uploads/2013/06/Filmgrain.png", "http://addits.androiddissected.com/2013/06/22/meet-my-new-movie-companion-filmgrain/", 0, false);
		Feeeeedz[6] = new FeedItem("Title", "Description", "Content", "FEED", "Cliff Wade", "June 22, 2013", "APP REVIEWS", "http://addits.androiddissected.com/wp-content/uploads/2013/06/Filmgrain.png", "http://addits.androiddissected.com/2013/06/22/meet-my-new-movie-companion-filmgrain/", 0, false);
		
		return Feeeeedz;
	}
	
	public static void downloadFeed()
	{
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/Android/data/" + PACKAGE_TAG);
		dir.mkdirs();
		File file = new File(dir, FEED_TAG);
		
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
	
	public static FeedItem[] retrieveFeed()
	{
		int count = 0;
		
		try
		{
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + "/Android/data/" + PACKAGE_TAG + "/" + FEED_TAG);
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
		
		FeedItem[] Feeeeedz = new FeedItem[count];
		
		try
		{
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + "/Android/data/" + PACKAGE_TAG + "/" + FEED_TAG);
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
			
			// Flags
			boolean itemActive = false;
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
					else if (itemActive && elemName.equals("category"))
					{
						if (xrp.next() == XmlPullParser.TEXT)
							Category = xrp.getText();
					}
					else if (itemActive && elemName.equals("pubDate"))
					{
						if (xrp.next() == XmlPullParser.TEXT)
							Date = xrp.getText();
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
				}
				else if (eventType == XmlPullParser.END_TAG && xrp.getName().equals("item"))
				{
					itemActive = false;
					
					Feeeeedz[feedCount] = new FeedItem(Title, Description, Content, CommentFeed, Author, Date, Category, Image, URL, Comments, false);
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
			link = link.replace("[", "").replace("]", "");
		}
		return link;
	}
}
