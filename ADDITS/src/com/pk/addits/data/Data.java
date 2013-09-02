package com.pk.addits.data;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import com.pk.addits.R;
import com.pk.addits.activity.ActivityMain;
import com.pk.addits.model.ArticleContent;
import com.pk.addits.model.CommentFeed;
import com.squareup.picasso.Picasso;

public class Data
{
	public static final boolean BETA = true;
	
	public static final int BUILD_TAG = 2;
	
	public static final String API_KEY_YOUTUBE = "AIzaSyCkM13XkYpzqEyjRX11F8IoiHLmd1TrKoU";
	
	public static final String PREFS_TAG = "AndroidDissectedPreferences";
	public static final String PREF_TAG_LAST_UPDATE_CHECK_TIME = "Last Update Check Time";
	public static final String PREF_TAG_PARSE_ARTICLE_CONTENT = "Parse Article Content";
	public static final String PREF_TAG_UPDATE_INTERVAL = "Update Interval";
	public static final String PREF_TAG_SAVED_BUILD = "Saved Build Number";
	public static final String PREF_TAG_ADS_ENABLED = "Ads Enabled";
	
	public static final String PACKAGE_TAG = "com.pk.addits";
	public static final String FEED_TAG = "feed.xml";
	public static final String FEED_URL = "http://www.androiddissected.com/feed/";
	public static final String MAIN_URL = "http://www.androiddissected.com/";
	public static final String EMEGENCY_URL = "http://data.pkmmte.com/apps/addits/emergency_shutdown.txt";
	public static final String TEMP_TAG = "temporary.xml";
	public static final String EMERGENCY_TAG = "TheCakeIsALie";
	
	public static final Integer CONTENT_TYPE_TEXT = 1;
	public static final Integer CONTENT_TYPE_IMAGE = 2;
	public static final Integer CONTENT_TYPE_VIDEO = 3;
	public static final Integer CONTENT_TYPE_APP = 4;
	
	public static final Integer SETTING_TYPE_TEXT = 1;
	public static final Integer SETTING_TYPE_CHECKBOX = 2;
	public static final Integer SETTING_TYPE_OTHER = 3;
	
	public static final String EXTRA_ID = "com.pk.addits.widget.EXTRA_ID";
	public static final String ARTICLE_ACTION = "com.pk.addits.widget.ARTICLE_ACTION";
	public static final String REFRESH_ACTION = "com.pk.addits.widget.REFRESH_ACTION";
	
	public static int getWidthByPercent(Context context, double percent)
	{
		Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = (int) (size.x * percent);
		return width;
	}
	
	public static int getHeightByPercent(Context context, double percent)
	{
		Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int height = (int) (size.y * percent);
		return height;
	}
	
	public static JSONObject getJSON()
	{
		DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
		HttpPost httppost = new HttpPost(Data.FEED_URL);
		// Depends on your web service
		httppost.setHeader("Content-type", "application/json");
		
		InputStream inputStream = null;
		String result = null;
		JSONObject jObject = null;
		try
		{
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			
			inputStream = entity.getContent();
			// json is UTF-8 by default
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
			result = sb.toString();
			jObject = new JSONObject(result);
		}
		catch (Exception e)
		{
			// Oops
		}
		finally
		{
			try
			{
				if (inputStream != null)
					inputStream.close();
			}
			catch (Exception squish)
			{
			}
		}
		
		return jObject;
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
							Date = xrp.getText();
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
	
	public static List<ArticleContent> generateArticleContent(String Content)
	{
		List<ArticleContent> contentList = new ArrayList<ArticleContent>();
		XmlPullParser xrp = Xml.newPullParser();
		
		try
		{
			xrp.setInput(new StringReader(Content));
			int eventType = xrp.getEventType();
			
			StringBuilder builder = new StringBuilder();
			boolean p_active = false;
			boolean h4_active = false;
			boolean img_active = false;
			int errorRetry = 0;
			
			while (eventType != XmlPullParser.END_DOCUMENT)
			{
				try
				{
					if (eventType == XmlPullParser.START_TAG)
					{
						String elemName = xrp.getName();
						if (!p_active && elemName.equalsIgnoreCase("p"))
						{
							p_active = true;
							builder = new StringBuilder();
							// builder.append("<p>");
						}
						else if (p_active)
						{
							if (elemName.equalsIgnoreCase("em"))
								builder.append("<em>");
							else if (elemName.equalsIgnoreCase("strong"))
								builder.append("<strong>");
							else if (elemName.equalsIgnoreCase("br"))
								builder.append("<br />");
							else if (elemName.equalsIgnoreCase("h1"))
								builder.append("<h1>");
							else if (elemName.equalsIgnoreCase("h2"))
								builder.append("<h2>");
							else if (elemName.equalsIgnoreCase("a"))
							{
								builder.append("<a ");
								int numAtribs = xrp.getAttributeCount();
								for (int i = 0; i < numAtribs; i++)
								{
									if (xrp.getAttributeName(i).equalsIgnoreCase("href"))
									{
										builder.append(" href=\"" + xrp.getAttributeValue(i) + "\"");
									}
								}
								builder.append(">");
							}
							else if (elemName.equalsIgnoreCase("img"))
							{
								img_active = true;
								String imgSource = "";
								int numAtribs = xrp.getAttributeCount();
								for (int i = 0; i < numAtribs; i++)
								{
									if (xrp.getAttributeName(i).equalsIgnoreCase("src"))
									{
										imgSource = xrp.getAttributeValue(i).trim();
										break;
									}
								}
								
								contentList.add(new ArticleContent(Data.CONTENT_TYPE_IMAGE, imgSource));
								
							}
							else if (elemName.equalsIgnoreCase("iframe"))
							{
								System.out.println("I found the iFrame!");
								String ID = "";
								int numAtribs = xrp.getAttributeCount();
								for (int i = 0; i < numAtribs; i++)
								{
									if (xrp.getAttributeName(i).equalsIgnoreCase("src"))
									{
										String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
										
										Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
										Matcher matcher = compiledPattern.matcher(xrp.getAttributeValue(i).trim());
										StringBuilder IDB = new StringBuilder();
										
										while (matcher.find())
										{
											IDB.append(matcher.group());
										}
										
										ID = IDB.toString();
										break;
									}
								}
								
								contentList.add(new ArticleContent(Data.CONTENT_TYPE_VIDEO, ID));
							}
							else if (elemName.equalsIgnoreCase("button"))
								p_active = false; // Nope.
						}
						else if (!h4_active && elemName.equalsIgnoreCase("h4"))
						{
							h4_active = true;
							builder = new StringBuilder();
							builder.append("<h4>");
						}
						else if (h4_active)
						{
							if (elemName.equalsIgnoreCase("em"))
								builder.append("<em>");
							else if (elemName.equalsIgnoreCase("strong"))
								builder.append("<strong>");
							else if (elemName.equalsIgnoreCase("br"))
								builder.append("<br />");
							else if (elemName.equalsIgnoreCase("h1"))
								builder.append("<h1>");
							else if (elemName.equalsIgnoreCase("h2"))
								builder.append("<h2>");
							else if (elemName.equalsIgnoreCase("a"))
							{
								builder.append("<a ");
								int numAtribs = xrp.getAttributeCount();
								for (int i = 0; i < numAtribs; i++)
								{
									if (xrp.getAttributeName(i).equalsIgnoreCase("href"))
									{
										builder.append(" href=\"" + xrp.getAttributeValue(i) + "\"");
									}
								}
								builder.append(">");
							}
							else if (elemName.equalsIgnoreCase("img"))
							{
								img_active = true;
								String imgSource = "";
								int numAtribs = xrp.getAttributeCount();
								for (int i = 0; i < numAtribs; i++)
								{
									if (xrp.getAttributeName(i).equalsIgnoreCase("src"))
									{
										imgSource = xrp.getAttributeValue(i).trim();
										break;
									}
								}
								
								contentList.add(new ArticleContent(Data.CONTENT_TYPE_IMAGE, imgSource));
							}
						}
					}
					else if (eventType == XmlPullParser.END_TAG)
					{
						String elemName = xrp.getName();
						if (p_active && elemName.equalsIgnoreCase("p"))
						{
							// builder.append("</p>");
							if (img_active)
							{
								if (Html.fromHtml(builder.toString()).toString().trim().length() > 0)
									contentList.add(new ArticleContent(Data.CONTENT_TYPE_TEXT, builder.toString() + "\n\n"));
							}
							else
								// if (Html.fromHtml(builder.toString()).toString().trim().length() > 0)
								contentList.add(new ArticleContent(Data.CONTENT_TYPE_TEXT, builder.toString() + "\n\n"));
							
							p_active = false;
							img_active = false;
						}
						else if (p_active)
						{
							if (elemName.equalsIgnoreCase("em"))
								builder.append("</em>");
							else if (elemName.equalsIgnoreCase("strong"))
								builder.append("</strong>");
							else if (elemName.equalsIgnoreCase("h1"))
								builder.append("</h1>");
							else if (elemName.equalsIgnoreCase("h2"))
								builder.append("</h2>");
							else if (elemName.equalsIgnoreCase("a"))
								builder.append("</a>");
						}
						else if (h4_active && elemName.equalsIgnoreCase("h4"))
						{
							builder.append("</h4>");
							if (img_active)
							{
								if (Html.fromHtml(builder.toString()).toString().trim().length() > 0)
									contentList.add(new ArticleContent(Data.CONTENT_TYPE_TEXT, builder.toString() + "\n\n"));
							}
							else
								contentList.add(new ArticleContent(Data.CONTENT_TYPE_TEXT, builder.toString() + "\n\n"));
							
							h4_active = false;
							img_active = false;
						}
					}
					else if (eventType == XmlPullParser.TEXT)
					{
						if (p_active)
						{
							builder.append(xrp.getText());
						}
						else if (h4_active)
						{
							builder.append(xrp.getText());
						}
					}
					eventType = xrp.next();
				}
				catch (Exception e)
				{
					Log.v("HAX", "xrp.next() error");
					
					if (errorRetry > 50)
						break;
					
					errorRetry++;
				}
			}
		}
		catch (Exception e)
		{
			Log.e("Error Parsing Content", e.getMessage(), e);
		}
		
		return contentList;
	}
	
	public static List<ArticleContent> generateArticleContent2(String Content)
	{
		final List<ArticleContent> contentList = new ArrayList<ArticleContent>();
		String sContent = "<article>" + Content + "</article>";
		
		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			
			DefaultHandler handler = new DefaultHandler()
			{
				StringBuilder sb;
				StringBuilder tsb;
				
				boolean p_active = false;
				boolean h2_active = false;
				boolean bsalary = false;
				
				public void startElement(String uri, String localName, String qName, Attributes attributes)
				{
					System.out.println("Start Element :" + qName);
					contentList.add(new ArticleContent(Data.CONTENT_TYPE_TEXT, "[" + qName + "]"));
					
					if (qName.equalsIgnoreCase("p"))
					{
						sb = new StringBuilder();
						tsb = new StringBuilder();
						sb.append("<p>");
						p_active = true;
					}
					else if (qName.equalsIgnoreCase("h2"))
					{
						sb = new StringBuilder();
						tsb = new StringBuilder();
						sb.append("<h2>");
						h2_active = true;
					}
					else if (p_active)
					{
						int length = attributes.getLength();
						if (qName.equalsIgnoreCase("img"))
						{
							String imgSource = "";
							for (int i = 0; i < length; i++)
							{
								if (attributes.getQName(i).equalsIgnoreCase("src"))
								{
									imgSource = attributes.getValue(i).trim();
									break;
								}
							}
							
							contentList.add(new ArticleContent(Data.CONTENT_TYPE_IMAGE, imgSource));
							// p_active = false;
						}
						else if (qName.equalsIgnoreCase("iframe"))
						{
							String ID = "";
							for (int i = 0; i < length; i++)
							{
								if (attributes.getQName(i).equalsIgnoreCase("src"))
								{
									String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
									
									Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
									Matcher matcher = compiledPattern.matcher(attributes.getValue(i).trim());
									StringBuilder IDB = new StringBuilder();
									
									while (matcher.find())
									{
										IDB.append(matcher.group());
									}
									
									ID = IDB.toString();
									break;
								}
							}
							
							contentList.add(new ArticleContent(Data.CONTENT_TYPE_VIDEO, ID));
							p_active = false;
						}
						else if (!qName.equalsIgnoreCase("div") && !qName.equalsIgnoreCase("button"))
						{
							System.out.println("HAX :" + qName);
							sb.append("<" + qName);
							tsb.append("<" + qName);
							for (int i = 0; i < length; i++)
							{
								sb.append(" " + attributes.getQName(i));
								sb.append("=\"" + attributes.getValue(i) + "\"");
								tsb.append(" " + attributes.getQName(i));
								tsb.append("=\"" + attributes.getValue(i) + "\"");
							}
							sb.append(">");
							tsb.append(">");
						}
					}
					else if (h2_active)
					{
						int length = attributes.getLength();
						
						sb.append("<" + qName);
						tsb.append("<" + qName);
						for (int i = 0; i < length; i++)
						{
							sb.append(" " + attributes.getQName(i));
							sb.append("=\"" + attributes.getValue(i) + "\"");
							tsb.append(" " + attributes.getQName(i));
							tsb.append("=\"" + attributes.getValue(i) + "\"");
						}
						sb.append(">");
						tsb.append(">");
					}
				}
				
				public void endElement(String uri, String localName, String qName)
				{
					contentList.add(new ArticleContent(Data.CONTENT_TYPE_TEXT, "[/" + qName + "]"));
					System.out.println("End Element :" + qName);
					
					if (p_active)
					{
						if (qName.equalsIgnoreCase("p") && (sb.length() - tsb.length()) > 0)
						{
							sb.append("</p>");
							contentList.add(new ArticleContent(Data.CONTENT_TYPE_TEXT, sb.toString().trim()));
							p_active = false;
						}
						else
						{
							sb.append("</" + qName + ">");
						}
					}
					else if (h2_active)
					{
						if (qName.equalsIgnoreCase("h2") && (sb.length() - tsb.length()) > 0)
						{
							sb.append("</h2>");
							contentList.add(new ArticleContent(Data.CONTENT_TYPE_TEXT, sb.toString().trim()));
							h2_active = false;
						}
						else
						{
							sb.append("</" + qName + ">");
						}
					}
				}
				
				public void characters(char ch[], int start, int length)
				{
					
					if (sb != null && p_active)
					{
						System.out.println("P : " + new String(ch, start, length));
						for (int i = start; i < start + length; i++)
						{
							sb.append(ch[i]);
						}
					}
					
					if (sb != null && h2_active)
					{
						System.out.println("H2 : " + new String(ch, start, length));
						for (int i = start; i < start + length; i++)
						{
							sb.append(ch[i]);
						}
					}
					
					if (bsalary)
					{
						System.out.println("Salary : " + new String(ch, start, length));
						bsalary = false;
					}
					
				}
				
			};
			
			saxParser.parse(new InputSource(new StringReader(sContent)), handler);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return contentList;
	}
	
	public static String pullLinks(String text)
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
				
				String resizeCrap = urlStr.substring(urlStr.lastIndexOf("?resize="), urlStr.length());
				String cleanIMG = urlStr.replace(resizeCrap, "");
				String format = cleanIMG.substring(cleanIMG.lastIndexOf("."), cleanIMG.length());
				if (format.equalsIgnoreCase(".png") || format.equalsIgnoreCase(".jpg") || format.equalsIgnoreCase(".jpeg") || format.equalsIgnoreCase(".gif") || format.equalsIgnoreCase(".webp"))
					links.add(cleanIMG);
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
	
	public static String pullImageLink(String encoded)
	{
		String img = "";
		
		try
		{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = factory.newPullParser();
			
			xpp.setInput(new StringReader(encoded));
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT)
			{
				if (eventType == XmlPullParser.START_TAG && "img".equals(xpp.getName()))
				{
					int count = xpp.getAttributeCount();
					for (int x = 0; x < count; x++)
					{
						if (xpp.getAttributeName(x).equalsIgnoreCase("src"))
						{
							img = xpp.getAttributeValue(x);
							return img;
						}
					}
				}
				eventType = xpp.next();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return img;
	}
	
	public static boolean containsLinks(String text)
	{
		ArrayList<String> links = new ArrayList<String>();
		
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
				links.add(urlStr);
			}
		}
		catch (Exception e)
		{
			Log.w("containsLinks() Parse Error", e);
		}
		
		if (links.size() > 0)
			return true;
		else
			return false;
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
	
	public static boolean hasActiveInternetConnection(Context context)
	{
		if (isNetworkConnected(context))
		{
			try
			{
				HttpURLConnection urlc = (HttpURLConnection) (new URL(MAIN_URL).openConnection());
				urlc.setRequestProperty("User-Agent", "Test");
				urlc.setRequestProperty("Connection", "close");
				urlc.setConnectTimeout(1000);
				urlc.connect();
				return (urlc.getResponseCode() == 200);
			}
			catch (IOException e)
			{
				Log.e("LOG_TAG", "Error checking internet connection", e);
			}
		}
		else
		{
			Log.d("LOG_TAG", "No network available!");
		}
		return false;
	}
	
	public static void deleteTempFile()
	{
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/Android/data/" + PACKAGE_TAG);
		File file = new File(dir, TEMP_TAG);
		
		file.delete();
	}
	
	public static String parseDate(Context context, String mDate)
	{
		SimpleDateFormat tFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
		Date date = new Date();
		try
		{
			date = tFormat.parse(mDate);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		
		final CharSequence ago = DateUtils.getRelativeDateTimeString(context, date.getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, 0);
		
		return ago.toString();
	}
	
	public static String parseRelativeDate(String mDate)
	{
		SimpleDateFormat tFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
		Date date = new Date();
		try
		{
			date = tFormat.parse(mDate);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		
		final CharSequence ago = DateUtils.getRelativeTimeSpanString(date.getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, 0);
		
		return ago.toString();
	}
	
	public static boolean isNewerDate(String nDate, String oDate)
	{
		SimpleDateFormat tFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
		Date newDate = new Date();
		Date oldDate = new Date();
		
		try
		{
			newDate = tFormat.parse(nDate);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		try
		{
			oldDate = tFormat.parse(oDate);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		
		return oldDate.before(newDate);
	}
	
	public static void zoomImageFromThumb(final View thumbView, String imgURL, final Context context)
	{
		// If there's an animation in progress, cancel it
		// immediately and proceed with this one.
		if (ActivityMain.mCurrentAnimator != null)
		{
			ActivityMain.mCurrentAnimator.cancel();
		}
		
		// Load the high-resolution "zoomed-in" image.
		Picasso.with(context).load(imgURL).error(R.drawable.loading_image_error).skipCache().into(ActivityMain.expandedImageView);
		
		// Calculate the starting and ending bounds for the zoomed-in image.
		// This step involves lots of math. Yay, math.
		final Rect startBounds = new Rect();
		final Rect finalBounds = new Rect();
		final Point globalOffset = new Point();
		
		// The start bounds are the global visible rectangle of the thumbnail,
		// and the final bounds are the global visible rectangle of the container
		// view. Also set the container view's offset as the origin for the
		// bounds, since that's the origin for the positioning animation
		// properties (X, Y).
		thumbView.getGlobalVisibleRect(startBounds);
		ActivityMain.container.getGlobalVisibleRect(finalBounds, globalOffset);
		startBounds.offset(-globalOffset.x, -globalOffset.y);
		finalBounds.offset(-globalOffset.x, -globalOffset.y);
		
		// Adjust the start bounds to be the same aspect ratio as the final
		// bounds using the "center crop" technique. This prevents undesirable
		// stretching during the animation. Also calculate the start scaling
		// factor (the end scaling factor is always 1.0).
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
		
		// Hide the thumbnail and show the zoomed-in view. When the animation
		// begins, it will position the zoomed-in view in the place of the
		// thumbnail.
		thumbView.setAlpha(0f);
		ActivityMain.expandedImageView.setVisibility(View.VISIBLE);
		Animation dAnim = AnimationUtils.loadAnimation(context, R.anim.alpha_darken);
		dAnim.setFillAfter(true);
		ActivityMain.contentFrameColor.setAnimation(dAnim);
		dAnim.start();
		ActivityMain.container.setClickable(false);
		ActivityMain.imageExpanded = true;
		
		// Set the pivot point for SCALE_X and SCALE_Y transformations
		// to the top-left corner of the zoomed-in view (the default
		// is the center of the view).
		ActivityMain.expandedImageView.setPivotX(0f);
		ActivityMain.expandedImageView.setPivotY(0f);
		
		// Construct and run the parallel animation of the four translation and
		// scale properties (X, Y, SCALE_X, and SCALE_Y).
		AnimatorSet set = new AnimatorSet();
		set.play(ObjectAnimator.ofFloat(ActivityMain.expandedImageView, View.X, startBounds.left, finalBounds.left)).with(ObjectAnimator.ofFloat(ActivityMain.expandedImageView, View.Y, startBounds.top, finalBounds.top)).with(ObjectAnimator.ofFloat(ActivityMain.expandedImageView, View.SCALE_X, startScale, 1f)).with(ObjectAnimator.ofFloat(ActivityMain.expandedImageView, View.SCALE_Y, startScale, 1f));
		set.setDuration(ActivityMain.mShortAnimationDuration);
		set.setInterpolator(new DecelerateInterpolator());
		set.addListener(new AnimatorListenerAdapter()
		{
			@Override
			public void onAnimationEnd(Animator animation)
			{
				ActivityMain.mCurrentAnimator = null;
			}
			
			@Override
			public void onAnimationCancel(Animator animation)
			{
				ActivityMain.mCurrentAnimator = null;
			}
		});
		set.start();
		ActivityMain.mCurrentAnimator = set;
		
		// Upon clicking the zoomed-in image, it should zoom back down
		// to the original bounds and show the thumbnail instead of
		// the expanded image.
		final float startScaleFinal = startScale;
		ActivityMain.expandedImageView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (ActivityMain.mCurrentAnimator != null)
				{
					ActivityMain.mCurrentAnimator.cancel();
				}
				
				// Animate the four positioning/sizing properties in parallel,
				// back to their original values.
				AnimatorSet set = new AnimatorSet();
				set.play(ObjectAnimator.ofFloat(ActivityMain.expandedImageView, View.X, startBounds.left)).with(ObjectAnimator.ofFloat(ActivityMain.expandedImageView, View.Y, startBounds.top)).with(ObjectAnimator.ofFloat(ActivityMain.expandedImageView, View.SCALE_X, startScaleFinal)).with(ObjectAnimator.ofFloat(ActivityMain.expandedImageView, View.SCALE_Y, startScaleFinal));
				set.setDuration(ActivityMain.mShortAnimationDuration);
				set.setInterpolator(new DecelerateInterpolator());
				set.addListener(new AnimatorListenerAdapter()
				{
					@Override
					public void onAnimationEnd(Animator animation)
					{
						thumbView.setAlpha(1f);
						ActivityMain.expandedImageView.setVisibility(View.GONE);
						ActivityMain.mCurrentAnimator = null;
					}
					
					@Override
					public void onAnimationCancel(Animator animation)
					{
						thumbView.setAlpha(1f);
						ActivityMain.expandedImageView.setVisibility(View.GONE);
						ActivityMain.mCurrentAnimator = null;
					}
				});
				set.start();
				ActivityMain.mCurrentAnimator = set;
				
				ActivityMain.imageExpanded = false;
				Animation bAnim = AnimationUtils.loadAnimation(context, R.anim.alpha_brighten);
				bAnim.setFillAfter(true);
				ActivityMain.contentFrameColor.setAnimation(bAnim);
				bAnim.start();
			}
		});
	}
}
