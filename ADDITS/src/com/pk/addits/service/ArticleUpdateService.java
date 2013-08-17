package com.pk.addits.service;

import java.util.List;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.XmlDom;
import com.pk.addits.activity.ActivityMain;
import com.pk.addits.data.Data;
import com.pk.addits.data.DatabaseHelper;
import com.pk.addits.model.Article;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.Html;

public class ArticleUpdateService extends IntentService {
	
	private final Activity context = (Activity) getApplicationContext();
	private int result = Activity.RESULT_CANCELED;
	private DatabaseHelper db;
	private AQuery aq;

	public ArticleUpdateService() {
		super("ArticleUpdateService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		aq = new AQuery(context);
		getFeed();
	}
	
	private void getFeed() {
		aq.ajax(Data.FEED_URL, XmlDom.class, this, "saveFeed");
	}
	
	@SuppressWarnings({ "null", "unused" })
	private void saveFeed(String url, XmlDom xml, AjaxStatus status) {
		List<XmlDom> entries = xml.tags("item");
		
		for (XmlDom item : entries) { 
			db.addArticle(new Article(
					(Integer) null,
					item.text("title"), 
					Html.fromHtml(item.text("description")).toString(),
					item.text("content:encoded"),
					item.text("wfw:commentRss"),
					item.text("dc:creator"),
					item.text("pubDate"),
					item.text("category"),
					Data.pullLinks(item.text("description")),
					item.text("link"), 
					false, 
					false));
		}
	}

}
