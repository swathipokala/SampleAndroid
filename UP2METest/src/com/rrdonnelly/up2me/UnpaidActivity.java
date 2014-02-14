package com.rrdonnelly.up2me;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.rrdonnelly.up2me.adapter.CashFlowAdapter;
import com.rrdonnelly.up2me.adapter.UnpaidAdapter;
import com.rrdonnelly.up2me.dao.StatementDAO;
import com.rrdonnelly.up2me.json.Statement;
import com.rrdonnelly.up2me.util.ControlApplication;
import com.rrdonnelly.up2me.util.NumberFormatUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul.K on 12/10/13.
 */
public class UnpaidActivity extends Activity {

    private GridView gridView;
    private UnpaidAdapter unpaidGridAdapter;

    public String userID = null;
    public String userToken = null;
    public String salt = null;
    public long loginUserId = -1l;
    TextView txtUnPaidAmount =null;
    public String fromScreen = null;
    Activity currentActivity;

    public void onCreate(Bundle savedInstanceState) {
    	
    	currentActivity = this;
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_unpaid);
        userToken = getIntent().getStringExtra("usertoken");
        userID = getIntent().getStringExtra("userName");
        salt = getIntent().getStringExtra("salt");
        loginUserId=getIntent().getLongExtra("loginUserId", -1l);
        fromScreen=getIntent().getStringExtra("fromScreen");
        
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        gridView = (GridView) findViewById(R.id.gridView);
       /* switch (rotation) {
            case Surface.ROTATION_270:
            case Surface.ROTATION_90:
                gridView.setColumnWidth(300);
                break;
        }*/
        Double amount=0.00;
        String webServiceUrl = getResources().getString(R.string.webservice_url);
        
        txtUnPaidAmount=(TextView)findViewById(R.id.unPaidAmount);
        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/DigitaldreamFat.ttf");
        txtUnPaidAmount.setTypeface(tf);
        TextView txtunReadMessages = (TextView) findViewById(R.id.unReadMessages);
        txtunReadMessages.setTypeface(tf);
        
       
       

        Button btnHomeorAssistance = (Button) findViewById(R.id.homeorBackbtn);
        if("assistance".equalsIgnoreCase(fromScreen)) {
        	btnHomeorAssistance.setText("< Assistants");
        	btnHomeorAssistance.setBackgroundResource(17170445);
        } else {
        	btnHomeorAssistance.setText("");
        	btnHomeorAssistance.setBackgroundResource(R.drawable.home_page_link);
        }

    }

    public void displayStatment(List<Statement> statementsList) {
        unpaidGridAdapter = new UnpaidAdapter(this, R.layout.unpaid_gridview_row, statementsList,txtUnPaidAmount, currentActivity, loginUserId,userID,userToken,salt);
        gridView.setAdapter(unpaidGridAdapter);
    }

    public List<Statement> getStatements(Activity activity, int userID) {
        StatementDAO statementDAO = new StatementDAO();
        return statementDAO.getUnpaidStatements(activity, loginUserId);


    }
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	Double amount=0.00;
    	List<Statement> statementList=getStatements(this, 0);
        for(Statement statement:statementList){
            if(!statement.isPaid()){
                amount+=statement.getBalance();
            }

        }
        /*DecimalFormat df = new DecimalFormat("#.##");
        txtUnPaidAmount.setText(df.format(amount));*/
        txtUnPaidAmount.setText(NumberFormatUtil.insertCommas(amount.toString()));
        displayStatment(statementList);
    }

    public void showUHome(View view){
//    	if(fromScreen != null && fromScreen.equalsIgnoreCase("home")){
    		finish();
//	        Intent dashBoardIntent = new Intent(view.getContext(), DashBoardActivity.class);
//	        dashBoardIntent.putExtra("usertoken", userToken);
//	        dashBoardIntent.putExtra("userName", userID);
//	        dashBoardIntent.putExtra("salt", salt);
//	        dashBoardIntent.putExtra("loginUserId", loginUserId);
//	        startActivityForResult(dashBoardIntent, 0);
//    	}else{
//	        Intent unPaidIntent = new Intent(view.getContext(), AssistantsActivity.class);
//	        unPaidIntent.putExtra("usertoken", userToken);
//	        unPaidIntent.putExtra("userName", userID);
//	        unPaidIntent.putExtra("salt", salt);
//	        unPaidIntent.putExtra("loginUserId", loginUserId);
//	        startActivityForResult(unPaidIntent, 0);
//    	}
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



}