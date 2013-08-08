package com.pk.addits.model;

public class SettingsItem
{
	String Name;
	String Description;
	String Type;
	String Value;
	
	public SettingsItem(String Name, String Description, String Type, String Value)
	{
		this.Name = Name;
		this.Description = Description;
		this.Type = Type;
		this.Value = Value;
	}
	
	public String getName()
	{
		return Name;
	}
	
	public String getDescription()
	{
		return Description;
	}
	
	public String getType()
	{
		return Type;
	}
	
	public String getValue()
	{
		return Value;
	}
}