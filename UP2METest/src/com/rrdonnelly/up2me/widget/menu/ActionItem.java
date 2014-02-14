package com.rrdonnelly.up2me.widget.menu;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;

/**
 * Action item, displayed as menu with icon and text.
 * 
 * @author Lorensius. W. L. T <lorenz@londatiga.net>
 * 
 * Contributors:
 * - Kevin Peck <kevinwpeck@gmail.com>
 *
 */
public class ActionItem {
	private Drawable icon;
	private Bitmap thumb;
	private String title;
	private long actionId = -1;
    private boolean selected;
    private boolean sticky;
    private long offerId;
    private long userId;
    private String tagColorCode;
    private Activity activity;
	
	/**
     * Constructor
     * 
     * @param actionId  Action id for case statements
     * @param title     Title
     * @param icon      Icon to use
     */
    public ActionItem(long actionId, String title, Drawable icon, boolean selected, long currentDocumentId, long userId, String tagColorCode) {
        this.title = title;
        this.icon = icon;
        this.actionId = actionId;
        this.selected = selected;
        this.offerId = offerId;
        this.userId = userId;
        this.tagColorCode = tagColorCode;
    }
    

    public ActionItem(long actionId, String title, Drawable icon, boolean selected, long offerId, long userId) {
    	this(actionId, title, icon, selected, offerId, userId, null);
    }
    
    
    public ActionItem(long actionId, String title, Drawable icon, boolean selected, long offerId) {
    	this(actionId, title, icon, selected, offerId, 0);    
    }

    
    public ActionItem(long actionId, String title, Drawable icon, boolean selected) {
    	this(actionId, title, icon, selected, 0);
    }
    

    public ActionItem(long actionId, String title, Drawable icon) {
    	this(actionId, title, icon, false);
    }

    
    /**
     * Constructor
     */
    public ActionItem() {
        this(-1, null, null, false);
    }
    
    /**
     * Constructor
     * 
     * @param actionId  Action id of the item
     * @param title     Text to show for the item
     */
    public ActionItem(int actionId, String title) {
        this(actionId, title, null, false);
    }
    
    /**
     * Constructor
     * 
     * @param icon {@link Drawable} action icon
     */
    public ActionItem(Drawable icon) {
        this(-1, null, icon, false);
    }
    
    /**
     * Constructor
     * 
     * @param actionId  Action ID of item
     * @param icon      {@link Drawable} action icon
     */
    public ActionItem(int actionId, Drawable icon) {
        this(actionId, null, icon, false);
    }
	
	/**
	 * Set action title
	 * 
	 * @param title action title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Get action title
	 * 
	 * @return action title
	 */
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * Set action icon
	 * 
	 * @param icon {@link Drawable} action icon
	 */
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	
	/**
	 * Get action icon
	 * @return  {@link Drawable} action icon
	 */
	public Drawable getIcon() {
		return this.icon;
	}
	
	 /**
     * Set action id
     * 
     * @param actionId  Action id for this action
     */
    public void setActionId(long actionId) {
        this.actionId = actionId;
    }
    
    /**
     * @return  Our action id
     */
    public long getActionId() {
        return actionId;
    }
    
    /**
     * Set sticky status of button
     * 
     * @param sticky  true for sticky, pop up sends event but does not disappear
     */
    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }
    
    /**
     * @return  true if button is sticky, menu stays visible after press
     */
    public boolean isSticky() {
        return sticky;
    }
    
	/**
	 * Set selected flag;
	 * 
	 * @param selected Flag to indicate the item is selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	/**
	 * Check if item is selected
	 * 
	 * @return true or false
	 */
	public boolean isSelected() {
		return this.selected;
	}

	/**
	 * Set thumb
	 * 
	 * @param thumb Thumb image
	 */
	public void setThumb(Bitmap thumb) {
		this.thumb = thumb;
	}
	
	/**
	 * Get thumb image
	 * 
	 * @return Thumb image
	 */
	public Bitmap getThumb() {
		return this.thumb;
	}
	

	public Activity getActivity() {
		return activity;
	}



	public void setActivity(Activity activity) {
		this.activity = activity;
	}



	public long getOfferId() {
		return offerId;
	}



	public void setOfferId(long offerId) {
		this.offerId = offerId;
	}


	public long getUserId() {
		return userId;
	}


	public void setUserId(long userId) {
		this.userId = userId;
	}


	public String getTagColorCode() {
		return tagColorCode;
	}


	public void setTagColorCode(String tagColorCode) {
		this.tagColorCode = tagColorCode;
	}
	
	


}