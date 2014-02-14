package com.rrdonnelly.up2me;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import com.rrdonnelly.up2me.adapter.StatementAdapter;
import com.rrdonnelly.up2me.dao.StatementDAO;
import com.rrdonnelly.up2me.json.Statement;
import com.rrdonnelly.up2me.services.JsonDataCallback;
import com.rrdonnelly.up2me.services.StatementsService;
import com.rrdonnelly.up2me.util.ControlApplication;
import com.rrdonnelly.up2me.valueobjects.SavedState;

/**
 * Created by Rahul.K on 12/11/13.
 */
public class MailActivity extends Activity {
    private GridView gridView;
    private StatementAdapter statementGridAdapter;

    private Activity currentActivity = null;
    public String userName = null;
    public String userToken = null;
    public String salt = null;
    public long loginUserId = 0l;
    public TextView txtView =null;
    public int selectItemID;
    public ImageView imageView =null;
    public long statementID =-1l;
    public boolean isRead=false;
    
    public List<Statement> statmentsListBuild;
    public List<Statement> filteredstatmentsList;
    
    String[] allStatementsList;
    public Statement statement=null; 
    public String filterText = "";
    
    public String sortBy = "";

    public void onCreate(Bundle savedInstanceState) {
    	currentActivity = this;
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mail);
        userToken = getIntent().getStringExtra("usertoken");
        userName = getIntent().getStringExtra("userName");
        salt = getIntent().getStringExtra("salt");
        loginUserId = getIntent().getLongExtra("loginUserId", 0l);
        System.out.println("Login user id : " + loginUserId);
        selectItemID = R.id.receivedDateMenu;
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        gridView = (GridView) findViewById(R.id.gridView);
        txtView= (TextView)findViewById(R.id.sortText);
    
  
        gridView.setLongClickable(true);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
            	Object o = gridView.getItemAtPosition(arg2);
            	imageView = (ImageView)arg1.findViewById(R.id.redLine);
            	statement = (Statement)o;
                statementID=statement.getId();
                showDialog();
                return true;
            }
        });
        
    	ImageView imageView = (ImageView) findViewById(R.id.sortArrow);
	  
    	 imageView.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View v) {
            	 showPopup();
	   }
	  });//closing the setOnClickListener method
    	 
    	 sortBy = "order by substr(statementDisplayDate, 7, 4) || '-' || substr(statementDisplayDate, 1, 2) || '-' || substr(statementDisplayDate, 4, 2)   desc,providerName asc";
    	 
    	    SavedState data=(SavedState)getLastNonConfigurationInstance();
            if (data != null){
    	        selectItemID= data.getSelectedID();
    	        txtView.setText(data.getSortTitle());
    	        filterText = data.getSearchCriteria();
    	      //  statmentsListdata = new StatementDAO().getAllAccountStatements(this, loginUserId," order by substr(periodEndDate , 7, 4)|| '-' || substr(periodEndDate , 1, 2) || '-' || substr(periodEndDate , 4, 2)  desc,providerName asc");	        
    	        //statmentsListBuild = data.getStatmentsListBuild();
    	        //displayStatment(statmentsListBuild);
    	        if(filterText != "" && filterText.length() > 0) {
      	    	   filteredstatmentsList = data.getStatmentsListBuild();
      	       } else {
      	    	  statmentsListBuild = data.getStatmentsListBuild();
      	       }
            } else {
            	StatementDAO statementDAO = new StatementDAO();
            	
                statmentsListBuild = statementDAO.getStatements(this, loginUserId,"order by substr(statementDisplayDate, 7, 4) || '-' || substr(statementDisplayDate, 1, 2) || '-' || substr(statementDisplayDate, 4, 2)   desc,providerName asc");
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
					List<Statement> accountStatements = com.rrdonnelly.up2me.services.StatementsService.createStatementList("Statement", json,currentActivity);

										
					billStatements.addAll(accountStatements);
					// System.out.println(statementIds);
					
//					statementsMap.put("Bill", billStatements);
//					statementsMap.put("Statement", accountStatements);
					
//					List<Statement> billList = statementsMap.get("Bill");
//					List<Statement> statementList = statementsMap.get("Statement");
					
					
					List<Statement> finalSearchedList = new ArrayList<Statement>();
					
					for(Statement statement: statmentsListBuild){
						for (Statement billStatement : billStatements) {
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
    
    public void onClick(View arg0) {
    	showPopup();
    }

    
    public void showPopup(){
    	//Creating the instance of PopupMenu
	    PopupMenu popup = new PopupMenu(MailActivity.this, txtView);
	    //Inflating the Popup using xml file
	    popup.getMenuInflater().inflate(R.menu.mails_statement, popup.getMenu());
	    
	    
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
	    	 sortFunction(item.getItemId(),item.getTitle().toString());
	      
	      return true;
	     }
	    });
	 
	    popup.show();//showing popup menu
    }
    
	public void showHideSearchViewFromMail(View view) {
		SearchView editSearchText = (SearchView) findViewById(R.id.searchviewText);
		if (editSearchText.getVisibility() != View.VISIBLE) {
			editSearchText.setVisibility(View.VISIBLE);
		} else {
			editSearchText.setVisibility(View.INVISIBLE);
		}
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
	    		alertDialog2.setMessage("Mail content will be marked as unread");
	    	}else{
	    		alertDialog2.setMessage("Mail Content will be marked as read");
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
	    	            			} else {
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
	    	            	
	    	            	 /* statmentsListBuild = new StatementDAO().getBillStatements(currentActivity,loginUserId," order by statementDisplayDate desc");
	    	                  displayStatment(statmentsListBuild);*/
	    	            	
	    	            	List<Statement> statementsList = new StatementDAO().getStatements(MailActivity.this, loginUserId,"");
	    	            	
	    	            	int unRead = 0;
	    	                for (int i=0;i<statementsList.size();i++){
	    	                    Statement statement= statementsList.get(i);
	    	                    if (statement.isBill() && !statement.isRead()) {
	    	                        unRead++;
	    	                    }
	    	                    if (!statement.isBill() && !statement.isRead()) {
	    	                        unRead++;
	    	                    }
	    	                }
	    	                TextView textView = (TextView) findViewById(R.id.unReadMessages);
	    	                textView.setText(unRead + " Unread");
	    	    
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
	 public void setRead(boolean read) {
		 statement.setRead(read);
	 }

    public void displayStatment(List<Statement> statementsList) {
        System.out.println("AccountStatements statementsList :" + statementsList.size());
        int unRead = 0;
        int bill = 0;
        int unPaidBills = 0;
        allStatementsList = new String[statementsList.size()];
        
        statementGridAdapter = new StatementAdapter(this, R.layout.statement_grid_row, statementsList);
        gridView.setAdapter(statementGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = gridView.getItemAtPosition(position);
                Statement statement = (Statement) o;
                ImageView imageView = (ImageView) v.findViewById(R.id.redLine);
                imageView.setVisibility(View.INVISIBLE);
                	statement.setRead(true);
                
                Intent billDetailsActivity = new Intent(v.getContext(), BillDetailsActivity.class);
                billDetailsActivity.putExtra("document_id", statement.getId());
                billDetailsActivity.putExtra("usertoken", userToken);
                billDetailsActivity.putExtra("userName",userName);
                billDetailsActivity.putExtra("salt",salt);
                billDetailsActivity.putExtra("loginUserId",loginUserId);
                billDetailsActivity.putExtra("all_statements_list", allStatementsList);
                billDetailsActivity.putExtra("fromScreen","mail");
                startActivityForResult(billDetailsActivity, 0);//   Toast.makeText(SyncActivity.this, "You have chosen : " + " " + obj_itemDetails.getName(), Toast.LENGTH_LONG).show();
            }
        });
        for (int i = 0; i < statementsList.size(); i++) {
            Statement statement = statementsList.get(i);
            if (statement.isBill() && !statement.isRead()) {
                unRead++;
            }
            if (!statement.isPaid() && statement.isBill()) {
                unPaidBills++;
            }

            if (!statement.isBill() && !statement.isRead()) {
                unRead++;
            }
            allStatementsList[i] = String.valueOf(statement.getId());
        }
        TextView textView = (TextView) findViewById(R.id.unReadMessages);
        textView.setText(unRead + " Unread");
        //statmentsListBuild = statementsList;
    }

    public void showDashHome(View view) {
    	finish();
//        Intent dashBoardIntent = new Intent(view.getContext(), DashBoardActivity.class);
//        dashBoardIntent.putExtra("usertoken", userToken);
//        dashBoardIntent.putExtra("userName", userName);
//        dashBoardIntent.putExtra("salt", salt);
//        dashBoardIntent.putExtra("loginUserId", loginUserId);
//        startActivityForResult(dashBoardIntent, 0);
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
	    
	    
	    public void sortFunction( int id,String title){
	    	//List<Statement> statementsListFromDB =null;
		      TextView txtView= (TextView)findViewById(R.id.sortText);;
		      switch (id) {
		      case R.id.receivedDateMenu:
		    	  sortBy = " order by substr(statementDisplayDate, 7, 4)|| '-' || substr(statementDisplayDate, 1, 2) || '-' || substr(statementDisplayDate, 4, 2)   desc,providerName asc";
		    	  statmentsListBuild = new StatementDAO().getStatements(MailActivity.this,loginUserId," order by substr(statementDisplayDate, 7, 4)|| '-' || substr(statementDisplayDate, 1, 2) || '-' || substr(statementDisplayDate, 4, 2)   desc,providerName asc");
		    	  displayStatment(statmentsListBuild);
		    	  txtView.setText(title);
		    	  selectItemID= R.id.receivedDateMenu;
		        	break;
		      case R.id.dueDateMenu:
		    	  sortBy = " order by substr(dueDate, 7, 4)  || '-' || substr(dueDate, 1, 2)|| '-' || substr(dueDate, 4, 2) desc,providerName asc";
		    	  statmentsListBuild = new StatementDAO().getStatements(MailActivity.this,loginUserId," order by substr(dueDate, 7, 4)  || '-' || substr(dueDate, 1, 2)|| '-' || substr(dueDate, 4, 2) desc,providerName asc");
		    	  displayStatment(statmentsListBuild);
		    	  txtView.setText(title);
		    	  selectItemID = R.id.dueDateMenu;
		    	  break;
		      case R.id.documentType:
		    	  sortBy = " order by bill desc,providerName asc,substr(statementDisplayDate, 7, 4)|| '-' || substr(statementDisplayDate, 1, 2) || '-' || substr(statementDisplayDate, 4, 2)   desc";
		    	  statmentsListBuild = new StatementDAO().getStatements(MailActivity.this,loginUserId," order by bill desc,providerName asc,substr(statementDisplayDate, 7, 4)|| '-' || substr(statementDisplayDate, 1, 2) || '-' || substr(statementDisplayDate, 4, 2)   desc");
		    	  displayStatment(statmentsListBuild);
		    	  txtView.setText(title);
		    	  selectItemID = R.id.documentType;
		    	  break;
		      case R.id.byProviderMenu:
		    	  sortBy = " order by providerName,substr(statementDisplayDate, 7, 4)|| '-' || substr(statementDisplayDate, 1, 2) || '-' || substr(statementDisplayDate, 4, 2)   desc ";
		    	  statmentsListBuild = new StatementDAO().getStatements(MailActivity.this,loginUserId," order by providerName,substr(statementDisplayDate, 7, 4)|| '-' || substr(statementDisplayDate, 1, 2) || '-' || substr(statementDisplayDate, 4, 2)   desc ");
		    	  displayStatment(statmentsListBuild);
		    	  txtView.setText(title);
		    	  selectItemID = R.id.byProviderMenu;
		         break;
		      }
	    }
	    
	    @Override
	    public Object onRetainNonConfigurationInstance() {
	        final SavedState data = new SavedState();
	        data.setSelectedID(selectItemID);
	        data.setSearchCriteria(filterText);
	        data.setSearch(false);
	        data.setSortTitle(txtView.getText().toString());
	        //data.setStatmentsListBuild(statmentsListBuild);
	        if(filterText != "" && filterText.length() > 0) {
	        	data.setStatmentsListBuild(filteredstatmentsList);
	        } else {
	        	data.setStatmentsListBuild(statmentsListBuild);
	        }
	        return data;
	    }
	    
	    @Override
	    protected void onDestroy() {
	    	// TODO Auto-generated method stub
	    	super.onDestroy();
	    	System.gc();
	    }
	    
	    @Override
	    protected void onResume() {
	    	// TODO Auto-generated method stub
	    	super.onResume();
	    	
	    	 //displayStatment(statmentsListBuild);
	         
	         SearchView searchView = (SearchView) findViewById(R.id.searchviewText);
	         
	         if(filterText != "" && filterText.length() > 0) {
		        	searchView.setVisibility(View.VISIBLE);
		        	searchView.setQuery(filterText, false);
		        	displayStatment(filteredstatmentsList);
		        } else {
//		        	statmentsListBuild =  new StatementDAO().getStatements(currentActivity, loginUserId,sortBy);
		        	displayStatment(statmentsListBuild);
		        }
	         
	         searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
	             @Override
	             public boolean onQueryTextChange(String newText) {
	            	 filterText = newText;
	             	ConnectivityManager cm = (ConnectivityManager) currentActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
	 				if (cm != null && cm.getActiveNetworkInfo() != null	&& cm.getActiveNetworkInfo().isConnected()) {
	 					
	 					if("".equals(newText)){
	 						if(statmentsListBuild == null) {
	 							
		            			statmentsListBuild = new StatementDAO().getStatements(currentActivity, loginUserId,sortBy);
		            		}
	 						displayStatment(statmentsListBuild);
	 					} else {
//	 						getMetaDataSearch(currentActivity, loginUserId, newText.toString().toUpperCase());
	 					}
	 				}else{
	 					if("".equals(newText)){
	 						if(statmentsListBuild == null){
	 							statmentsListBuild = new StatementDAO().getStatements(currentActivity, loginUserId,sortBy);
	 						}
	 						displayStatment(statmentsListBuild);
	 					}else{
//	 					List<Statement> finalSearchedList = new ArrayList<Statement>();
//	 	            	if(statmentsListBuild.size() > 0) {
//	 	            		for (int i = 0; i < statmentsListBuild.size(); i++) {
//	 	            		    Statement itemStatement = statmentsListBuild.get(i);
//	 	            		    String providerName = itemStatement.getProviderName().toUpperCase();
//	 	            		    if(providerName.contains(newText.toString().toUpperCase())) {
//	 	            		    	finalSearchedList.add(itemStatement);
//	 	            		    }
//	 	            		}
//	 	            	}
//	 	            	displayStatment(finalSearchedList);
//	 	            	statmentsListBuild = finalSearchedList;
	 					}
	 				}
	                 return true;
	             }

	             @Override
	             public boolean onQueryTextSubmit(String query) {
	            	 ConnectivityManager cm = (ConnectivityManager) currentActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
						if (cm != null && cm.getActiveNetworkInfo() != null	&& cm.getActiveNetworkInfo().isConnected()) {
			            	if(!"".equals(filterText)){
			            		getMetaDataSearch(currentActivity, loginUserId, filterText.toString().toUpperCase());
							}
						}else{
							if(!"".equals(filterText)){
							List<Statement> finalSearchedList = new ArrayList<Statement>();
		 	            	if(statmentsListBuild.size() > 0) {
		 	            		for (int i = 0; i < statmentsListBuild.size(); i++) {
		 	            		    Statement itemStatement = statmentsListBuild.get(i);
		 	            		    String providerName = itemStatement.getProviderName().toUpperCase();
		 	            		    if(providerName.contains(filterText.toString().toUpperCase())) {
		 	            		    	finalSearchedList.add(itemStatement);
		 	            		    }
		 	            		}
		 	            	}
		 	            	displayStatment(finalSearchedList);
		 	            	filteredstatmentsList = finalSearchedList;
						}
						}
	                 return true;
	             }
	         });
	    	
	    }



}
