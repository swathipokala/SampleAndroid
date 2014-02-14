package com.rrdonnelly.up2me;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.rrdonnelly.up2me.adapter.OfferListAdapter;
import com.rrdonnelly.up2me.dao.DatabaseHandler;
import com.rrdonnelly.up2me.dao.OfferDAO;
import com.rrdonnelly.up2me.json.Offer;
import com.rrdonnelly.up2me.services.OfferService;
import com.rrdonnelly.up2me.util.ControlApplication;
import com.rrdonnelly.up2me.valueobjects.SavedState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul.K on 12/10/13.
 */
public class OfferListActivity extends Activity {

    public String userID = null;
    public String userToken = null;
    public String salt = null;
    public long loginUserId = 0l;
    private GridView gridView;
    private OfferListAdapter offerGridAdapter;
    String[] offersNamesList;
    public TextView textView =null;
    private Activity currentActivity = null;
    public int selectItemID;
    
    List<Offer> offersListBuild;
    
    public ImageView imgView =null;
    public Offer offer = null; 
    public long offerId =-1l;
    public boolean isRead=false;
    String[] allOffersList;
    
    public String filterText = "";
    SearchView searchView;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_offers);
        userToken = getIntent().getStringExtra("usertoken");
        userID = getIntent().getStringExtra("userName");
        salt = getIntent().getStringExtra("salt");
        loginUserId=getIntent().getLongExtra("loginUserId", 0l);
        System.out.println("OfferListActivity loginUserId :"+loginUserId);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        gridView = (GridView) findViewById(R.id.gridView);
        currentActivity = this;
        selectItemID = R.id.expDateMenu;
        
        textView= (TextView)findViewById(R.id.sortText);
        textView.setText(R.string.offerExpireDate);
        
        SavedState data=(SavedState)getLastNonConfigurationInstance();
        if (data != null){
	        selectItemID= data.getSelectedID();
	        textView.setText(data.getSortTitle());
	        offersListBuild = data.getOffersListBuild();
	        filterText = data.getSearchCriteria();
        } else {
        	offersListBuild = getAllOffers(this,loginUserId);
        }
        
        DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseHandlerInstance(currentActivity);
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

        String selectQuery = "SELECT distinct name  FROM offersservice where userid="+loginUserId;

        Cursor cursor = db.rawQuery(selectQuery, null);
        offersNamesList = new String[cursor.getCount()];
        int i = 0;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
           /* for(int i = 0; i < cursor.getCount(); i++) {
            	offersNamesList[i] = cursor.getString(0) + "|,|" + cursor.getString(1);
            	cursor.moveToNext();
            } */

            do {
                offersNamesList[i] = cursor.getString(0);
                i++;
            } while (cursor.moveToNext());
        }
        cursor.close();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = gridView.getItemAtPosition(position);
                Offer offer = (Offer)o;
                
                OfferDAO offerDAO=new OfferDAO();
                ImageView imageView = (ImageView) v.findViewById(R.id.redLine);
                imageView.setVisibility(View.INVISIBLE);
                offer.setRead(true);
                	
        		try {
        			ConnectivityManager cm = (ConnectivityManager) currentActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
        			if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
        				OfferService.readUnreadOffers(currentActivity, loginUserId, offerId, true);
        				offerDAO.updateRead(offer.getId(),1, currentActivity, 0);
        			} else {
        				offerDAO.updateRead(offer.getId(),1, currentActivity, 1);
        			}
				} catch (Exception e) {
					e.printStackTrace();
				}
                Intent offerDetails = new Intent(OfferListActivity.this, OfferDetailsActivity.class);
                offerDetails.putExtra("usertoken", userToken);
                offerDetails.putExtra("userName",userID);
                offerDetails.putExtra("salt",salt);
                offerDetails.putExtra("loginUserId",loginUserId);
                offerDetails.putExtra("offer_id", offer.getId().toString());
                offerDetails.putExtra("offer_description", offer.getName());
                offerDetails.putExtra("offers_names_list", offersNamesList);
                offerDetails.putExtra("fromScreen", "offerScreen");
                startActivityForResult(offerDetails, 0);//   Toast.makeText(SyncActivity.this, "You have chosen : " + " " + obj_itemDetails.getName(), Toast.LENGTH_LONG).show();
            }
        });

    	ImageView imageView = (ImageView) findViewById(R.id.sortArrow);
	  
    	 imageView.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View v) {
            	 showPopup();
	   }
	  });//closing the setOnClickListener method
    	 
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
            	Object o = gridView.getItemAtPosition(arg2);
            	imgView = (ImageView)arg1.findViewById(R.id.redLine);
                offer = (Offer)o;
                offerId=offer.getId();
                showDialog();
                return true;
            }
        });
        
    }
    
    public void onClick(View arg0) {
    	showPopup();
    }

    
    public void showPopup(){
    	//Creating the instance of PopupMenu
	    PopupMenu popup = new PopupMenu(OfferListActivity.this, textView);
	    //Inflating the Popup using xml file
	    popup.getMenuInflater().inflate(R.menu.offer_details, popup.getMenu());
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
	      TextView txtView= (TextView)findViewById(R.id.sortText);;
	      List<Offer> offersListFromDB =null;
	      switch (item.getItemId()) {
	      case R.id.expDateMenu:
	    	   offersListFromDB = new OfferDAO().getAllOffers(OfferListActivity.this,loginUserId, " order by expiresOnDisplayDate asc");
	    	   offersListBuild =  offersListFromDB;
	    	  displayStatment(offersListFromDB);
	    	  txtView.setText(item.getTitle());
	    	  selectItemID = R.id.expDateMenu;
	        	break;
	      case R.id.byProviderMenu:
	    	   offersListFromDB = new OfferDAO().getAllOffers(OfferListActivity.this,loginUserId, " order by name");
	    	   offersListBuild =  offersListFromDB;
	    	  displayStatment(offersListFromDB);
	    	  txtView.setText(item.getTitle());
	    	  selectItemID = R.id.byProviderMenu;
	         break;
	      }
	      
	      return true;
	     }
	    });
	 
	    popup.show();//showing popup menu
    }
    
    public List<Offer> searchOffersList(String newText) {
    	filterText = newText;
    	List<Offer> finalSearchedList = new ArrayList<Offer>();
    	if(offersListBuild.size() > 0) {
    		for (int i = 0; i < offersListBuild.size(); i++) {
    		    Offer  itemOffer = offersListBuild.get(i);
    		    String offerName = itemOffer.getName().toUpperCase();
    		    String offerExpiresOnDisplayDate = itemOffer.getExpiresOnDisplayDate().toUpperCase();
    		    String offerText = itemOffer.getOfferText().toUpperCase();
    		    
    		    if(offerName.contains(newText.toString().toUpperCase())
    		    	|| offerExpiresOnDisplayDate.contains(newText.toString().toUpperCase())
    		    	|| offerText.contains(newText.toString().toUpperCase())
    		     ) {
    		    	finalSearchedList.add(itemOffer);
    		    }
    		}
    	}
    	return finalSearchedList;
    }
    
    @Override
    public Object onRetainNonConfigurationInstance() {
    	
    	final SavedState data = new SavedState();
        data.setSelectedID(selectItemID);
        data.setSearchCriteria(filterText);
        data.setSearch(false);
        data.setSortTitle(textView.getText().toString());
        data.setOffersListBuild(offersListBuild);
        return data;
    }

    public void showHideSearchView(View view) {
   	 SearchView editSearchText = (SearchView) findViewById(R.id.searchviewText);
		if(editSearchText.getVisibility() != View.VISIBLE) {
			editSearchText.setVisibility(View.VISIBLE);
		} else {
			editSearchText.setVisibility(View.INVISIBLE);
		}
   }
    
    public void displayStatment(List<Offer> offerList) {

        final TextView txtOffersCount=(TextView) findViewById(R.id.unReadMessages);
        int count=0;
        for(Offer offer:offerList){
            if(!offer.isRead()){
                count++;
            }
        }
        txtOffersCount.setText(count+" "+ "Unread");
        offerGridAdapter = new OfferListAdapter(this, R.layout.offers_grid_row, offerList);
        gridView.setAdapter(offerGridAdapter);
    }

    public List getAllOffers(Activity activity, long userID) {
        List<Offer> offersListFromDB = new OfferDAO().getAllOffers(this,loginUserId, " order by expiresOnDisplayDate asc");

        return offersListFromDB;
    }

    public void showOffersHome(View view){
       finish();
    	/* Intent dashBoardIntent = new Intent(view.getContext(), DashBoardActivity.class);
        dashBoardIntent.putExtra("usertoken", userToken);
        dashBoardIntent.putExtra("userName", userID);
        dashBoardIntent.putExtra("salt", salt);
        dashBoardIntent.putExtra("loginUserId", loginUserId);
        startActivityForResult(dashBoardIntent, 0);*/
    }
    
    public void showDialog(){
    	AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(this);
    	 
    	// Setting Dialog Title
    	alertDialog2.setTitle("UP2ME");
    	OfferDAO offerDao = new OfferDAO();
    	Offer offer = offerDao.getOfferByID(this,offerId);
    	//Statement statement=statementDAO.getStatementByID(this, statementID);
    	// Setting Dialog Message
    	isRead=offer.isRead();
    	if (isRead){
    		alertDialog2.setMessage("Offer content will be marked as unread");
    	}else{
    		alertDialog2.setMessage("Offer content will be marked as read");
    	}
    	alertDialog2.setPositiveButton("Ok",
    	        new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog, int which) {
    	                // Write your code here to execute after dialog
    	            	if(isRead){
    	            		OfferDAO offerDAO = new OfferDAO();
    	            		imgView.setVisibility(View.VISIBLE);
    	            		setRead(false);
    	            		try {
    	            			ConnectivityManager cm = (ConnectivityManager) currentActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
    	            			if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
    	            				OfferService.readUnreadOffers(currentActivity, loginUserId, offerId, false);
    	            				offerDAO.updateRead(offerId, 0, getParent(), 0);
    	            			} else {
    	            				offerDAO.updateRead(offerId, 0, getParent(), 1);
    	            			}
							} catch (Exception e) {
								e.printStackTrace();
							}
    	            	}else{
    	            		OfferDAO offerDAO = new OfferDAO();
    	            		imgView.setVisibility(View.INVISIBLE);
    	            		setRead(true);
    	            		try {
    	            			ConnectivityManager cm = (ConnectivityManager) currentActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
    	            			if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
    	            				OfferService.readUnreadOffers(currentActivity, loginUserId, offerId, true);
    	            				offerDAO.updateRead(offerId, 1, getParent(), 0);
    	            			} else {
    	            				offerDAO.updateRead(offerId, 1, getParent(), 1);
    	            			}
							} catch (Exception e) {
								e.printStackTrace();
							}
    	            	}
    	            	
    	            	List<Offer> offersList = new OfferDAO().getAllOffers(currentActivity, loginUserId, "");  
    	            	
    	            	int bill = 0;
    	            	if(offersList != null && offersList.size() > 0){
	    	                for (int i=0;i<offersList.size();i++){
	    	                    Offer offer = offersList.get(i);
	    	                    if (!offer.isRead()){
	    	                        bill++;
	    	                    }
	    	                    
	    	                    allOffersList = new String[offersList.size()];
	    	                    allOffersList[i] = String.valueOf(offer.getId());
	    	                }
	    	                TextView textView=(TextView)findViewById(R.id.unReadMessages);
	    	                textView.setText(bill+" Unread");
    	            	}
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
    	offer.setRead(read);
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
	    
	    @Override
	    protected void onResume() {
	    	// TODO Auto-generated method stub
	    	super.onResume();
	    	
	    	displayStatment(offersListBuild);
	        
	        searchView = (SearchView) findViewById(R.id.searchviewText);
	        if(filterText != "" && filterText.length() > 0) {
	        	searchView.setVisibility(View.VISIBLE);
	        	searchView.setQuery(filterText, false);
	        	List<Offer> finalSearchedList = searchOffersList(filterText);
	        	displayStatment(finalSearchedList);
	        }
	        
	        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
	            @Override
	            public boolean onQueryTextChange(String newText) {
	            	List<Offer> finalSearchedList = searchOffersList(newText);
	            	displayStatment(finalSearchedList);
	                return true;
	            }

	            @Override
	            public boolean onQueryTextSubmit(String query) {
	                // Do something
	                return true;
	            }
	        });
	    }
}