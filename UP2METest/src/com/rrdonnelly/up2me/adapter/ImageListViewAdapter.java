package com.rrdonnelly.up2me.adapter;

import java.io.File;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.valueobjects.Offer;


/**
 * Created by Durga Prasad G on 12/06/13.
 */

public class ImageListViewAdapter extends BaseAdapter {
    private ArrayList<Offer> itemDetailsArrayList;
    private LayoutInflater l_Inflater;

    public ImageListViewAdapter(Context context, ArrayList<Offer> results) {
        this.itemDetailsArrayList = results;
        l_Inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return itemDetailsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemDetailsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.w("ItemListBaseAdapter", "In getView of ItemListBaseAdapter");
        ViewHolder holder;
        if (convertView == null) {
            convertView = l_Inflater.inflate(R.layout.image_list_view, null);
            holder = new ViewHolder();
            holder.itemImage = (ImageView) convertView.findViewById(R.id.offerThumbNails);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Offer offer = (Offer) itemDetailsArrayList.get(position);
        String imageURL = offer.getTeaserImagePath();
        File imgFile = new File(imageURL);
        if(imgFile.exists()){
            //Log.w("ItemListBaseAdapter", "setting image :"+imgFile.getAbsolutePath());
            //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            //holder.itemImage.setImageBitmap(myBitmap);
            holder.itemImage.setImageURI(Uri.fromFile(imgFile));

        }

        return convertView;
    }

    class ViewHolder {
        ImageView itemImage;
    }
}