package com.rrdonnelly.up2me.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.rrdonnelly.up2me.BillDetailsActivity;
import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.dao.StatementDAO;
import com.rrdonnelly.up2me.json.Statement;
import com.rrdonnelly.up2me.services.StatementsService;
import com.rrdonnelly.up2me.util.NumberFormatUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul.K on 12/10/13.
 */
public class UnpaidAdapter extends ArrayAdapter<Statement> {

    private Context context;
    private int layoutResourceId;
    private List<Statement> data = new ArrayList<Statement>();
    TextView txtUnPaidAmount;
    Activity currentActivity;
	private long loginUserId = 0l;
	public String userName=null;
	public String userToken=null;
	public String salt=null;
	String[] allStatementsList;
    public UnpaidAdapter(Context context, int layoutResourceId,
                         List<Statement> data,TextView txtUnPaidAmount,Activity currentActivity, Long loginUserId,String userName,String userToken,String salt) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.txtUnPaidAmount=txtUnPaidAmount;
        this.currentActivity=currentActivity;
        this.loginUserId = loginUserId;
        allStatementsList = new String[data.size()];
        for (int i=0;i<data.size();i++){
            Statement statement= data.get(i);
            allStatementsList[i] = String.valueOf(statement.getId());
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) row.findViewById(R.id.name);
            holder.billAmount = (TextView) row.findViewById(R.id.billAmount);
            holder.statementImage = (ImageView) row.findViewById(R.id.statementImage);
            holder.redLine = (ImageView) row.findViewById(R.id.redLine);
            holder.chkButton =(ToggleButton) row.findViewById(R.id.chkButton);
            holder.txtPaidMessage = (TextView) row.findViewById(R.id.txtPaidMessage);
            holder.relativeLayout =(RelativeLayout)  row.findViewById(R.id.unPaidRelLayout);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        Statement item = data.get(position);
        holder.relativeLayout.setTag(item.getId());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            	Long id=(Long)v.getTag();
            	Intent billDetailsActivity = new Intent(v.getContext(), BillDetailsActivity.class);
                billDetailsActivity.putExtra("document_id", id);
                billDetailsActivity.putExtra("usertoken", userToken);
                billDetailsActivity.putExtra("userName",userName);
                billDetailsActivity.putExtra("salt",salt);
                billDetailsActivity.putExtra("loginUserId",loginUserId);
                billDetailsActivity.putExtra("all_statements_list", allStatementsList);
                billDetailsActivity.putExtra("fromScreen","unpaid");
                billDetailsActivity.putExtra("parentScreen","bills");
            	currentActivity.startActivityForResult(billDetailsActivity, 0);
                
            }
        });
        
      
        holder.name.setText(item.getProviderName());
        System.out.println("item.getMessage() :"+ item.getMessage());
        //holder.billAmount.setText(item.getBalance()+"");
        holder.billAmount.setText(NumberFormatUtil.insertCommas(String.valueOf(item.getBalance())+""));
        if(item.isRead()){
            holder.redLine.setVisibility(View.INVISIBLE);
        } else {
        	holder.redLine.setVisibility(View.VISIBLE);
        }
        if(item.isPaid()){
            holder.chkButton.setChecked(true);
            holder.chkButton.setBackgroundResource(R.drawable.checkbox_ticked);
            holder.txtPaidMessage.setText("Paid");
        } else{
            holder.chkButton.setBackgroundResource(R.drawable.checkbox_un_ticked);
            holder.chkButton.setChecked(false);
            holder.txtPaidMessage.setText("Unpaid");
        }
        holder.chkButton.setTag(R.id.txtPaidMessage,holder.txtPaidMessage);
        holder.chkButton.setTag(R.id.unPaidAmount,txtUnPaidAmount);
        holder.chkButton.setTag(R.id.billAmount,holder.billAmount);
        holder.chkButton.setId(Integer.parseInt(item.getId()+""));
        holder.chkButton.setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Button clicked Background"+v.getBackground());
                int id=v.getId();
                System.out.println("Value is Read id"+id);
                Boolean checked=  ((ToggleButton) v).isChecked();
                if(checked){
                	setChecked(position, true);
                    StatementDAO statementDAO=new StatementDAO();
                    v.setBackgroundResource(R.drawable.checkbox_ticked);
                    TextView txtunPaidAmount=(TextView)v.getTag(R.id.unPaidAmount);
                    String unPaidAmnt=txtunPaidAmount.getText().toString();
                    TextView txtbillAmount=(TextView)v.getTag(R.id.billAmount);
                    String billAmnt=txtbillAmount.getText().toString();
                    String billAmount="";
                    String unPaidAmount = "";
                    if(billAmnt != null && billAmnt.contains(",")){
                    	billAmount = billAmnt.replace(",", "");
                    }else{
                    	billAmount = billAmnt;
                    }
                    if(unPaidAmnt != null && unPaidAmnt.contains(",")){
                    	unPaidAmount = unPaidAmnt.replace(",", "");
                    }else{
                    	unPaidAmount = unPaidAmnt;
                    }
                    Double d=Double.parseDouble(unPaidAmount)-Double.parseDouble(billAmount);
                    /*DecimalFormat df = new DecimalFormat("#.##");
                    txtunPaidAmount.setText(df.format(d));*/
                    txtunPaidAmount.setText(NumberFormatUtil.insertCommas(d.toString()));
                    TextView txt=(TextView)v.getTag(R.id.txtPaidMessage);
                    txt.setText("Paid");
                    try{
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
                } else {
                	setChecked(position, false);
                    StatementDAO statementDAO=new StatementDAO();
                    v.setBackgroundResource(R.drawable.checkbox_un_ticked);
                    TextView txtunPaidAmount=(TextView)v.getTag(R.id.unPaidAmount);
                    String unPaidAmnt=txtunPaidAmount.getText().toString();
                    TextView txtbillAmount=(TextView)v.getTag(R.id.billAmount);
                    String billAmnt=txtbillAmount.getText().toString();
                    String billAmount="";
                    String unPaidAmount = "";
                    if(billAmnt != null && billAmnt.contains(",")){
                    	billAmount = billAmnt.replace(",", "");
                    }else{
                    	billAmount = billAmnt;
                    }
                    if(unPaidAmnt != null && unPaidAmnt.contains(",")){
                    	unPaidAmount = unPaidAmnt.replace(",", "");
                    }else{
                    	unPaidAmount = unPaidAmnt;
                    }
                    Double d=Double.parseDouble(unPaidAmount)+Double.parseDouble(billAmount);
                    /*DecimalFormat df = new DecimalFormat("#.##");
                    txtunPaidAmount.setText(df.format(d));*/
                    txtunPaidAmount.setText(NumberFormatUtil.insertCommas(d.toString()));
                    TextView txt=(TextView)v.getTag(R.id.txtPaidMessage);
                    txt.setText("Unpaid");
                    try{
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
                }
            }
        });
        //holder.image.setImageBitmap(item.getImage());
        File imgFile = new File(item.getDisplayImagePathSmall().getPath());
        if(imgFile.exists()){
            Log.w("StatementAdapter", "StatementAdapter setting image :" + imgFile.getAbsolutePath());
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.statementImage.setImageBitmap(myBitmap);
        }
        return row;
    }
    
//    public boolean isChecked(int position) {
//        return data. .get(position, false);
//    }

    public void setChecked(int position, boolean isChecked) {
      Statement st = (Statement)  data.get(position);
        st.setPaid(isChecked);
        notifyDataSetChanged();
    }

 
    static class ViewHolder {
        TextView name;
        TextView billAmount;
        ImageView statementImage;
        ImageView redLine;
        ToggleButton chkButton;
        TextView txtPaidMessage;
        RelativeLayout relativeLayout;
    }
}