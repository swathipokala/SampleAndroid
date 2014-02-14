package com.rrdonnelly.up2me;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Team Android
 */
public class CloudAdapter extends ArrayAdapter<CloudData> {
	@Override
	public int getCount() {
		return cloudList.size();
	}

	Context context;
	private ArrayList<CloudData> cloudList;

	/**
	 * Constructor to initialize the adapter object
	 * 
	 * @param context
	 * @param textViewResourceId
	 * @param countryList
	 */
	public CloudAdapter(Context context, int textViewResourceId, ArrayList<CloudData> list) {
		super(context, textViewResourceId, list);
		this.cloudList = new ArrayList<CloudData>();
		this.cloudList = list;
		this.context = context;
	}

	/**
	 * Holder to refer the views
	 * 
	 */
	private class ViewHolder {
		ImageView ivCloudProviderImage;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/***
	 * customizing the view by overriding getview
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.cloud_grid_row, null);

			holder = new ViewHolder();
			holder.ivCloudProviderImage = (ImageView) convertView.findViewById(R.id.ivCloudProviderImage);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		CloudData cloud = cloudList.get(position);
		String imageURL = cloud.getStrImagePath();
		if(imageURL.indexOf("/")!=-1){
			imageURL = imageURL.substring(imageURL.lastIndexOf("/") + 1);
 	   }
        File imgFile = new File(context.getFilesDir(), imageURL);
        if(imgFile.exists()){
        	holder.ivCloudProviderImage.setImageURI(Uri.fromFile(imgFile));
        } else {
        	new ImageViewDownloader(holder.ivCloudProviderImage,context).execute(cloud.getStrImagePath(),cloud.strImageText);
        }
        
        int i=0;
        boolean ret = false;
        if(cloud.getSelectedCloud().size() > 0){
            for (i = 0; i < cloud.getSelectedCloud().size(); i++) {
    			if (cloud.getSelectedCloud().get(i) == cloud.getClientId()) {
    				ret = true;
    				break;
    			}
            }
        }
        
        if(ret) {
       	 convertView.setBackgroundResource(R.drawable.cloud_selected_background);
        } else {
       	 convertView.setBackgroundResource(R.color.white);
        }
        
		return convertView;
	}
	
}

class CloudData{
	String strImagePath, strImageText;
	long clientId;
	boolean selected;
	List<Integer> selectedCloud; 
	CloudData(long clientId,String ImagePath,String ImageText, boolean select, List<Integer> selectedCloudItems)
	{
		this.clientId = clientId;
		this.strImagePath = ImagePath;
		this.strImageText = ImageText;
		this.selected = select;
		this.selectedCloud = selectedCloudItems;
	}
	public List<Integer> getSelectedCloud() {
		return selectedCloud;
	}
	public void setSelectedCloud(List<Integer> selectedCloud) {
		this.selectedCloud = selectedCloud;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public String getStrImagePath() {
		return strImagePath;
	}
	public void setStrImagePath(String strImagePath) {
		this.strImagePath = strImagePath;
	}
	public String getStrImageText() {
		return strImageText;
	}
	public void setStrImageText(String strImageText) {
		this.strImageText = strImageText;
	}
	public long getClientId() {
		return clientId;
	}
	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

}

