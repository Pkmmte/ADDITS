package com.pk.addits.model;

public class Article
{
	int ID;
	String Title;
	String Description;
	String Content;
	String CommentFeed;
	String Author;
	String Date;
	String Category;
	String Image;
	String URL;
	boolean Favorite;
	boolean Read;
	
	public Article()
	{
		
	}

	public Article(int ID, String Title, String Description, String Content, String CommentFeed, String Author, String Date, String Category, String Image, String URL, boolean Favorite, boolean Read)
	{
		this.ID = ID;
		this.Title = Title;
		this.Description = Description;
		this.Content = Content;
		this.CommentFeed = CommentFeed;
		this.Author = Author;
		this.Date = Date;
		this.Category = Category;
		this.Image = Image;
		this.URL = URL;
		this.Favorite = Favorite;
		this.Read = Read;
	}

	public int getID()
	{
		return ID;
	}
	
	public void setID(int ID)
	{
		this.ID = ID;
	}

	public String getTitle()
	{
		return Title;
	}
	
	public void setTitle(String Title)
	{
		this.Title = Title;
	}

	public String getDescription()
	{
		return Description;
	}
	
	public void setDescription(String Description)
	{
		this.Description = Description;
	}

	public String getContent()
	{
		return Content;
	}
	
	public void setContent(String Content)
	{
		this.Content = Content;
	}

	public String getCommentFeed()
	{
		return CommentFeed;
	}
	
	public void setCommentFeed(String CommentFeed)
	{
		this.CommentFeed = CommentFeed;
	}

	public String getAuthor()
	{
		return Author;
	}
	
	public void setAuthor(String Author)
	{
		this.Author = Author;
	}

	public String getDate()
	{
		return Date;
	}
	
	public void setDate(String Date)
	{
		this.Date = Date;
	}

	public String getCategory()
	{
		return Category;
	}
	
	public void setCategory(String Category)
	{
		this.Category = Category;
	}

	public String getImage()
	{
		return Image;
	}
	
	public void setImage(String Image)
	{
		this.Image = Image;
	}

	public String getURL()
	{
		return URL;
	}
	
	public void setURL(String URL)
	{
		this.URL = URL;
	}

	public boolean isFavorite()
	{
		return Favorite;
	}

	public void setFavorite(boolean Favorite)
	{
		this.Favorite = Favorite;
	}

	public boolean isRead()
	{
		return Read;
	}

	public void setRead(boolean Read)
	{
		this.Read = Read;
	}
}
