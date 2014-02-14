package com.rrdonnelly.up2me.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.json.Statement;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StatementAdapter extends ArrayAdapter<Statement> {
    private Context context;
    private int layoutResourceId;
    private List<Statement> data = new ArrayList<Statement>();

    public StatementAdapter(Context context, int layoutResourceId,
                           List<Statement> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) row.findViewById(R.id.name);
            holder.billDesc = (TextView) row.findViewById(R.id.billDescription);
            holder.dueAmount = (TextView) row.findViewById(R.id.dueAmount);
            holder.dueDate = (TextView) row.findViewById(R.id.dueDate);
            holder.statementImage = (ImageView) row.findViewById(R.id.statementImage);
            holder.redLine = (ImageView) row.findViewById(R.id.redLine);
            holder.amtDue = (TextView) row.findViewById(R.id.amtDue);
            holder.dueD = (TextView) row.findViewById(R.id.dueD);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
       }

        Statement item = data.get(position);
        holder.name.setText(item.getProviderName());
        holder.billDesc.setText(item.getStatementDisplayDate());
        holder.dueDate.setText(item.getDueDate());
        if(item.isRead()){
            holder.redLine.setVisibility(View.INVISIBLE);
        } else {
        	holder.redLine.setVisibility(View.VISIBLE);
        }
        if(item.isBill()){
        	holder.amtDue.setText("Amount Due : ");
        	holder.dueD.setText("Due Date : ");
        } else {
        	holder.amtDue.setText(R.string.statement_value);
        	holder.dueD.setText(R.string.statement_date);
        }
        holder.dueAmount.setText("$" + insertCommas(String.valueOf(item.getBalance())));
        //holder.image.setImageBitmap(item.getImage());
        File imgFile = new File(item.getDisplayImagePathSmall().getPath());
        if(imgFile.exists()){
            Log.w("StatementAdapter", "StatementAdapter setting image :" + imgFile.getAbsolutePath());
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.statementImage.setImageBitmap(myBitmap);
        }
        return row;
    }

    static class ViewHolder {
        TextView name;
        TextView billDesc;
        TextView dueAmount;
        TextView dueDate;
        TextView amtDue;
        TextView dueD;
        ImageView statementImage;
        ImageView redLine;
    }
    
    
    private String insertCommas(BigDecimal number) {
    	  // for your case use this pattern -> #,##0.00
    	DecimalFormat df = new DecimalFormat("#,##0.00");
    	return df.format(number);
    }

    private String insertCommas(String number) {
    	return insertCommas(new BigDecimal(number));
    }
    
}