package com.rrdonnelly.up2me.valueobjects;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by rahul.k on 11/28/13.
 */
public class Offer implements Serializable {

    private String name ;
    private String id ;
    private String description;
    private String imageURL;
    private String teaserImagePath;
    private String offerImagePath;
    private String expiresOnDisplayDate;
    private int tagId;
    private String statementText;
    
    public String getStatementText() {
		return statementText;
	}
	public void setStatementText(String statementText) {
		this.statementText = statementText;
	}
	public String getExpiresOnDisplayDate() {
		return expiresOnDisplayDate;
	}
	public void setExpiresOnDisplayDate(String expiresOnDisplayDate) {
		this.expiresOnDisplayDate = expiresOnDisplayDate;
	}
	public String getTeaserImagePath() {
		return teaserImagePath;
	}
	public void setTeaserImagePath(String teaserImagePath) {
		this.teaserImagePath = teaserImagePath;
	}
	public String getOfferImagePath() {
		return offerImagePath;
	}
	public void setOfferImagePath(String offerImagePath) {
		this.offerImagePath = offerImagePath;
	}
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
	public int getTagId() {
		return tagId;
	}
	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

    
}
