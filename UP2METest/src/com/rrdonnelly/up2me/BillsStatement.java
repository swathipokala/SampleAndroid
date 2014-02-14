package com.rrdonnelly.up2me;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.TextView.OnEditorActionListener;

import com.rrdonnelly.up2me.adapter.BillProviderAdapter;
import com.rrdonnelly.up2me.adapter.StatementAdapter;
import com.rrdonnelly.up2me.dao.DocumentProvidersDAO;
import com.rrdonnelly.up2me.dao.StatementDAO;
import com.rrdonnelly.up2me.json.DocumentProviders;
import com.rrdonnelly.up2me.json.Offer;
import com.rrdonnelly.up2me.json.Statement;
import com.rrdonnelly.up2me.json.StatementCategory;
import com.rrdonnelly.up2me.services.JsonDataCallback;
import com.rrdonnelly.up2me.services.StatementsService;
import com.rrdonnelly.up2me.util.ControlApplication;
import com.rrdonnelly.up2me.util.NumberFormatUtil;
import com.rrdonnelly.up2me.valueobjects.SavedState;

/**
 * Created by rahul.k on 12/3/13.
 */
public class BillsStatement extends Activity {
    private GridView gridView;
    private BillProviderAdapter billProviderAdapter;

    public String userName=null;
    public String userToken=null;
    public String salt=null;
    private Activity currentActivity = null;
    long loginUserId = 0l;
    public String fromScreen = null;
    public String parentScreen = null;
    public TextView textView =null;
    public ImageView imageView =null;
    public long statementID =-1l;
    public boolean isRead=false;
    public List<Statement> filteredstatmentsList;
    //public List<Statement> statmentsListdata = new ArrayList<Statement>();
    
    String[] allStatementsList;
    public int selectItemID;
    public Statement statement=null; 
    TextView txtView =null;
    public String filterText = "";
    
    public String sortBy = "";
    
    public LinearLayout billPayAdapterLayout = null;
    public LinearLayout billCategoriesListLayout = null;
    String[] allCategoriesList;
    public TextView totallblValue = null;
    List<Statement> statementList;  
	List<StatementCategory> billsCategoryList;
	List<DocumentProviders> documentProvidersList;	
	ToggleButton billsCheckBoxAll;
	ToggleButton providersOpenClose;
	ToggleButton categoriesOpenClose;
	private boolean isProviderShow = true;
	private boolean isCategoryShow = true;
	ScrollView billCategoriesScroll;
	private boolean resumeCalled= false;
	SearchView searchView;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bills_statement);
        currentActivity = this;
        userToken=getIntent().getStringExtra("usertoken");
        userName=getIntent().getStringExtra("userName");
        salt=getIntent().getStringExtra("salt");
        loginUserId=getIntent().getLongExtra("loginUserId", 0l);
        fromScreen=getIntent().getStringExtra("fromScreen");
        selectItemID= R.id.dueDateMenu;
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        gridView = (GridView) findViewById(R.id.gridView);
        txtView = (TextView)findViewById(R.id.sortText);
        billPayAdapterLayout = (LinearLayout)findViewById(R.id.billPayAdapter);
        billCategoriesListLayout = (LinearLayout)findViewById(R.id.billCategoriesList); 
        totallblValue = (TextView)findViewById(R.id.totallblValue);
        billsCheckBoxAll = (ToggleButton)findViewById(R.id.billsCheckBoxAll);
		providersOpenClose = (ToggleButton) findViewById(R.id.billProvidersToggleBtn);
		categoriesOpenClose = (ToggleButton) findViewById(R.id.billCategoriesToggleBtn);
		billCategoriesScroll = (ScrollView)findViewById(R.id.billCategoriesScrollView);
		searchView = (SearchView) findViewById(R.id.searchviewText);

        		
        Button btnHomeorAssistance = (Button) findViewById(R.id.homeorBackbtn);
        if("assistance".equalsIgnoreCase(fromScreen)) {
        	btnHomeorAssistance.setText("< Assistants");
        	btnHomeorAssistance.setBackgroundResource(17170445);
        } else {
        	btnHomeorAssistance.setText("");
        	btnHomeorAssistance.setBackgroundResource(R.drawable.home_page_link);
        }
            textView= (TextView)findViewById(R.id.sortText);
        	ImageView imageView = (ImageView) findViewById(R.id.sortArrow);
		  
        	 imageView.setOnClickListener(new View.OnClickListener() {

                 @Override
                 public void onClick(View v) {
                	 showPopup();
		   }
		  });//closing the setOnClickListener method
		 
        	 sortBy = " order by dueDateStr  desc,providerName asc";
        	 
        	 DocumentProvidersDAO documentProvidersDAO = new DocumentProvidersDAO();        	 
        	 
        	 SavedState data=(SavedState)getLastNonConfigurationInstance();
             if (data != null){
     	        selectItemID= data.getSelectedID();
     	        txtView.setText(data.getSortTitle());
     	        filterText = data.getSearchCriteria();
     	        if(filterText != "" && filterText.length() > 0 && data.getStatementList() != null) {
     	        	searchView.setVisibility(View.VISIBLE);
        			searchView.setQuery(filterText, false);
     	        	filteredstatmentsList = data.getStatementList();
     	        	//displayStatment(filteredstatmentsList);
     	        } else {
     	        	statementList = data.getStatementList();
     	        	//displayStatment(statementList);
     	        }
     	        billsCategoryList = data.getStatementCategoryList();
     	        documentProvidersList = data.getStatementProvidersList();
     	        
            	if(data.isProviderShow()){
            		providersOpenClose.setChecked(true);
            		isProviderShow = true;
            		gridView.setVisibility(View.VISIBLE);
            	}else{
            		providersOpenClose.setChecked(false);
            		isProviderShow = false;
            		gridView.setVisibility(View.GONE);
            	} 
            	
            	if(data.isCategoryShow()){
            		categoriesOpenClose.setChecked(true);
            		isCategoryShow = true;
            		billCategoriesScroll.setVisibility(View.VISIBLE);
            	}else{
            		categoriesOpenClose.setChecked(false);
            		isCategoryShow = false;
            		billCategoriesScroll.setVisibility(View.GONE);
            	}
            	
            	if(data.isSelectAll()){
            		billsCheckBoxAll.setChecked(true);
            		billsCheckBoxAll.setBackgroundResource(R.drawable.checkbox_ticked);
            	}
             } else {
 	        	statementList = new StatementDAO().getBillStatements(currentActivity, loginUserId,sortBy,"","");
 	        	
 	        	List<String> providerNames=new ArrayList<String>();
 	        	String providerName="";
 	        	for (Statement statement:statementList){
 	        		if(!providerNames.contains(statement.getProviderName())){
 	        			providerNames.add(statement.getProviderName());
 	        			providerName+="'"+statement.getProviderName()+"',";
 	        		}
 	        	}
 	        	if(providerName.length()>0){
 	        		providerName = providerName.substring(0, providerName.length() - 1);
 	        	}
 	        	billsCategoryList = new StatementDAO().getStatementCategory(currentActivity, "Bill");
 		    	documentProvidersList =  documentProvidersDAO.getUserDocumentProviders(this, String.valueOf(loginUserId),providerName);
 		    	
             }
             
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

					List<Statement> billStatements = com.rrdonnelly.up2me.services.StatementsService.createStatementList("Bill", json, currentActivity);
					
					List<Statement> finalSearchedList = new ArrayList<Statement>();
					
					for(Statement statement: statementList){
						for (Statement billStatement : billStatements) {
							if (billStatement.getId() == statement.getId()){
								finalSearchedList.add(statement);
							}
						}
					}
					//statmentsListBuild = finalSearchedList;
					filteredstatmentsList = finalSearchedList;
					displayStatment(finalSearchedList);
					
					
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			};
		callback.execute(webServiceUrl + "/user/MetaDataStatements?userId="+loginUserId+"&metadata="+metaData, null, null);
	}
    
    public void showDialog(){
    	AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(this);
    	 
    	// Setting Dialog Title
    	alertDialog2.setTitle("UP2ME");
    	StatementDAO statementDAO = new StatementDAO();
    	Statement statement=statementDAO.getStatementByID(this, statementID);
    	// Setting Dialog Message
    	isRead=statement.isRead();
    	if (statement.isRead()){
    		alertDialog2.setMessage("Bill content will be marked as unread");
    	}else{
    		alertDialog2.setMessage("Bill content will be marked as read");
    	}
    	alertDialog2.setPositiveButton("Ok",
    	        new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog, int which) {
    	                // Write your code here to execute after dialog
    	            	if(isRead){
    	            		StatementDAO statementDAO = new StatementDAO();
    	            		imageView.setVisibility(View.VISIBLE);
    	            		setRead(false);
    	            		try {
    	            			ConnectivityManager cm = (ConnectivityManager) currentActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
    	            			if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
    	            				StatementsService.readUnreadStatements(currentActivity, loginUserId, statementID, false);
    	            				statementDAO.updateRead(statementID, 0, getParent(), 0);
    	            			} else{
    	            				statementDAO.updateRead(statementID, 0, getParent(), 1);
    	            			}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
    	            	}else{
    	            		StatementDAO statementDAO = new StatementDAO();
    	            		imageView.setVisibility(View.INVISIBLE);
    	            		setRead(true);
    	            		try {
    	            			ConnectivityManager cm = (ConnectivityManager) currentActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
    	            			if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
    	            				StatementsService.readUnreadStatements(currentActivity, loginUserId, statementID, true);
    	            				statementDAO.updateRead(statementID, 1, getParent(), 0);
    	            			} else {
    	            				statementDAO.updateRead(statementID, 1, getParent(), 1);
    	            			}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
    	            	}
    	            	
    	            	//statmentsListBuild = new StatementDAO().getBillStatements(currentActivity,loginUserId," order by statementDisplayDate desc");
    	            	//displayStatment(statmentsListBuild);
    	            	List<Statement> statementsList = new StatementDAO().getBillStatements(currentActivity,loginUserId,"","","");  
    	            	
    	            	int bill = 0;
    	                for (int i=0;i<statementsList.size();i++){
    	                    Statement statement= statementsList.get(i);
    	                    if (statement.isBill() && !statement.isRead()){
    	                        bill++;
    	                    }
    	                    
    	                    allStatementsList[i] = String.valueOf(statement.getId());
    	                }
    	                TextView textView=(TextView)findViewById(R.id.unReadMessages);
    	                textView.setText(bill+" Unread");
    	            }
    	        });
    	// Setting Negative "NO" Btn
    	alertDialog2.setNegativeButton("Cancel",
    	        new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog, int which) {
    	              
    	                dialog.cancel();
    	            }
    	        });
    	 
    	// Showing Alert Dialog
    	alertDialog2.show();
    }
    
    public void onClick(View arg0) {
    	showPopup();
    }
    
    public void setRead(boolean read) {
		 statement.setRead(read);
	 }

    
    public void showPopup(){
    	//Creating the instance of PopupMenu
	    PopupMenu popup = new PopupMenu(BillsStatement.this, textView);
	    //Inflating the Popup using xml file
	    popup.getMenuInflater().inflate(R.menu.bills_statement, popup.getMenu());
	    
	    for (int i = 0; i < popup.getMenu().size(); ++i) {
	        MenuItem mi =  popup.getMenu().getItem(i);
	        // check the Id as you wish
	        if (mi.getItemId() == selectItemID) {
	            mi.setChecked(true); 
	        } else {
	           mi.setChecked(false);
	        }
	    }
	    
	    //registering popup with OnMenuItemClickListener
	    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
	     public boolean onMenuItemClick(MenuItem item) {
	      sortFunction(item.getItemId(), item.getTitle().toString());
	      return true;
	     }
	    });
	 
	    popup.show();//showing popup menu
    }
    
    public void showHideSearchView(View view) {
   	 SearchView editSearchText = (SearchView) findViewById(R.id.searchviewText);
		if(editSearchText.getVisibility() != View.VISIBLE) {
			editSearchText.setVisibility(View.VISIBLE);
			editSearchText.setFocusable(true);
		} else {
			editSearchText.setVisibility(View.INVISIBLE);
			editSearchText.clearFocus();
			editSearchText.setFocusable(false);
		}
   }

    public void showHome(View view){
//    	if ("home".equalsIgnoreCase(fromScreen) || fromScreen==null){
    		finish();
//	        Intent dashBoardIntent = new Intent(view.getContext(), DashBoardActivity.class);
//	        dashBoardIntent.putExtra("usertoken", userToken);
//	        dashBoardIntent.putExtra("userName", userName);
//	        dashBoardIntent.putExtra("salt", salt);
//	        dashBoardIntent.putExtra("loginUserId", loginUserId);
//	        startActivityForResult(dashBoardIntent, 0);
//    	}else if("assistance".equalsIgnoreCase(fromScreen)){
//	        Intent dashBoardIntent = new Intent(view.getContext(), AssistantsActivity.class);
//	        dashBoardIntent.putExtra("usertoken", userToken);
//	        dashBoardIntent.putExtra("userName", userName);
//	        dashBoardIntent.putExtra("salt", salt);
//	        dashBoardIntent.putExtra("loginUserId", loginUserId);
//	        startActivityForResult(dashBoardIntent, 0);
//    	}
    }

    public void displayStatment(List<Statement> statementsList) {
        int unRead=0;
        int bill=0;
        int unPaidBills=0;

        /*billProviderAdapter = new BillProviderAdapter(this, R.layout.bill_provider_adapter, statementsList);
        gridView.setAdapter(billProviderAdapter);
        allStatementsList = new String[statementsList.size()];
        for (int i=0;i<statementsList.size();i++){
            Statement statement= statementsList.get(i);
            if (statement.isBill() && !statement.isRead()){
                bill++;
            }
            if (!statement.isPaid() && statement.isBill()){
                unPaidBills++;
            }

            if(!statement.isBill() && !statement.isRead()){
                unRead++;
            }
            allStatementsList[i] = String.valueOf(statement.getId());
        }*/
        
        billPayAdapterLayout.removeAllViews();
        final LayoutInflater  inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        totallblValue.setText("0.00");
        allStatementsList = new String[statementsList.size()];
        boolean isBillsCheckedAll = true;
        for (int i=0;i<statementsList.size();i++){
            final Statement statement= statementsList.get(i);
            LinearLayout lv= (LinearLayout) inflater.inflate(R.layout.bill_pay_adapter, null);
            LinearLayout billPayMainLayout = (LinearLayout)lv.findViewById(R.id.billPayMainLayout);
            TextView billName = (TextView)lv.findViewById(R.id.billName);
            billName.setText(statement.getProviderName());
            
            TextView billDue = (TextView)lv.findViewById(R.id.billDue);
            billDue.setText("Due " + statement.getDueDate() +" • "+statement.getCategory());
            
            TextView billMinimumValue = (TextView)lv.findViewById(R.id.billMinimumValue);
            billMinimumValue.setText("$ " + NumberFormatUtil.insertCommas(new BigDecimal(statement.getMinAmountDue())));
            billMinimumValue.setTag(statement.getId());
            TextView billMinimum =(TextView)lv.findViewById(R.id.billMinimum);
            billMinimum.setTag(lv);
            
            
            TextView paidBill = (TextView)lv.findViewById(R.id.paidBill);
            paidBill.setTag(lv);
            TextView unpaidBill =(TextView)lv.findViewById(R.id.unpaidBill);
            unpaidBill.setTag(lv);
            EditText billTotalValue = (EditText)lv.findViewById(R.id.billTotalValue);
            billTotalValue.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus){
						//EditText billTotalValue = (EditText)findViewById(R.id.billTotalValue);
						if (v instanceof EditText){
							EditText billTotalValue = (EditText) v;
						if(billTotalValue.getText()!=null){
						String text=billTotalValue.getText().toString().trim();
							if(text!=null && text.length() > 0){
								billTotalValue.setText(NumberFormatUtil.insertCommas(new BigDecimal(text.replace(",", "").trim())));
								}
							}
						calculateTotal();
						}
					}
				}
			});
            if(statement.isPaid()){
            	billMinimum.setText("Paid");
            	billMinimumValue.setVisibility(View.INVISIBLE);
            	billTotalValue.setEnabled(false);
            } else {
            	 billTotalValue.setText(NumberFormatUtil.insertCommas(new BigDecimal(statement.getMinAmountDue())));
            	
            }
            paidBill.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                	LinearLayout lv= (LinearLayout) v.getTag();
                	LinearLayout minLayout = (LinearLayout )lv.findViewById(R.id.minLayout);
                	minLayout.setVisibility(View.VISIBLE);
                	LinearLayout paidLayout =(LinearLayout ) lv.findViewById(R.id.paidLayout);
                	paidLayout.setVisibility(View.GONE);
                	TextView billMinimum =(TextView)lv.findViewById(R.id.billMinimum);
                	billMinimum.setText("Paid");
                	statement.setPaid(true);
                	TextView billMinimumValue = (TextView)lv.findViewById(R.id.billMinimumValue);
                	billMinimumValue.setVisibility(View.INVISIBLE);
                	EditText billTotalValue = (EditText)lv.findViewById(R.id.billTotalValue);
                    billTotalValue.setText("");
                    billTotalValue.setEnabled(false);
                    Long id=(Long)billMinimumValue.getTag();
                    try{
                    	StatementDAO statementDAO=new StatementDAO();
            			ConnectivityManager cm = (ConnectivityManager) currentActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
            			if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
            				 
            				StatementsService.paidUnpaidStatements(currentActivity, id, true, loginUserId);
            				statementDAO.updatePaid(id,1, currentActivity, 0);
            			} else{
            				statementDAO.updatePaid(id,1, currentActivity, 1);
            			}
                    }catch(Exception e){
                    	e.printStackTrace();
                    }
                    calculateTotal();
                   }
                });
            
            unpaidBill.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                	LinearLayout lv= (LinearLayout) v.getTag();
                	LinearLayout minLayout = (LinearLayout )lv.findViewById(R.id.minLayout);
                	minLayout.setVisibility(View.VISIBLE);
                	TextView billMinimum =(TextView)lv.findViewById(R.id.billMinimum);
                	billMinimum.setText("Minimum");
                	TextView billMinimumValue = (TextView)lv.findViewById(R.id.billMinimumValue);
                	billMinimumValue.setVisibility(View.VISIBLE);
                	 EditText billTotalValue = (EditText)lv.findViewById(R.id.billTotalValue);
                     billTotalValue.setText(billMinimumValue.getText().toString().replace("$", ""));
                     billTotalValue.setEnabled(true);
                	LinearLayout paidLayout =(LinearLayout ) lv.findViewById(R.id.paidLayout);
                	paidLayout.setVisibility(View.GONE);
                	statement.setPaid(false);
                	Long id=(Long)billMinimumValue.getTag();
                    try{
                    	StatementDAO statementDAO=new StatementDAO();
            			ConnectivityManager cm = (ConnectivityManager) currentActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
            			if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
            				 
            				StatementsService.paidUnpaidStatements(currentActivity, id, false, loginUserId);
            				statementDAO.updatePaid(id,0, currentActivity, 0);
            			} else{
            				statementDAO.updatePaid(id,0, currentActivity, 1);
            			}
                    }catch(Exception e){
                    	e.printStackTrace();
                    }
                    calculateTotal();
                   }
                });

            
            billMinimum.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                	LinearLayout lv= (LinearLayout) v.getTag();
                	LinearLayout minLayout = (LinearLayout )lv.findViewById(R.id.minLayout);
                	minLayout.setVisibility(View.GONE);
                	LinearLayout paidLayout =(LinearLayout ) lv.findViewById(R.id.paidLayout);
                	paidLayout.setVisibility(View.VISIBLE);
                   }
                });
            
            TextView billBalanceValue = (TextView)lv.findViewById(R.id.billBalanceValue);
            billBalanceValue.setText("$ " + NumberFormatUtil.insertCommas(new BigDecimal(statement.getBalance())));
            
            
            
            if(i%2 == 0){
            	billPayMainLayout.setBackgroundColor(getResources().getColor(R.color.bill_gray_dark));
            }else{
            	billPayMainLayout.setBackgroundColor(getResources().getColor(R.color.bill_gray_light));
            }
            
            ImageView imageView=(ImageView) lv.findViewById(R.id.billImage);
            imageView.setId(Integer.parseInt(String.valueOf(statement.getId())));
            allStatementsList[i] = String.valueOf(statement.getId());
            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    v.getId();
                    Log.v("ImageButton click id", v.getId()+"");
                    
                    Intent billDetailsActivity = new Intent(v.getContext(), BillDetailsActivity.class);
                    billDetailsActivity.putExtra("document_id", Long.valueOf(v.getId()));
                    billDetailsActivity.putExtra("usertoken", userToken);
                    billDetailsActivity.putExtra("userName",userName);
                    billDetailsActivity.putExtra("salt",salt);
                    billDetailsActivity.putExtra("loginUserId",loginUserId);
                    billDetailsActivity.putExtra("all_statements_list", allStatementsList);
                    billDetailsActivity.putExtra("fromScreen","bills");
                    billDetailsActivity.putExtra("parentScreen",fromScreen);
                    startActivityForResult(billDetailsActivity, 0);//   Toast.makeText(SyncActivity.this, "You have chosen : " + " " + obj_itemDetails.getName(), Toast.LENGTH_LONG).show();
                    
                }
            });
            
            String imageURL = statement.getDisplayImagePathSmall().getPath();
            if(imageURL!=null && imageURL.length()>0){
                File imgFile = new File(imageURL);
                if(imgFile.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                }
            }
            
            ToggleButton billsCheckBoxChild = (ToggleButton) lv.findViewById(R.id.billsCheckBox);   
            billsCheckBoxChild.setTag(i);
            
			if(statement.isItemChecked() || billsCheckBoxAll.isChecked()){
				billsCheckBoxChild.setBackgroundResource(R.drawable.checkbox_ticked);
				billsCheckBoxChild.setChecked(true);
				String totalBalance = totallblValue.getText().toString().trim();
				String totalAmount = "";
				if (totalBalance != null && totalBalance.contains(",")) {
					totalAmount = totalBalance.replace(",", "");
				} else {
					totalAmount = totalBalance;
				}
				if(!statement.isPaid()){
				Double d = Double.parseDouble(totalAmount) + Double.parseDouble(String.valueOf(statement.getMinAmountDue()));
				totallblValue.setText(NumberFormatUtil.insertCommas(d.toString()));
				}
			}else{
				billsCheckBoxChild.setBackgroundResource(R.drawable.checkbox_un_ticked);
				billsCheckBoxChild.setChecked(false);
				isBillsCheckedAll = false;
			}
            
            billPayAdapterLayout.addView(lv);
        }
        if(statementsList != null && statementsList.size() == 0){
        	isBillsCheckedAll = false;
        }
        if(isBillsCheckedAll){
        	billsCheckBoxAll.setBackgroundResource(R.drawable.checkbox_ticked);
        	billsCheckBoxAll.setChecked(true);
        }else{
			billsCheckBoxAll.setBackgroundResource(R.drawable.checkbox_un_ticked);
			billsCheckBoxAll.setChecked(false);
        }
     }
    
    public void displayCategories(List<StatementCategory> categoryList){
    	billCategoriesListLayout.removeAllViews();
    	final LayoutInflater  inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	for(int i=0; i<categoryList.size();i++){
            StatementCategory statementCategory = categoryList.get(i);
            LinearLayout lv= (LinearLayout) inflater.inflate(R.layout.bill_categories_adapter, null);
            
            TextView billCategories = (TextView)lv.findViewById(R.id.billCategories);
            billCategories.setText(statementCategory.getDisplayName());
            
            ToggleButton billCategoriesChk = (ToggleButton)lv.findViewById(R.id.billCategoriesChk);
            //billCategoriesChk.setChecked(true);
            //billCategoriesChk.setBackgroundResource(R.drawable.checkbox_ticked);
            
			final StatementCategory statementCategoryList = categoryList.get(i);
			
			if(statementCategoryList.isItemSelected()){
				billCategoriesChk.setBackgroundResource(R.drawable.checkbox_ticked);
				billCategoriesChk.setChecked(true);
			}else{
				billCategoriesChk.setBackgroundResource(R.drawable.checkbox_un_ticked);
				billCategoriesChk.setChecked(false);
			}
			
            billCategoriesChk.setTag(i);
            
            billCategoriesChk.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					filterText = "";
					Boolean isChecked = ((ToggleButton) v).isChecked();
					if (isChecked) {
						v.setBackgroundResource(R.drawable.checkbox_ticked);
						statementCategoryList.setItemSelected(true);
					}else{
						v.setBackgroundResource(R.drawable.checkbox_un_ticked);
						statementCategoryList.setItemSelected(false);
					}
					displayFilterRecords();
				}
			});
            
            //billCategoriesListLayout.setTag(i);
            
            //allCategoriesList = new String[categoryList.size()];
           //allCategoriesList[i] = String.valueOf(statementCategory.getAccountTypeId());
            billCategoriesListLayout.addView(lv);
    	}
    }
    
	/*public void onCategoryClicked(View view) {    
		ToggleButton toggleButton= (ToggleButton) view;
		boolean on = toggleButton.isChecked();
		final StatementCategory statementCategory = cashFlowList.get(i);
		if (on) {
			toggleButton.setBackgroundResource(R.drawable.checkbox_ticked);
		} else {
			toggleButton.setBackgroundResource(R.drawable.checkbox_un_ticked);
		}
		String catergories = getCheckedCategories();
		String strSelectedProviders = getProvider();
		statementList = new StatementDAO().getBillStatements(currentActivity, loginUserId,sortBy,catergories,strSelectedProviders);
		displayStatment(statementList);
	}*/
	
	protected String getCheckedCategories() {
		String categories = "";
		for (int i = 0; i < billCategoriesListLayout.getChildCount(); i++) {
			final View mChild = billCategoriesListLayout.getChildAt(i);
			if (mChild instanceof LinearLayout) {
				TextView tvBillCategories = (TextView) mChild.findViewById(R.id.billCategories);
				final ToggleButton billCategoriesChk = (ToggleButton) mChild.findViewById(R.id.billCategoriesChk);
				if (!billCategoriesChk.isChecked()) {
					categories += "'" + tvBillCategories.getText() + "',";
				}
			}
		}
		
		if(categories != null && categories.length() > 0){
			categories = categories.substring(0,categories.length() -1);
		}
		return categories;
	}
	
	public void billsSelectAll(View view){
		totallblValue.setText("0.00");
		ToggleButton toggleButton = (ToggleButton) view;
		boolean on = toggleButton.isChecked();
		ToggleButton billsCheckBoxAll = (ToggleButton)view;
		for (int i = 0; i < billPayAdapterLayout.getChildCount(); i++) {
			final View mChild = billPayAdapterLayout.getChildAt(i);
			if (mChild instanceof LinearLayout) {
				final ToggleButton billsCheckBox = (ToggleButton) mChild.findViewById(R.id.billsCheckBox);
				Statement statement;
				if(filterText != "" && filterText.length() > 0 && filteredstatmentsList != null  && filteredstatmentsList.size() != 0) {
					statement = filteredstatmentsList.get(i);
				}else{
					statement = statementList.get(i);
				}
				EditText billTotalValue = (EditText)mChild.findViewById(R.id.billTotalValue);
				String billTotalAmnt = billTotalValue.getText().toString().trim();
				String billTotalAmount = "";
				if(billTotalAmnt != null && billTotalAmnt.length() > 0){
					billTotalAmount = billTotalAmnt.replace(",","");
				}else{
					billTotalAmount = "0.00";
				}
				
				String totalPayVal = totallblValue.getText().toString();
				String totalPayValue = "";
				if(totalPayVal != null &&  totalPayVal.length() > 0){
					totalPayValue = totalPayVal.replace(",","");
				} else{
					totalPayValue = totalPayVal;
				}
				Double d=Double.parseDouble(totalPayValue)+Double.parseDouble(billTotalAmount);
				if(on){
					billsCheckBox.setChecked(true);
					billsCheckBox.setBackgroundResource(R.drawable.checkbox_ticked);
					billsCheckBoxAll.setBackgroundResource(R.drawable.checkbox_ticked);
					totallblValue.setText(NumberFormatUtil.insertCommas(d.toString()));
					billsCheckBoxAll.setChecked(true);
					statement.setItemChecked(true);
				} else{
					billsCheckBox.setChecked(false);
					billsCheckBox.setBackgroundResource(R.drawable.checkbox_un_ticked);
					billsCheckBoxAll.setBackgroundResource(R.drawable.checkbox_un_ticked);
					totallblValue.setText("0.00");
					billsCheckBoxAll.setChecked(false);
					statement.setItemChecked(false);
				}
			}
		}
	}
	

	
	public void calculateTotalBillAmount(View view){
		ToggleButton toggleButton = (ToggleButton) view;
		boolean on = toggleButton.isChecked();
		ToggleButton billsCheckBox1 = (ToggleButton)view;
		if(on){
			billsCheckBox1.setChecked(true);
			if(filterText != "" && filterText.length() > 0 && filteredstatmentsList != null  && filteredstatmentsList.size() != 0) {
				filteredstatmentsList.get((Integer)view.getTag()).setItemChecked(true);
			}else{
				statementList.get((Integer)view.getTag()).setItemChecked(true);
			}
			
			billsCheckBox1.setBackgroundResource(R.drawable.checkbox_ticked);
		}else{
			billsCheckBox1.setChecked(false);
			if(filterText != "" && filterText.length() > 0 && filteredstatmentsList != null  && filteredstatmentsList.size() != 0) {
				filteredstatmentsList.get((Integer)view.getTag()).setItemChecked(false);
			}else{
			statementList.get((Integer)view.getTag()).setItemChecked(false);
			}
			billsCheckBox1.setBackgroundResource(R.drawable.checkbox_un_ticked);
			
			billsCheckBoxAll.setBackgroundResource(R.drawable.checkbox_un_ticked);
			billsCheckBoxAll.setChecked(false);
			
		}
		
		totallblValue.setText("0.00");
		for (int i = 0; i < billPayAdapterLayout.getChildCount(); i++) {
			final View mChild = billPayAdapterLayout.getChildAt(i);
			if (mChild instanceof LinearLayout) {
				final ToggleButton billsCheckBox = (ToggleButton) mChild.findViewById(R.id.billsCheckBox);
				Statement statement;
				if(filterText != "" && filterText.length() > 0 && filteredstatmentsList != null  && filteredstatmentsList.size() != 0) {
					statement = filteredstatmentsList.get(i);
				}else{
					statement = statementList.get(i);
				}
				EditText billTotalValue = (EditText)mChild.findViewById(R.id.billTotalValue);
				String billTotalAmnt = billTotalValue.getText().toString();
				String billTotalAmount = "";
				if(billsCheckBox.isChecked() && billTotalAmnt != null && billTotalAmnt.length() > 0){
					billTotalAmount = billTotalAmnt.replace(",","");
				}else{
					billTotalAmount = "0.00";
				}
				
				String totalPayVal = totallblValue.getText().toString();
				String totalPayValue = "";
				if(totalPayVal != null &&  totalPayVal.length() > 0){
					totalPayValue = totalPayVal.replace(",","");
				} else{
					totalPayValue = totalPayVal;
				}
				Double d=Double.parseDouble(totalPayValue)+Double.parseDouble(billTotalAmount);
				totallblValue.setText(NumberFormatUtil.insertCommas(d.toString()));
			}
		}
	}

	
	public void billIndividualSelect(View view){
		calculateTotalBillAmount(view);
		/*ToggleButton toggleButton = (ToggleButton) view;
		boolean on = toggleButton.isChecked();
		ToggleButton billsCheckBox = (ToggleButton)view;
		final View mChildPay = billPayAdapterLayout.getChildAt((Integer)view.getTag());
		statementList.get((Integer)view.getTag()); 
		if(mChildPay instanceof LinearLayout){
			EditText billTotalValue = (EditText)mChildPay.findViewById(R.id.billTotalValue);
			String billTotalAmnt = billTotalValue.getText().toString().trim();
			String billTotalAmount = "";
			if(billTotalAmnt != null && billTotalAmnt.length() > 0){
				billTotalAmount = billTotalAmnt.replace(",","");
			}else{
				billTotalAmount = "0.00";
			}
			String totalPayVal = totallblValue.getText().toString();
			String totalPayValue = "";
			if(totalPayVal != null && totalPayVal.length() > 0){
				totalPayValue = totalPayVal.replace(",","");
			} else{
				totalPayValue = totalPayVal;
			}
			if(on){
				Double d=0.00;
				if(billTotalAmount !=null && billTotalAmount.trim().length()>0){
					d=Double.parseDouble(totalPayValue)+Double.parseDouble(billTotalAmount);
				}
				
				billsCheckBox.setChecked(true);
				statementList.get((Integer)view.getTag()).setItemChecked(true);
				billsCheckBox.setBackgroundResource(R.drawable.checkbox_ticked);
				totallblValue.setText(NumberFormatUtil.insertCommas(d.toString()));
			} else{
				Double d=0.00;
				if(billTotalAmount !=null && billTotalAmount.trim().length()>0){
					d=Double.parseDouble(totalPayValue)-Double.parseDouble(billTotalAmount);
				}
				billsCheckBox.setChecked(false);
				statementList.get((Integer)view.getTag()).setItemChecked(false);
				billsCheckBox.setBackgroundResource(R.drawable.checkbox_un_ticked);
				totallblValue.setText(NumberFormatUtil.insertCommas(d.toString()));
				
				billsCheckBoxAll.setBackgroundResource(R.drawable.checkbox_un_ticked);
				billsCheckBoxAll.setChecked(false);
			}
		}*/
	}
    
    public void displyProviders(List<DocumentProviders> documentProvidersList){
    	billProviderAdapter = new BillProviderAdapter(this, R.layout.bill_provider_adapter, documentProvidersList,currentActivity, loginUserId);
    	gridView.setAdapter(billProviderAdapter);
    }

    public class StatementService extends AsyncTask<String, Void, List<Statement>> {

        // Actual download method, run in the task thread
        @Override
        protected List<Statement> doInBackground(String... params) {
            // params comes from the execute() call: params[0] is the url.
            return fetchStatements(params[0]);
        }

       // Once the image is downloaded, associates it to the imageView
        @Override
        protected void onPostExecute(List<Statement> statementsList) {
            displayStatment(statementsList);
        }

        private List<Statement> fetchStatements(String url) {
           
        	List<Statement> statementsListFromDB = new StatementDAO().getBillStatements(currentActivity,loginUserId," order by statementDisplayDate desc","","");
        	
        	return statementsListFromDB;

        }

    } 
    
    public void sortFunction( int id,String title){
		String strSelectedProviders  = getProvider();
     	String strSelectedCategories = getCheckedCategories();
    	 switch (id) {
	      case R.id.receivedDateMenu:
	    	  sortBy = " order by statementDisplayDateStr  desc,providerName asc";
	    	  statementList = new StatementDAO().getBillStatements(currentActivity,loginUserId," order by statementDisplayDateStr  desc,providerName asc",strSelectedCategories,strSelectedProviders);
	    	  displayStatment(statementList);
	    	  txtView.setText(title);
	    	  selectItemID = R.id.receivedDateMenu;
	        	break;
	      case R.id.dueDateMenu:
	    	  sortBy = " order by dueDateStr  desc,providerName asc";
	    	  statementList = new StatementDAO().getBillStatements(currentActivity,loginUserId," order by dueDateStr  desc,providerName asc",strSelectedCategories,strSelectedProviders);
	    	  displayStatment(statementList);
	    	  txtView.setText(title);
	    	  selectItemID = R.id.dueDateMenu;
	    	  break;
	      case R.id.byProviderMenu:
	    	  sortBy = " order by providerName asc,statementDisplayDateStr  desc";
	    	  statementList = new StatementDAO().getBillStatements(currentActivity,loginUserId," order by providerName asc,statementDisplayDateStr  desc",strSelectedCategories,strSelectedProviders);
	    	  displayStatment(statementList);
	    	  txtView.setText(title);
	    	  selectItemID = R.id.byProviderMenu;
	         break;
	      }
    }
    
    @Override
    public Object onRetainNonConfigurationInstance() {
    	resumeCalled=false;
        final SavedState data = new SavedState();
        data.setSelectedID(selectItemID);
        data.setSearchCriteria(filterText);
        data.setSearch(false);
        data.setSortTitle(txtView.getText().toString());
        data.setStatementCategoryList(billsCategoryList);
        data.setStatementProvidersList(documentProvidersList);
        if(filterText != "" && filterText.length() > 0) {
        	data.setStatementList(filteredstatmentsList);
        } else {
        	data.setStatementList(statementList);
        }
    	data.setProviderShow(isProviderShow);
    	data.setCategoryShow(isCategoryShow);
    	if(billsCheckBoxAll.isChecked()){
    		data.setSelectAll(true);
    	}else{
    		data.setSelectAll(false);
    	}
        return data;
    }
    
	 public ControlApplication getApp()
	    {
	        return (ControlApplication )this.getApplication();
	    }

	    @Override
	    public void onUserInteraction()
	    {
	        super.onUserInteraction();
	        
	        getApp().touch();
	        String TAG= "";
	        Log.d(TAG, "User interaction to "+this.toString());
	    }	
	    
	    
	    @Override
	    protected void onDestroy() {
	    	// TODO Auto-generated method stub
	    	super.onDestroy();
	    	System.gc();
	    }
	    
	    protected void onResume() {
	    	// TODO Auto-generated method stub
	    	super.onResume();
	    	if(!resumeCalled){
	    		resumeCalled=true;
	    if(filterText != "" && filterText.length() > 0 && filteredstatmentsList != null) {
	    	displayStatment(filteredstatmentsList);
	    }else{
            displayStatment(statementList);
	    }
            displayCategories(billsCategoryList);
            displyProviders(documentProvidersList);
	        
	        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		        @Override
		        public void onItemClick(AdapterView<?> a, View v, int position, long id) {
		        	filterText = "";
		         Object o = gridView.getItemAtPosition(position);
	           	 TextView checked = (TextView) v.findViewById(R.id.providerChecked);
	           	 
	           	 DocumentProviders documentProviders = (DocumentProviders) o;
	           	 System.out.println(gridView.getCount());
	           	 billProviderAdapter.notifyDataSetChanged();
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
						gridView.setVisibility(View.VISIBLE);
						providersOpenClose.setChecked(true);
						isProviderShow = true;
					}else{
						gridView.setVisibility(View.GONE);
						providersOpenClose.setChecked(false);
						isProviderShow = false;
					}
				}
			});
			
			categoriesOpenClose.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(categoriesOpenClose.isChecked()){
						billCategoriesScroll.setVisibility(View.VISIBLE);
						categoriesOpenClose.setChecked(true);
						isCategoryShow = true;
					}else{
						billCategoriesScroll.setVisibility(View.GONE);
						categoriesOpenClose.setChecked(false);
						isCategoryShow = false;
					}
				}
			});

			
	    	
	        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
	            @Override
	            public boolean onQueryTextChange(String newText) {
	            	
	            	ConnectivityManager cm = (ConnectivityManager) currentActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
					if (cm != null && cm.getActiveNetworkInfo() != null	&& cm.getActiveNetworkInfo().isConnected()) {
		            	if("".equals(newText)){
		            		filterText = newText;
		            		if(statementList == null) {
		            			String strSelectedProviders  = getProvider();
		           	      	 String strSelectedCategories = getCheckedCategories();
		            			
		           	      statementList = new StatementDAO().getBillStatements(currentActivity, loginUserId,sortBy,strSelectedCategories,strSelectedProviders);
		            		}
		            		displayStatment(statementList); 
		            		filteredstatmentsList = statementList;
		            		
						}else{
//							getMetaDataSearch(currentActivity, loginUserId, newText.toString().toUpperCase());
						}
					}else{
						if("".equals(newText)){
							filterText = newText;
	 						if(statementList == null){
	 							String strSelectedProviders  = getProvider();
			           	      	 String strSelectedCategories = getCheckedCategories();
			            			
			           	      statementList = new StatementDAO().getBillStatements(currentActivity, loginUserId,sortBy,strSelectedCategories,strSelectedProviders);
	 						}
	 						displayStatment(statementList);
	 						filteredstatmentsList = statementList;
	 					}else{
//						List<Statement> finalSearchedList = new ArrayList<Statement>();
//		            	if(statementList.size() > 0) {
//		            		for (int i = 0; i < statementList.size(); i++) {
//		            		    Statement itemStatement = statementList.get(i);
//		            		    String providerName = itemStatement.getProviderName().toUpperCase();
//		            		    String statementDisplayDate = itemStatement.getStatementDisplayDate().toUpperCase();
//		            		    String dueDate = itemStatement.getDueDate().toUpperCase();
//		            		    String balance = String.valueOf(itemStatement.getBalance()).toUpperCase();
//		            		    
//		            		    if(providerName.contains(newText.toString().toUpperCase())
//		            		    	|| statementDisplayDate.contains(newText.toString().toUpperCase())
//		            		    	|| dueDate.contains(newText.toString().toUpperCase()) 
//		            		    	|| balance.contains(newText.toString().toUpperCase()) 
//		            		     ) {
//		            		    	finalSearchedList.add(itemStatement);
//		            		    }
//		            		}
//		            	}
//		            	filteredstatmentsList = finalSearchedList;
//		            	displayStatment(finalSearchedList);
					}
					}
	                return true;
	            }

	            @Override
	            public boolean onQueryTextSubmit(String query) {
	            	filterText = query;
	            	ConnectivityManager cm = (ConnectivityManager) currentActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
					if (cm != null && cm.getActiveNetworkInfo() != null	&& cm.getActiveNetworkInfo().isConnected()) {
		            	if(!"".equals(query)){
							getMetaDataSearch(currentActivity, loginUserId, query.toString().toUpperCase());
						}
					}else{
						if(!"".equals(query)){
						List<Statement> finalSearchedList = new ArrayList<Statement>();
		            	if(statementList.size() > 0) {
		            		for (int i = 0; i < statementList.size(); i++) {
		            		    Statement itemStatement = statementList.get(i);
		            		    String providerName = itemStatement.getProviderName().toUpperCase();
		            		    String statementDisplayDate = itemStatement.getStatementDisplayDate().toUpperCase();
		            		    String dueDate = itemStatement.getDueDate().toUpperCase();
		            		    String balance = String.valueOf(itemStatement.getBalance()).toUpperCase();
		            		    
		            		    if(providerName.contains(query.toString().toUpperCase())
		            		    	|| statementDisplayDate.contains(query.toString().toUpperCase())
		            		    	|| dueDate.contains(query.toString().toUpperCase()) 
		            		    	|| balance.contains(query.toString().toUpperCase()) 
		            		     ) {
		            		    	finalSearchedList.add(itemStatement);
		            		    }
		            		}
		            	}
		            	filteredstatmentsList = finalSearchedList;
		            	displayStatment(finalSearchedList);
						}
					}
	                return true;
	            }
	        });
	    	}
	    }
	    
		public void displayFilterRecords(){
			 String strSelectedProviders  = getProvider();
	      	 String strSelectedCategories = getCheckedCategories();
	      	 
	      	statementList = new StatementDAO().getBillStatements(currentActivity, loginUserId,sortBy,strSelectedCategories,strSelectedProviders);
			displayStatment(statementList);
		}	
		
		public String getProvider(){
			String selectedProviders="";
			for(int i = 0; i < gridView.getCount(); i++){
				  Object o = gridView.getItemAtPosition(i);
		           
		           	 DocumentProviders documentProiders = (DocumentProviders) o;
		           	 if(!documentProiders.isItemSelected()){
		           		selectedProviders += documentProiders.getProviderId()+",";
		           	 }
				
			}
			if(selectedProviders != null && selectedProviders.length() > 0){
				selectedProviders = selectedProviders.substring(0, selectedProviders.length() - 1);
			}
			return selectedProviders;
			
		}		
		
		public void calculateTotal(){
			Double amount=0.00;
			for (int i = 0; i < billPayAdapterLayout.getChildCount(); i++) {
				final View mChild = billPayAdapterLayout.getChildAt(i);
				if (mChild instanceof LinearLayout) {
					ToggleButton tbBillCheckBox= (ToggleButton)mChild.findViewById(R.id.billsCheckBox);
					if(tbBillCheckBox.isChecked()){
						 EditText billTotalValue = (EditText)mChild.findViewById(R.id.billTotalValue);
						 String strTotalValue = billTotalValue.getText().toString().trim();
						 double value=0.00;
						 if(strTotalValue!=null && strTotalValue.length()>0){
							 value= Double.valueOf(strTotalValue.replaceAll(",", "").trim());
						 }
								 
						amount+=value;
					}
				}
			}
			totallblValue.setText(NumberFormatUtil.insertCommas(amount.toString()));
		}
		
		public void payBy(View view) {
			// Creating the instance of PopupMenu
			PopupMenu popup = new PopupMenu(BillsStatement.this, view);
			// Inflating the Popup using xml file
			popup.getMenuInflater().inflate(R.menu.payby, popup.getMenu());

			// registering popup with OnMenuItemClickListener
			popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					Intent billPayIntent =null;
					switch (item.getItemId()) {
						case R.id.googleWallet:
							billPayIntent = new Intent(currentActivity.getBaseContext(), BillPayActivity.class);
							billPayIntent.putExtra("usertoken", userToken);
							billPayIntent.putExtra("userName", userName);
							billPayIntent.putExtra("salt", salt);
							billPayIntent.putExtra("loginUserId", loginUserId);
							billPayIntent.putExtra("url", "http://www.google.com/wallet/");
							startActivityForResult(billPayIntent, 0);
							break;
					
						case R.id.payPal:
							billPayIntent = new Intent(currentActivity.getBaseContext(), BillPayActivity.class);
							billPayIntent.putExtra("usertoken", userToken);
							billPayIntent.putExtra("userName", userName);
							billPayIntent.putExtra("salt", salt);
							billPayIntent.putExtra("loginUserId", loginUserId);
							billPayIntent.putExtra("url", "Https://www.paypal.com");
							startActivityForResult(billPayIntent, 0);
							break;
						case R.id.online:
							billPayIntent = new Intent(currentActivity.getBaseContext(), BillPayActivity.class);
							billPayIntent.putExtra("usertoken", userToken);
							billPayIntent.putExtra("userName", userName);
							billPayIntent.putExtra("salt", salt);
							billPayIntent.putExtra("loginUserId", loginUserId);
							billPayIntent.putExtra("url", "Https://www.rrdonnelley.com/m-ebpp/index.html");
							startActivityForResult(billPayIntent, 0);
							break;
							
					}

					return true;
				}
			});

			popup.show();// showing popup menu
		}
		
}