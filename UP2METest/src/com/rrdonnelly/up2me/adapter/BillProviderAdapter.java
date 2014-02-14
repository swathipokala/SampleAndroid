package com.rrdonnelly.up2me.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.rrdonnelly.up2me.R;
import com.rrdonnelly.up2me.json.DocumentProviders;
import com.rrdonnelly.up2me.util.DownloadImages;

/**
 * Created by rahul.k on 12/5/13.
 */
public class BillProviderAdapter extends ArrayAdapter<DocumentProviders> {

    private Context context;
    private int layoutResourceId;
    private List<DocumentProviders> data = new ArrayList<DocumentProviders>();
    Activity currentActivity;
	private long loginUserId = 0l;
	public String userName=null;
	public String userToken=null;
	public String salt=null;
	/*String[] allStatementsList;*/
 
    public BillProviderAdapter(Context context, int layoutResourceId,List<DocumentProviders> data,Activity currentActivity, Long loginUserId) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.currentActivity=currentActivity;
        this.loginUserId = loginUserId;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.providerImage = (ImageView) row.findViewById(R.id.providerImage);
            holder.tvProviderChecked = (TextView) row.findViewById(R.id.providerChecked);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        DocumentProviders item = data.get(position);
        if(item.isItemSelected()){
        	holder.tvProviderChecked.setVisibility(View.VISIBLE);
        }else{
        	holder.tvProviderChecked.setVisibility(View.INVISIBLE);
        }
       
        String displayImagePathSmall = item.getImageUrlSmallStr();
		String displayImagePathSmallfileName = displayImagePathSmall.substring(displayImagePathSmall.lastIndexOf("/") + 1);
		if(displayImagePathSmallfileName != null && displayImagePathSmallfileName != "") {
        	File imgFile = new File(currentActivity.getBaseContext().getFilesDir(), displayImagePathSmallfileName);
            if(!imgFile.exists()){
//            	new DownloadImages(currentActivity).execute(displayImagePathSmall, holder.providerImage);
            	holder.providerImage.setImageURI(null);
            } else {
                holder.providerImage.setImageURI(Uri.fromFile(imgFile));
            }
		}
        
        
        return row;
    }
    
    static class ViewHolder {
    	TextView tvProviderChecked;
    	ImageView providerImage;
    }
}
