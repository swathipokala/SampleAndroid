package com.rrdonnelly.up2me;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.rrdonnelly.up2me.adapter.CashFlowAdapter;
import com.rrdonnelly.up2me.dao.DocumentProvidersDAO;
import com.rrdonnelly.up2me.dao.StatementDAO;
import com.rrdonnelly.up2me.json.DocumentProviders;
import com.rrdonnelly.up2me.json.Statement;
import com.rrdonnelly.up2me.json.StatementCategory;
import com.rrdonnelly.up2me.services.JsonDataCallback;
import com.rrdonnelly.up2me.util.NumberFormatUtil;
import com.rrdonnelly.up2me.valueobjects.SavedState;

/**
 * Created by rahul.k on 12/5/13.
 */
public class CashFlowActivity extends Activity {

	public String userName = null;
	public String userToken = null;
	public String salt = null;
	public long loginUserId = 0l;
	public String fromScreen = null;
	Activity currentActivity;

	LinearLayout cashFlowPayAdapter, cashFlowCategorieslist;
	GridView gridViewProviders;
	CashFlowAdapter cashFlowGridAdapter;
	TextView totallblValue;
	ToggleButton includeAllCheck;
	List<StatementCategory> cashFlowCategoryList;
	List<Statement> statementList;
	List<DocumentProviders> documentProvidersList;
	String[] allStatementsList; 
	SavedState data;
	ToggleButton providersOpenClose, categoriesOpenClose;
	public String filterText = "";
	 public List<Statement> filteredstatmentsList;
	 SearchView searchView;
	 ScrollView cashFlowCategoriesScrollView;
	 private boolean isProviderShow;
	 private boolean isCategoryShow;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_cash_flow);
		currentActivity = this;

		userToken = getIntent().getStringExtra("usertoken");
		userName = getIntent().getStringExtra("userName");
		salt = getIntent().getStringExtra("salt");
		loginUserId = getIntent().getLongExtra("loginUserId", 0l);
		fromScreen = getIntent().getStringExtra("fromScreen");
		
		buildBackorHome(fromScreen);

		cashFlowPayAdapter = (LinearLayout) findViewById(R.id.cashFlowPayAdapter);
		cashFlowCategorieslist = (LinearLayout) findViewById(R.id.cashFlowCategorieslist);
		gridViewProviders = (GridView) findViewById(R.id.gridViewProviders);
		totallblValue = (TextView) findViewById(R.id.totallblValue);
		includeAllCheck = (ToggleButton) findViewById(R.id.includeAllCheck);
		
		cashFlowCategoriesScrollView = (ScrollView) findViewById(R.id.cashFlowCategoriesScrollView);
		providersOpenClose = (ToggleButton) findViewById(R.id.providersOpenClose);
		categoriesOpenClose = (ToggleButton) findViewById(R.id.categoriesOpenClose);
		searchView = (SearchView) findViewById(R.id.searchviewText);
		
		data=(SavedState)getLastNonConfigurationInstance();
		System.out.println(data);
		
		StatementDAO statemenetDAO = new StatementDAO();
		
		DocumentProvidersDAO documentProvidersDAO = new DocumentProvidersDAO();
		
        if (data != null){
        	filterText = data.getSearchCriteria();
        	if(filterText != "" && filterText.length() > 0 && data.getStatementList() != null) {
        			searchView.setVisibility(View.VISIBLE);
        			searchView.setQuery(filterText, false);
   	    	   		filteredstatmentsList = data.getStatementList();
   	    	   		displayStatment(filteredstatmentsList);
	   	       } else {
	   	    	   	statementList = data.getStatementList();
	   	    	   	displayStatment(statementList);
	   	       }
        	cashFlowCategoryList = data.getStatementCategoryList();
        	documentProvidersList = data.getStatementProvidersList();
        	
        	
        	if(data.isProviderShow()){
        		providersOpenClose.setChecked(true);
        		isProviderShow = true;
        		gridViewProviders.setVisibility(View.VISIBLE);
        	}else{
        		providersOpenClose.setChecked(false);
        		isProviderShow = false;
        		gridViewProviders.setVisibility(View.GONE);
        	} 
        	
        	if(data.isCategoryShow()){
        		categoriesOpenClose.setChecked(true);
        		isCategoryShow = true;
        		cashFlowCategoriesScrollView.setVisibility(View.VISIBLE);
        	}else{
        		categoriesOpenClose.setChecked(false);
        		isCategoryShow = false;
        		cashFlowCategoriesScrollView.setVisibility(View.GONE);
        	}
        	
        	if(data.isSelectAll()){
        		includeAllCheck.setChecked(true);
        		includeAllCheck.setBackgroundResource(R.drawable.checkbox_ticked);
        	}
        	
        }else{
        	isCategoryShow = true;
        	providersOpenClose.setChecked(true);
    		isProviderShow = true;
    		categoriesOpenClose.setChecked(true);
    		
        	statementList = getStatements(this, Integer.parseInt(String.valueOf(loginUserId)),null,null);
        	List<String> providerNames=new ArrayList<String>();
        	String providerName="";
        	for (Statement statement:statementList){
        		if(!providerNames.contains(statement.getName())){
        			providerNames.add(statement.getName());
        			providerName+="'"+statement.getName()+"',";
        		}
        	}
        	if(providerName.length()>0){
        		providerName = providerName.substring(0, providerName.length() - 1);
        	}
        	
        	
        	cashFlowCategoryList = statemenetDAO.getStatementCategory(this, "CashFlow");
        	documentProvidersList = documentProvidersDAO.getUserDocumentProviders(this, String.valueOf(loginUserId),providerName);
        	displayStatment(statementList);
        }
        
		
		displayIncludeCategories(cashFlowCategoryList);
		displayDocumentProviders(documentProvidersList);
		
		
		gridViewProviders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> a, View v, int position, long id) {
	        	filterText = "";
	         Object o = gridViewProviders.getItemAtPosition(position);
           	 TextView checked = (TextView) v.findViewById(R.id.providerChecked);
           	 
           	 DocumentProviders documentProviders = (DocumentProviders) o;
           	
           	 
           	 System.out.println(gridViewProviders.getCount());
           	 cashFlowGridAdapter.notifyDataSetChanged();
           	 if(documentProviders.isItemSelected()){
           		 documentProviders.setItemSelected(false);
           		 checked.setVisibility(View.INVISIBLE);
           	 }else{
           		 documentProviders.setItemSelected(true);
           		 checked.setVisibility(View.VISIBLE);
           	 }
           	 
           	 
           	 displayFilterRecords();
           	 
           	 
           }
	        
		});
		
		providersOpenClose.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(providersOpenClose.isChecked()){
					gridViewProviders.setVisibility(View.VISIBLE);
					providersOpenClose.setChecked(true);
					isProviderShow = true;
				}else{
					gridViewProviders.setVisibility(View.GONE);
					providersOpenClose.setChecked(false);
					isProviderShow = false;
				}
			}
		});
		
		categoriesOpenClose.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(categoriesOpenClose.isChecked()){
					cashFlowCategoriesScrollView.setVisibility(View.VISIBLE);
					categoriesOpenClose.setChecked(true);
					isCategoryShow = true;
				}else{
					cashFlowCategoriesScrollView.setVisibility(View.GONE);
					categoriesOpenClose.setChecked(false);
					isCategoryShow = false;
				}
			}
		});
	}
	
	
	public void buildBackorHome(String fromScreen) {
		 Button btnHomeorAssistance = (Button) findViewById(R.id.homeorBackbtn);
	        if("assistance".equalsIgnoreCase(fromScreen)) {
	        	btnHomeorAssistance.setText("< Assistants");
	        	btnHomeorAssistance.setBackgroundResource(17170445);
	        } else {
	        	btnHomeorAssistance.setText("");
	        	btnHomeorAssistance.setBackgroundResource(R.drawable.home_page_link);
	        }
	}


	public void showHome(View view) {
		finish();
	}

	public ArrayList getStatements(Activity activity, int userID,String proverID,String categories) {
		StatementDAO statementDAO = new StatementDAO();
		return statementDAO.getAllCashFlowStatements(activity, loginUserId,proverID,categories);
	}

	public void displayStatment(final List<Statement> statementsList) {
		
		allStatementsList = new String[statementsList.size()];
		cashFlowPayAdapter.removeAllViews();
		Double amount = 0.00;
		totallblValue.setText(NumberFormatUtil.insertCommas(amount.toString()));
		ArrayList<Long> idList=new ArrayList<Long>();
		final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < statementsList.size(); i++) {
			LinearLayout lv = (LinearLayout) inflater.inflate(R.layout.cash_pay_adapter, null);
			LinearLayout lcashPayLayout = (LinearLayout) lv.findViewById(R.id.cashPayLayout);

			ImageView ivCashFlowImage = (ImageView) lv.findViewById(R.id.CashFlowImage);
			TextView tvCashFlowName = (TextView) lv.findViewById(R.id.cashFlowName);
			TextView tvCashFlowDueDate = (TextView) lv.findViewById(R.id.CashFlowDueDate);
			TextView tvCashFlowAmount = (TextView) lv.findViewById(R.id.CashFlowAmount);

			ToggleButton CashFlowToggleButton = (ToggleButton) lv.findViewById(R.id.CashFlowToggleButton);

			Statement statement = statementsList.get(i);
			CashFlowToggleButton.setChecked(true);
			CashFlowToggleButton.setBackgroundResource(R.drawable.checkbox_ticked);

			tvCashFlowName.setText(statement.getName());
			tvCashFlowDueDate.setText(statement.getStatementDisplayDate() + " • " + statement.getMessageType());
			tvCashFlowAmount.setText(NumberFormatUtil.insertCommas(new BigDecimal(statement.getBalance())));

			File imgFile = new File(statement.getDisplayImagePathSmall().getPath());
			if (imgFile.exists()) {
				Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
				ivCashFlowImage.setImageBitmap(myBitmap);
			}
			
			ivCashFlowImage.setTag(statement.getId());
			
			ivCashFlowImage.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					 Intent billDetailsActivity = new Intent(v.getContext(), BillDetailsActivity.class);
					 
					 
		                billDetailsActivity.putExtra("document_id",(Long)v.getTag());
		                billDetailsActivity.putExtra("usertoken", userToken);
		                billDetailsActivity.putExtra("userName",userName);
		                billDetailsActivity.putExtra("salt",salt);
		                billDetailsActivity.putExtra("loginUserId",loginUserId);
		                billDetailsActivity.putExtra("fromScreen","statements");
		                billDetailsActivity.putExtra("all_statements_list", allStatementsList);
		                startActivityForResult(billDetailsActivity, 0);// 
				}
			});
			
			if (!idList.contains(statement.getId())){
				idList.add(statement.getId());
			}
			
			if(statement.isItemChecked() || includeAllCheck.isChecked()){
				CashFlowToggleButton.setBackgroundResource(R.drawable.checkbox_ticked);
				CashFlowToggleButton.setChecked(true);
				String totalBalance = totallblValue.getText().toString().trim();
				String totalAmount = "";
				if (totalBalance != null && totalBalance.contains(",")) {
					totalAmount = totalBalance.replace(",", "");
				} else {
					totalAmount = totalBalance;
				}
				
				Double d = Double.parseDouble(totalAmount) + Double.parseDouble(String.valueOf(statement.getBalance()));
				totallblValue.setText(NumberFormatUtil.insertCommas(d.toString()));
				
			}else{
				CashFlowToggleButton.setBackgroundResource(R.drawable.checkbox_un_ticked);
				CashFlowToggleButton.setChecked(false);
			}

			if (i % 2 == 0) {
				lcashPayLayout.setBackgroundResource(R.color.bill_gray_dark);
			} else {
				lcashPayLayout.setBackgroundResource(R.color.bill_gray_light);
			}

			CashFlowToggleButton.setTag(i);

			CashFlowToggleButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Boolean isChecked = ((ToggleButton) v).isChecked();
					ToggleButton cashToggleButton = (ToggleButton) v;
					Statement statement1 = statementsList.get(Integer.parseInt(cashToggleButton.getTag().toString()));
					String totalBalance = totallblValue.getText().toString().trim();
					String totalAmount = "";
					if (totalBalance != null && totalBalance.contains(",")) {
						totalAmount = totalBalance.replace(",", "");
					} else {
						totalAmount = totalBalance;
					}

					if (isChecked) {
						v.setBackgroundResource(R.drawable.checkbox_ticked);
						cashToggleButton.setChecked(true);
						statement1.setItemChecked(true);
						Double d = Double.parseDouble(totalAmount) + Double.parseDouble(String.valueOf(statement1.getBalance()));
						totallblValue.setText(NumberFormatUtil.insertCommas(d.toString()));
					} else {
						cashToggleButton.setChecked(false);
						v.setBackgroundResource(R.drawable.checkbox_un_ticked);
						statement1.setItemChecked(false);
						Double d = Double.parseDouble(totalAmount) - Double.parseDouble(String.valueOf(statement1.getBalance()));
						totallblValue.setText(NumberFormatUtil.insertCommas(d.toString()));
						
						includeAllCheck.setBackgroundResource(R.drawable.checkbox_un_ticked);
						includeAllCheck.setChecked(false);
					}
				}
			});
			
			
			

			cashFlowPayAdapter.addView(lv);
		}
		
		if(statementsList.size() == 0){
			 amount = 0.00;
			totallblValue.setText(NumberFormatUtil.insertCommas(amount.toString()));
		}
		
		if (idList.size()>0){
			int i=0;
			allStatementsList = new String[idList.size()];
			for(Long id:idList){
				allStatementsList[i]=String.valueOf(id);
				i++;
			}
					
		}
			
	}

	private void displayIncludeCategories(final List<StatementCategory> cashFlowList) {
		cashFlowCategorieslist.removeAllViews();
		final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < cashFlowList.size(); i++) {
			LinearLayout lv = (LinearLayout) inflater.inflate(R.layout.cashflow_categories_adapter, null);
			TextView tvCashFlowCategories = (TextView) lv.findViewById(R.id.cashFlowCategories);
			final ToggleButton cashFlowCategoriesChk = (ToggleButton) lv.findViewById(R.id.cashFlowCategoriesChk);
			final StatementCategory statementCategory = cashFlowList.get(i);
			if(statementCategory.isItemSelected()){
				cashFlowCategoriesChk.setBackgroundResource(R.drawable.checkbox_ticked);
				cashFlowCategoriesChk.setChecked(true);
			}else{
				cashFlowCategoriesChk.setBackgroundResource(R.drawable.checkbox_un_ticked);
				cashFlowCategoriesChk.setChecked(false);
			}
			cashFlowCategoriesChk.setTag(i);
			cashFlowCategoriesChk.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					filterText = "";
					Boolean isChecked = ((ToggleButton) v).isChecked();
					if (isChecked) {
						v.setBackgroundResource(R.drawable.checkbox_ticked);
						statementCategory.setItemSelected(true);
					}else{
						v.setBackgroundResource(R.drawable.checkbox_un_ticked);
						statementCategory.setItemSelected(false);
					}
					displayFilterRecords();
				}
			});
			
			
			tvCashFlowCategories.setText(statementCategory.getDisplayName());
			cashFlowCategorieslist.addView(lv);
		}
	}

	protected String getCheckedCategories() {
		String categories="";
		for(int i=0;i<cashFlowCategorieslist.getChildCount();i++){
			final View mChild = cashFlowCategorieslist.getChildAt(i);
				if(mChild instanceof LinearLayout) {
					TextView tvCashFlowCategories = (TextView) mChild.findViewById(R.id.cashFlowCategories);
					final ToggleButton cashFlowCategoriesChk = (ToggleButton) mChild.findViewById(R.id.cashFlowCategoriesChk);
					if (!cashFlowCategoriesChk.isChecked()){
						categories+="'"+tvCashFlowCategories.getText()+"',";
					}
				}
			}
		System.out.println(categories);
		return categories;
	}

	private void displayDocumentProviders(List<DocumentProviders> documentProvidersList) {
		
		cashFlowGridAdapter = new CashFlowAdapter(this, R.layout.cashflow_provider_adapter, documentProvidersList, currentActivity, loginUserId, userName, userToken, salt);
		gridViewProviders.setAdapter(cashFlowGridAdapter);
		
	}
	
	public void onIncludeAllChecked(View v){
		Double amount = 0.00;
		Boolean isSelected = ((ToggleButton) v).isChecked();
		ToggleButton IncludeAll = (ToggleButton) v;
		for(int i=0;i < cashFlowPayAdapter.getChildCount();i++){
			final View mChild = cashFlowPayAdapter.getChildAt(i);{
				Statement statement;
			 if(filterText != "" && filterText.length() > 0 && filteredstatmentsList != null && filteredstatmentsList.size() != 0) {
				statement = filteredstatmentsList.get(i);
			 }else{
				 statement = statementList.get(i);
			 }
				ToggleButton CashFlowToggleButton = (ToggleButton) mChild.findViewById(R.id.CashFlowToggleButton);
				TextView tvCashFlowAmount = (TextView) mChild.findViewById(R.id.CashFlowAmount);
				String strBalance = tvCashFlowAmount.getText().toString().trim();
				String totalAmount = "";
				if (strBalance != null && strBalance.contains(",")) {
					totalAmount = strBalance.replace(",", "");
				} else {
					totalAmount = strBalance;
				}
				if(isSelected){
					CashFlowToggleButton.setChecked(true);
					CashFlowToggleButton.setBackgroundResource(R.drawable.checkbox_ticked);
					IncludeAll.setBackgroundResource(R.drawable.checkbox_ticked);
					amount += Double.parseDouble(totalAmount);
					statement.setItemChecked(true);
					IncludeAll.setChecked(true);
				}else{
					CashFlowToggleButton.setChecked(false);
					CashFlowToggleButton.setBackgroundResource(R.drawable.checkbox_un_ticked);
					IncludeAll.setBackgroundResource(R.drawable.checkbox_un_ticked);
					statement.setItemChecked(false);
					IncludeAll.setChecked(false);
				}
				
				totallblValue.setText(NumberFormatUtil.insertCommas(amount.toString()));
			}
		}
	}
	
    public void showHideSearchView(View view) {
   	 SearchView editSearchText = (SearchView) findViewById(R.id.searchviewText);
		if(editSearchText.getVisibility() != View.VISIBLE) {
			editSearchText.setVisibility(View.VISIBLE);
		} else {
			editSearchText.setVisibility(View.INVISIBLE);
		}
   }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}

	@Override
	protected void onResume() {

		super.onResume();
		
		
		 
		 searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
             @Override
             public boolean onQueryTextChange(String newText) {
             	ConnectivityManager cm = (ConnectivityManager) currentActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
 				if (cm != null && cm.getActiveNetworkInfo() != null	&& cm.getActiveNetworkInfo().isConnected()) {
 					if("".equals(newText)){
 						 filterText = newText;
 						if(statementList == null){
 							statementList = getStatements(CashFlowActivity.this, Integer.parseInt(String.valueOf(loginUserId)),null,null);
 						}
 						displayStatment(statementList);
 						filteredstatmentsList = statementList;
 					}else {
// 						getMetaDataSearch(currentActivity, loginUserId, newText.toString().toUpperCase());
 					}
 				}else{
 					if("".equals(newText)){
 						 filterText = newText;
 						if(statementList == null){
 							statementList = getStatements(CashFlowActivity.this, Integer.parseInt(String.valueOf(loginUserId)),null,null);
 						}
 						displayStatment(statementList);
 						filteredstatmentsList = statementList;
 					}else{
// 					List<Statement> finalSearchedList = new ArrayList<Statement>();
// 	            	if(statementList.size() > 0) { //statmentsListdata
// 	            		for (int i = 0; i < statementList.size(); i++) { //statmentsListdata
// 	            		    Statement itemStatement = statementList.get(i); //statmentsListdata
// 	            		    String providerName = itemStatement.getName().toUpperCase();
// 	            		    if(providerName.contains(newText.toString().toUpperCase())) {
// 	            		    	finalSearchedList.add(itemStatement);
// 	            		    }
// 	            		}
// 	            	}
// 	            	
//// 	            	statementList = finalSearchedList;
// 	            	filteredstatmentsList = finalSearchedList;
// 	            	displayStatment(finalSearchedList);
 					}
 				}
                 return true;
             }

             @Override
             public boolean onQueryTextSubmit(String query) {
                 // Do something
            	 filterText = query;
            	 ConnectivityManager cm = (ConnectivityManager) currentActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
  				if (cm != null && cm.getActiveNetworkInfo() != null	&& cm.getActiveNetworkInfo().isConnected()) {
  					if(!"".equals(query)){
  						getMetaDataSearch(currentActivity, loginUserId, filterText.toString().toUpperCase());
  					}
  				}else{
  					if(!"".equals(query)){
  						List<Statement> finalSearchedList = new ArrayList<Statement>();
  	 	            	if(statementList.size() > 0) { //statmentsListdata
  	 	            		for (int i = 0; i < statementList.size(); i++) { //statmentsListdata
  	 	            		    Statement itemStatement = statementList.get(i); //statmentsListdata
  	 	            		    String providerName = itemStatement.getName().toUpperCase();
  	 	            		    if(providerName.contains(query.toString().toUpperCase())) {
  	 	            		    	finalSearchedList.add(itemStatement);
  	 	            		    }
  	 	            		}
  	 	            	}
  	 	            	
//  	 	            	statementList = finalSearchedList;
  	 	            	filteredstatmentsList = finalSearchedList;
  	 	            	displayStatment(finalSearchedList);
  					}
  				}
                 return true;
             }
         });
		
	}
	
    public void getMetaDataSearch(final Activity currentActivity, long loginUserId, final String metaData){
		String webServiceUrl = currentActivity.getResources().getString(R.string.webservice_url);
		JSONObject serverInputs=new JSONObject();
		JsonDataCallback callback = new JsonDataCallback(currentActivity, serverInputs) {
			@Override
			public void receiveData(Object object) {
					String strObject = (String) object;
					try{
					JSONObject json = new JSONObject(strObject.toString());

					List<Statement> accountStatements = com.rrdonnelly.up2me.services.StatementsService.createStatementList("Statement", json,currentActivity);
					
					List<Statement> finalSearchedList = new ArrayList<Statement>();
					
					for(Statement statement: statementList){
						for (Statement billStatement : accountStatements) {
							if (billStatement.getId() == statement.getId()){
								finalSearchedList.add(statement);
							}
						}
					}
	            	
					displayStatment(finalSearchedList);
					filteredstatmentsList = finalSearchedList;
					
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			};
		callback.execute(webServiceUrl + "/user/MetaDataStatements?userId="+loginUserId+"&metadata="+metaData, null, null);
	}
	
	public String getProvider(){
		String selectedProviders="";
		for(int i = 0; i < gridViewProviders.getCount(); i++){
			  Object o = gridViewProviders.getItemAtPosition(i);
	           
	           	 DocumentProviders documentProiders = (DocumentProviders) o;
	           	 if(!documentProiders.isItemSelected()){
	           		selectedProviders += documentProiders.getProviderId()+",";
	           	 }
			
		}
		return selectedProviders;
		
	}
	
	public void displayFilterRecords(){
		String strSelectedProviders  = getProvider();
		if(strSelectedProviders.length() != 0){
      	 strSelectedProviders = strSelectedProviders.substring(0, strSelectedProviders.length() - 1);
		}
		
      	 String strSelectedCategories = getCheckedCategories();
      	 if(strSelectedCategories.length() != 0){
      	 strSelectedCategories = strSelectedCategories.substring(0, strSelectedCategories.length() - 1);
      	 }
      	 
         statementList = getStatements(this, Integer.parseInt(String.valueOf(loginUserId)),strSelectedProviders,strSelectedCategories);
      	 displayStatment(statementList);
	}
	
	@Override
    public Object onRetainNonConfigurationInstance() {
    	final SavedState data = new SavedState();
//    	data.setStatementList(statementList);
    	data.setStatementCategoryList(cashFlowCategoryList);
    	data.setStatementProvidersList(documentProvidersList);
    	data.setSearchCriteria(filterText);
    	if(filterText != "" && filterText.length() > 0) {
        	data.setStatementList(filteredstatmentsList);
        } else {
          	data.setStatementList(statementList);
        }
    	data.setProviderShow(isProviderShow);
    	data.setCategoryShow(isCategoryShow);
    	if(includeAllCheck.isChecked()){
    	data.setSelectAll(true);
    	}else{
    		data.setSelectAll(false);
    	}
    	return data;
    }

}