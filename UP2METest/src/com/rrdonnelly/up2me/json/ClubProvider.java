package com.rrdonnelly.up2me.json;

public class ClubProvider 
{
	private Long Id;
	private String name;	
	private Image displayImagePath;
	private boolean subscribed;
	
	public Long getId() 
	{
		return Id;
	}
	public void setId(Long id) 
	{
		Id = id;
	}
	
	public String getName() 
	{
		return name;
	}
	public void setName(String name) 
	{
		this.name = name;
	}
	
	public Image getDisplayImagePath() 
	{
		return displayImagePath;
	}
	public void setDisplayImagePath(Image displayImagePath) 
	{
		this.displayImagePath = displayImagePath;
	}
	public boolean isSubscribed() {
		return subscribed;
	}
	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
	}
	
}
