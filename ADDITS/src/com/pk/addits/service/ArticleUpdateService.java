package com.pk.addits.service;

import java.util.List;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.XmlDom;
import com.pk.addits.activity.ActivityMain;
import com.pk.addits.data.Data;
import com.pk.addits.data.DatabaseHelper;
import com.pk.addits.fragment.FragmentArticle;
import com.pk.addits.model.Article;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.Html;
import android.widget.Toast;

public class ArticleUpdateService extends IntentService {
	
//	private final Activity context;
	private final IBinder mBinder = new MyBinder();
	private int result = Activity.RESULT_CANCELED;
	private DatabaseHelper db;
	private AQuery aq;

	public ArticleUpdateService() {
		super("ArticleUpdateService");
//		context = ;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	  return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
	  return mBinder;
	}
	
	public class MyBinder extends Binder {
		public ArticleUpdateService getService() {
	  return ArticleUpdateService.this;
	  }
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		aq = new AQuery(getApplicationContext());
		getFeed();
	}
	
	private void getFeed() {
		aq.ajax(Data.FEED_URL, XmlDom.class, this, "saveFeed");
	}
	
	@SuppressWarnings({ "null", "unused" })
	private void saveFeed(String url, XmlDom xml, AjaxStatus status) {
		List<XmlDom> entries = xml.tags("item");
		for (XmlDom item : entries) { 
			db.addArticle(new Article((Integer) null, item.text("title"), 
					Html.fromHtml(item.text("description")).toString(),
					item.text("content:encoded"), item.text("wfw:commentRss"),
					item.text("dc:creator"), item.text("pubDate"),
					item.text("category"), Data.pullLinks(item.text("description")),
					item.text("link"), false, false));
		}
		FragmentArticle.contentAdapter.notifyDataSetChanged();
	}

}
