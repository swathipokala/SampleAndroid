package com.rrdonnelly.up2me.valueobjects;

import java.util.List;

import com.rrdonnelly.up2me.json.CloudProvider;
import com.rrdonnelly.up2me.json.DocumentProviders;
import com.rrdonnelly.up2me.json.Offer;
import com.rrdonnelly.up2me.json.Statement;
import com.rrdonnelly.up2me.json.StatementCategory;

public class SavedState {
	
	private int selectedID;
	private String SearchCriteria;
	private boolean isSearch;
	private String sortTitle;
	private List statmentsListBuild;
	private List<Integer> selectedCloudListBuild;
	private List<Integer> selectedOfferProviderList;
	private List<Integer> selectedDocumentProviderList;
	
	private List<Statement> statementList;
	private List<StatementCategory> statementCategoryList;
	private List<DocumentProviders> statementProvidersList;
	private boolean isProviderShow;
	private boolean isCategoryShow;
	
	private boolean isSelectAll;
	
	public boolean isSelectAll() {
		return isSelectAll;
	}
	public void setSelectAll(boolean isSelectAll) {
		this.isSelectAll = isSelectAll;
	}
	public boolean isProviderShow() {
		return isProviderShow;
	}
	public void setProviderShow(boolean isProviderShow) {
		this.isProviderShow = isProviderShow;
	}
	public boolean isCategoryShow() {
		return isCategoryShow;
	}
	public void setCategoryShow(boolean isCategoryShow) {
		this.isCategoryShow = isCategoryShow;
	}
	public List<DocumentProviders> getStatementProvidersList() {
		return statementProvidersList;
	}
	public void setStatementProvidersList(List<DocumentProviders> statementProvidersList) {
		this.statementProvidersList = statementProvidersList;
	}
	public List<StatementCategory> getStatementCategoryList() {
		return statementCategoryList;
	}
	public void setStatementCategoryList(List<StatementCategory> statementCategoryList) {
		this.statementCategoryList = statementCategoryList;
	}
	public List<Statement> getStatementList() {
		return statementList;
	}
	public void setStatementList(List<Statement> statementList) {
		this.statementList = statementList;
	}
	
	public List<Integer> getSelectedOfferProviderList() {
		return selectedOfferProviderList;
	}
	public void setSelectedOfferProviderList(List<Integer> selectedOfferProviderList) {
		this.selectedOfferProviderList = selectedOfferProviderList;
	}
	public List<Integer> getSelectedDocumentProviderList() {
		return selectedDocumentProviderList;
	}
	public void setSelectedDocumentProviderList(List<Integer> selectedDocumentProviderList) {
		this.selectedDocumentProviderList = selectedDocumentProviderList;
	}
	public List<Integer> getSelectedCloudListBuild() {
		return selectedCloudListBuild;
	}
	public void setSelectedCloudListBuild(List<Integer> selectedCloudListBuild) {
		this.selectedCloudListBuild = selectedCloudListBuild;
	}
	private List<Offer> offersListBuild;
	
	private int selectedListView;
		
	public int getSelectedListView() {
		return selectedListView;
	}
	public void setSelectedListView(int selectedListView) {
		this.selectedListView = selectedListView;
	}
	public List<Offer> getOffersListBuild() {
		return offersListBuild;
	}
	public void setOffersListBuild(List<Offer> offersListBuild) {
		this.offersListBuild = offersListBuild;
	}
	
	public String getSortTitle() {
		return sortTitle;
	}
	public void setSortTitle(String sortTitle) {
		this.sortTitle = sortTitle;
	}
	
	public int getSelectedID() {
		return selectedID;
	}
	public void setSelectedID(int selectedID) {
		this.selectedID = selectedID;
	}
	public String getSearchCriteria() {
		return SearchCriteria;
	}
	public void setSearchCriteria(String searchCriteria) {
		SearchCriteria = searchCriteria;
	}
	public boolean isSearch() {
		return isSearch;
	}
	public void setSearch(boolean isSearch) {
		this.isSearch = isSearch;
	}
	public List getStatmentsListBuild() {
		return statmentsListBuild;
	}
	public void setStatmentsListBuild(List statmentsListBuild) {
		this.statmentsListBuild = statmentsListBuild;
	}

}
