package com.pk.addits.model;

import java.io.Serializable;
import java.util.List;

public class SerializableArticleList implements Serializable
{
	private static final long serialVersionUID = 1L;
	private List<Article> articleList;
	
	public SerializableArticleList(List<Article> articleList)
	{
		this.articleList = articleList;
	}
	
	public List<Article> getList()
	{
		return this.articleList;
	}
}