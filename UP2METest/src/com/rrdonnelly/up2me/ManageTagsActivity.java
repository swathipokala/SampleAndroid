package com.rrdonnelly.up2me;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rrdonnelly.up2me.dao.Tag;
import com.rrdonnelly.up2me.dao.TagDAO;
import com.rrdonnelly.up2me.services.TagService;
import com.rrdonnelly.up2me.util.ControlApplication;
import com.rrdonnelly.up2me.util.tags.AsyncTagSyncToBackend;

public class ManageTagsActivity extends Activity {

	public String userID = null;
    public String userName=null;
    public String userToken=null;
    public String salt=null;
    public long loginUserId = 0l;

	private Activity currentActivity = null;
	private List<Tag> allTagsList = null; 
	private EditText[] tags = new EditText[9];
	private ImageView[] deleteImageViews = new ImageView[9];
	LinearLayout tagslayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_manage_tags);
		tagslayout = (LinearLayout) findViewById(R.id.tagslayout);
		
		userToken=getIntent().getStringExtra("usertoken");
	    userName=getIntent().getStringExtra("userName");
	    salt=getIntent().getStringExtra("salt");
	    loginUserId=getIntent().getLongExtra("loginUserId", -1);
		
				
		
			
		currentActivity = this;
		
		List<Tag> tagsListFromDB = new TagDAO().getAllTags(currentActivity, loginUserId);
    	
    	displayTags(tagsListFromDB);

	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_tags, menu);
		return true;
	}

	
	public void displayTags(List<Tag> tagsListFromDB){
		int i = 0;
		
		
		for(Tag tag : tagsListFromDB){
			
			 final LayoutInflater  inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 LinearLayout lv= (LinearLayout) inflater.inflate(R.layout.tags_layout, null);
			 TextView tagColorView= (TextView)lv.findViewById(R.id.tagColorBox);
			 ImageView deleteTag= (ImageView)lv.findViewById(R.id.deleteTag);
			 EditText tagText= (EditText)lv.findViewById(R.id.tagText);
			 
			 if(tag.getColorCode().equalsIgnoreCase("#EE1F25")){
				 tagText.setKeyListener(null);	 
				 //deleteTag.setEnabled(false);
				 deleteTag.setVisibility(View.INVISIBLE);
			 }
			 
			 Layout tags = new Layout();
			 tags.setLinear(lv);
			 tags.setId(tag.getTagId());
			 deleteTag.setTag(tags);
			 tagText.setText(tag.getName());
			 tagText.setTag(tag.getColorCode());
			 tagColorView.setBackgroundColor(Color.parseColor(tag.getColorCode()));
			 tagslayout.addView(lv);
		}
	}
	
	
	public void saveTags(View view){
		//List<Tag> tagsListToUpdate = new ArrayList<Tag>();
		
		TagDAO tagDAO = new TagDAO();
	
		List<String> tagNamesList = new ArrayList<String>();
		List<Tag> tagsList = new ArrayList<Tag>();
		
		boolean tagWithEmptyName = false;
		
		for(int i=0;i<tagslayout.getChildCount();i++){
			final View mChild = tagslayout.getChildAt(i);
			if(mChild instanceof LinearLayout) {
			  TextView tagColorView= (TextView)mChild.findViewById(R.id.tagColorBox);
			  ImageView deleteTag= (ImageView)mChild.findViewById(R.id.deleteTag);
			  EditText tagText= (EditText)mChild.findViewById(R.id.tagText);
			  Layout tags = (Layout) deleteTag.getTag();
			  String tagName = tagText.getText().toString();
			  String tagColorCode = tagText.getTag().toString();
			  
				Tag tag = new Tag();
				tag.setName(tagName);
				tag.setColorCode(tagColorCode);
				tag.setUserId(loginUserId);
				//tagDAO.addTag(tag, currentActivity);
				tagsList.add(tag);
			  
			  
			  tagNamesList.add(tagName);
			  if(tagName.trim().length()==0){
				  tagWithEmptyName = true;
				  break;
			  }
			  
			}
			//duplicateTagError();
		}

		
		if(tagWithEmptyName){
			emptyTagError();
		} else{
		
			boolean duplicateExists = false;
			for (int i = 0; i < tagNamesList.size(); i++) {
			  for (int j = i+1; j < tagNamesList.size(); j++) {
			    if(tagNamesList.get(i).equals(tagNamesList.get(j))){
			    	duplicateExists = true;
			    	break;
			    }
			  }
			}
			
			if(duplicateExists){
				duplicateTagError();
			} else {
				for(Tag tag:tagsList){
				  tagDAO.updateOrInsertTag(tag.getName(), tag.getColorCode(),
							currentActivity, loginUserId);
				}
				Toast.makeText(getApplicationContext(), "Saved tags successfully", Toast.LENGTH_SHORT).show();
				new AsyncTagSyncToBackend(currentActivity,true,true,true)
				.execute(String.valueOf(loginUserId));
			}
		}
		
		//Synching to backend db.
		
	
		
		/*try {
			TagService.syncTagsToBackend(currentActivity, loginUserId);
			TagService.syncUserOfferTagsToBackend(currentActivity, loginUserId);
			TagService.syncUserStatementTagsToBackend(currentActivity, loginUserId);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}
	
	public void deleteTag(final View view){
		
    	AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(this);
    	alertDialog2.setTitle("UP2ME");
    	alertDialog2.setMessage("Do you really want to delete this tag?");
    	alertDialog2.setPositiveButton("Ok",
    	        new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog, int which) {
    	        		ImageView imageView = (ImageView) view;
    	        		Layout o = (Layout)imageView.getTag();
    	        		
    	        		//String sequenceId = o.toString();
    	        		
    	        		TagDAO tagDAO = new TagDAO();
    	        		//tagDAO.deleteTag(sequenceId, currentActivity);
    	        		tagDAO.deleteOffersAndStatementTags(o.getId(), currentActivity,loginUserId);
    	        		tagDAO.deleteTag(currentActivity, o.getId(), loginUserId);
    	        		tagslayout.removeView(o.getLinear());
//    	        		long tagId = tagDAO.getTagIdWithSequenceId(sequenceId, currentActivity, loginUserId);
//    	        		new TagDAO().deleteOffersAndStatementTags(tagId, currentActivity,loginUserId);
//    	        		
//    	        		List<Tag> tagsListFromDB = new TagDAO().getAllTags(currentActivity,loginUserId);
//    	            	
//    	            	displayTags(tagsListFromDB);
    	            	Toast.makeText(getApplicationContext(), "Tag is deleted", Toast.LENGTH_SHORT).show();
    					new AsyncTagSyncToBackend(currentActivity,true,true,true)
    					.execute(String.valueOf(loginUserId));
    	            }
    	});
		
    	alertDialog2.setNegativeButton("Cancel",
    	        new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog, int which) {
    	              
    	                dialog.cancel();
    	            }
    	        });

    	alertDialog2.show();
	}
	
	
	public void duplicateTagError(){
		
    	AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(this);
    	alertDialog2.setTitle("UP2ME");
    	alertDialog2.setMessage("Duplicate tag name should not exists.");
    	alertDialog2.setPositiveButton("Ok",
    	        new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog, int which) {
    	        		//Toast.makeText(getApplicationContext(), "Tag is deleted", Toast.LENGTH_SHORT).show();
    	            }
    	});
    	alertDialog2.show();
	}
	

	public void emptyTagError(){
		
    	AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(this);
    	alertDialog2.setTitle("UP2ME");
    	alertDialog2.setMessage("Empty tag name is not allowed.");
    	alertDialog2.setPositiveButton("Ok",
    	        new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog, int which) {
    	        		//Toast.makeText(getApplicationContext(), "Tag is deleted", Toast.LENGTH_SHORT).show();
    	            }
    	});
    	alertDialog2.show();
	}

	
	
	
	public void showSettingsPage(View view){
		finish();
//	    Intent settingsActivityIntent = new Intent(view.getContext(), SettingsActivity.class);
//	    
//	    settingsActivityIntent.putExtra("usertoken", userToken);
//        settingsActivityIntent.putExtra("userName",userName);
//        settingsActivityIntent.putExtra("salt",salt);
//        settingsActivityIntent.putExtra("loginUserId",loginUserId);
//
//	    startActivityForResult(settingsActivityIntent, 0);
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
	
	    class Layout{
	    	long id;
	    	public long getId() {
				return id;
			}
			public void setId(long id) {
				this.id = id;
			}
			public LinearLayout getLinear() {
				return linear;
			}
			public void setLinear(LinearLayout linear) {
				this.linear = linear;
			}
			LinearLayout linear;
	    }
	    
	    @Override
	    protected void onDestroy() {
	    	// TODO Auto-generated method stub
	    	super.onDestroy();
	    	System.gc();
	    }

	

}
