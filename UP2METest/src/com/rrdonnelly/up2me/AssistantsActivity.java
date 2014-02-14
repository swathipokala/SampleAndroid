package com.rrdonnelly.up2me;

import java.util.List;

import com.rrdonnelly.up2me.dao.StatementDAO;
import com.rrdonnelly.up2me.json.Statement;
import com.rrdonnelly.up2me.util.ControlApplication;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class AssistantsActivity extends Activity {
	
		public String userID = null;
	    public String userName=null;
	    public String userToken=null;
	    public String salt=null;
	    public long loginUserId = 0l;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_assistants);
		 userToken=getIntent().getStringExtra("usertoken");
	     userName=getIntent().getStringExtra("userName");
	     salt=getIntent().getStringExtra("salt");
	     loginUserId=getIntent().getLongExtra("loginUserId", -1);
	     
//	   	List<Statement> statementsList = new StatementDAO().getAllStatementsForCount(this,loginUserId);
//  	  int unRead=0;
//        int bill=0;
//        int unPaidBills=0;
//  	 for (int i=0;i<statementsList.size();i++){
//           //LinearLayout.LayoutParams paramsExample = new LinearLayout.LayoutParams(lay);
//           Statement statement= statementsList.get(i);
//           if (statement.isBill() && !statement.isRead()){
//               bill++;
//           }
//           if (!statement.isPaid() && statement.isBill()){
//               unPaidBills++;
//           }
//
//           if(!statement.isBill() && !statement.isRead()){
//               unRead++;
//           }
//  	 }
//  	 
//  	 if(unPaidBills>0){
//           TextView upaidView=(TextView)findViewById(R.id.unPaid);
//           upaidView.setVisibility(View.VISIBLE);
//           upaidView.setText(""+unPaidBills);
//       }
//       if(bill>0){
//           TextView billtext=(TextView)findViewById(R.id.unReadBills);
//           billtext.setVisibility(View.VISIBLE);
//           billtext.setText(""+bill);
//       }
//       if(unRead>0){
//           TextView unReadtext=(TextView)findViewById(R.id.unReadStatements);
//           unReadtext.setVisibility(View.VISIBLE);
//           unReadtext.setText(""+unRead+"");
//       }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.assistants, menu);
		return true;
	}
	
    public void showHome(View view){
    	finish();
//        Intent dashBoardIntent = new Intent(view.getContext(), DashBoardActivity.class);
//        dashBoardIntent.putExtra("usertoken", userToken);
//        dashBoardIntent.putExtra("userName", userID);
//        dashBoardIntent.putExtra("salt", salt);
//        dashBoardIntent.putExtra("loginUserId", loginUserId);
//        startActivityForResult(dashBoardIntent, 0);
    }	
    
    public void showBills(View view) {
        Intent billStatement = new Intent(view.getContext(), BillsStatement.class);
        billStatement.putExtra("usertoken", userToken);
        billStatement.putExtra("userName",userName);
        billStatement.putExtra("salt",salt);
        billStatement.putExtra("loginUserId",loginUserId);
        billStatement.putExtra("fromScreen","assistance");
        startActivityForResult(billStatement, 0);
    } 
    
    public void showCashFlow(View view) {
        Intent cashFlowActivity = new Intent(view.getContext(), CashFlowActivity.class);
        cashFlowActivity.putExtra("usertoken", userToken);
        cashFlowActivity.putExtra("userName",userName);
        cashFlowActivity.putExtra("salt",salt);
        cashFlowActivity.putExtra("loginUserId",loginUserId);
        cashFlowActivity.putExtra("fromScreen","assistance");
        startActivityForResult(cashFlowActivity, 0);
    }
    
    public void showMyPlan(View view) {
        Intent myPlanActivity = new Intent(view.getContext(), MyPlanActivity.class);
        myPlanActivity.putExtra("usertoken", userToken);
        myPlanActivity.putExtra("userName",userName);
        myPlanActivity.putExtra("salt",salt);
        myPlanActivity.putExtra("loginUserId",loginUserId);
        myPlanActivity.putExtra("fromScreen","assistance");
        myPlanActivity.putExtra("header","MY PLAN");
        String URL ="http://209.11.252.135/UP2MEPlugIns/MyPlan?userToken=" + userToken	+ "&userName=" + userName + "&salt=" + salt;
        myPlanActivity.putExtra("URL",URL);
        startActivityForResult(myPlanActivity, 0);
    }    
    
    public void showStatments(View view) {
        Intent accountStatement = new Intent(view.getContext(), AccountStatements.class);
        accountStatement.putExtra("usertoken", userToken);
        accountStatement.putExtra("userName",userName);
        accountStatement.putExtra("salt",salt);
        accountStatement.putExtra("loginUserId",loginUserId);
        accountStatement.putExtra("fromScreen","assistance");
        startActivityForResult(accountStatement, 0);
    }
    
    public void showUnpaid(View view) {
        Intent unpaidActivity = new Intent(view.getContext(), UnpaidActivity.class);
        unpaidActivity.putExtra("usertoken", userToken);
        unpaidActivity.putExtra("userName",userName);
        unpaidActivity.putExtra("salt",salt);
        unpaidActivity.putExtra("loginUserId",loginUserId);
        unpaidActivity.putExtra("fromScreen","assistance");
        startActivityForResult(unpaidActivity, 0);
    }    
    
    public void showMyCreditCard(View view) {
        Intent myPlanActivity = new Intent(view.getContext(), MyPlanActivity.class);
        myPlanActivity.putExtra("usertoken", userToken);
        myPlanActivity.putExtra("userName",userName);
        myPlanActivity.putExtra("salt",salt);
        myPlanActivity.putExtra("loginUserId",loginUserId);
        myPlanActivity.putExtra("fromScreen","assistance");
        myPlanActivity.putExtra("header","CREDIT CARD");
        String URL ="http://209.11.252.135/UP2MEPlugIns/CCAssistant?userToken=" + userToken	+ "&userName=" + userName + "&salt=" + salt;
        myPlanActivity.putExtra("URL",URL);
        startActivityForResult(myPlanActivity, 0);
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
	 	List<Statement> statementsList = new StatementDAO().getAllStatementsForCount(this,loginUserId);
	  	  int unRead=0;
	        int bill=0;
	        int unPaidBills=0;
	  	 for (int i=0;i<statementsList.size();i++){
	           //LinearLayout.LayoutParams paramsExample = new LinearLayout.LayoutParams(lay);
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
	  	 }
	  	 
	  	 /*if(unPaidBills>0){
	           TextView upaidView=(TextView)findViewById(R.id.unPaid);
	           upaidView.setVisibility(View.VISIBLE);
	           upaidView.setText(""+unPaidBills);
	       }*/
	       if(unPaidBills>0){
	           TextView billtext=(TextView)findViewById(R.id.unReadBills);
	           billtext.setVisibility(View.VISIBLE);
	           billtext.setText(""+unPaidBills);
	       }
	       /*
	       if(unRead>0){
	           TextView unReadtext=(TextView)findViewById(R.id.unReadStatements);
	           unReadtext.setVisibility(View.VISIBLE);
	           unReadtext.setText(""+unRead+"");
	       }*/
	    }


}
