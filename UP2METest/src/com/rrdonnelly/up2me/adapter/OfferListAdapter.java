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
import android.widget.ToggleButton;

import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.json.Offer;
import com.rrdonnelly.up2me.json.Statement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul.K on 12/10/13.
 */
public class OfferListAdapter extends ArrayAdapter<Offer> {

    private Context context;
    private int layoutResourceId;
    private List<Offer> data = null;

    public OfferListAdapter(Context context, int layoutResourceId,
                           List<Offer> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

     //   if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) row.findViewById(R.id.name);
            holder.statementImage = (ImageView) row.findViewById(R.id.statementImage);
            holder.redLine = (ImageView) row.findViewById(R.id.redLine);
            holder.description = (TextView) row.findViewById(R.id.billDescription);
            holder.offerText=(TextView) row.findViewById(R.id.txtOfferText);
            row.setTag(holder);
       // } else {
            holder = (ViewHolder) row.getTag();
        //}
        Offer item = data.get(position);
        holder.name.setText(item.getName());
        holder.description.setText("Valid through " + item.getExpiresOnDisplayDate());
        holder.offerText.setText(item.getOfferText());
        if(item.isRead()){
            holder.redLine.setVisibility(View.INVISIBLE);
        }
        holder.offerText.setText(item.getOfferText());

        //holder.image.setImageBitmap(item.getImage());
        File imgFile = new File(item.getDisplayImagePathSmallLocal());
        if(imgFile.exists()){
            Log.w("StatementAdapter", "StatementAdapter setting image :" + imgFile.getAbsolutePath());
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.statementImage.setImageBitmap(myBitmap);
        }
        return row;
    }

    static class ViewHolder {
        TextView name;
        ImageView statementImage;
        ImageView redLine;
        TextView description;
        TextView offerText;
    }
}
