package com.rrdonnelly.up2me.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Debug;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.json.Offer;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;


/**
 * Created by rahul.k on 11/28/13.
 */

public class ItemListBaseAdapter extends BaseAdapter {
    private List<Offer> itemDetailsArrayList;
    private LayoutInflater l_Inflater;

    public ItemListBaseAdapter(Context context, List<Offer> results) {
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
        //logHeap("Item List Base Adapter - getView method starting ");
        
        ViewHolder holder;
        if (convertView == null) {
            convertView = l_Inflater.inflate(R.layout.item_details_view, null);
            holder = new ViewHolder();
            holder.txt_itemName = (TextView) convertView.findViewById(R.id.name);
            holder.txt_itemDesc = (TextView) convertView.findViewById(R.id.description);
            holder.itemImage = (ImageView) convertView.findViewById(R.id.photo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Offer offer = (Offer) itemDetailsArrayList.get(position);
        holder.txt_itemName.setText(offer.getName());
        //holder.txt_itemDesc.setText(offer.getDescription());
        holder.txt_itemDesc.setText(offer.getOfferText());
        //String imageURL = offer.getImageURL();
        String imageURL = offer.getDisplayImagePathSmallLocal();
        File imgFile = new File(imageURL);
        if(imgFile.exists()){
            //Log.w("ItemListBaseAdapter", "setting image :"+imgFile.getAbsolutePath());
            //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.itemImage.setImageURI(Uri.fromFile(imgFile));	
            //holder.itemImage.setImageBitmap(myBitmap);

        }
      //  logHeap("Item List Base Adapter - getView method ending ");
        return convertView;
    }

    class ViewHolder {
        TextView txt_itemName;
        TextView txt_itemDesc;
        ImageView itemImage;
    }
    
    public static void logHeap(String logMethodLocation) {
        Double allocated = Double.valueOf(Debug.getNativeHeapAllocatedSize())/Double.valueOf((1048576));
        Double available = Double.valueOf(Debug.getNativeHeapSize())/1048576.0;
        Double free = Double.valueOf(Debug.getNativeHeapFreeSize())/1048576.0;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

       /* TestFlight.passCheckpoint(logMethodLocation + " debug.heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free)");
        Log.w("Memory Sizes: ",logMethodLocation + " debug.heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free)");
        TestFlight.passCheckpoint(logMethodLocation + " debug.memory: allocated: " + df.format(Double.valueOf(Runtime.getRuntime().totalMemory()/1048576)) + "MB of " + df.format(Double.valueOf(Runtime.getRuntime().maxMemory()/1048576))+ "MB (" + df.format(Double.valueOf(Runtime.getRuntime().freeMemory()/1048576)) +"MB free)");
        Log.w("Memory Sizes: ",logMethodLocation + " debug.memory: allocated: " + df.format(Double.valueOf(Runtime.getRuntime().totalMemory()/1048576)) + "MB of " + df.format(Double.valueOf(Runtime.getRuntime().maxMemory()/1048576))+ "MB (" + df.format(Double.valueOf(Runtime.getRuntime().freeMemory()/1048576)) +"MB free)");*/
    }
}