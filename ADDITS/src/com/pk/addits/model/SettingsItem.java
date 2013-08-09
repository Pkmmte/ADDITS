package com.pk.addits.model;

public class SettingsItem
{
	String Name;
	String Description;
	String Value;
	int Type;
	
	public SettingsItem(String Name, String Description, String Value, int Type)
	{
		this.Name = Name;
		this.Description = Description;
		this.Value = Value;
		this.Type = Type;
	}
	
	public String getName()
	{
		return Name;
	}
	
	public String getDescription()
	{
		return Description;
	}
	
	public String getValue()
	{
		return Value;
	}
	
	public int getType()
	{
		return Type;
	}
}