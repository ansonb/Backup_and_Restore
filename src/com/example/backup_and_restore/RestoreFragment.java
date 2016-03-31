package com.example.backup_and_restore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RestoreFragment extends Fragment{
	
	String vCardPath;
	String msgBackupPath;
	
	public TextView contacts;
	public TextView messages;
	public TextView drive;	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.restore_2, container, false);
		
		contacts = (TextView) rootView.findViewById(R.id.contact_backup_image);
		messages = (TextView) rootView.findViewById(R.id.messages_backup_image);
		drive = (TextView) rootView.findViewById(R.id.drive_backup_image);
		
		contacts.setHeight(MainActivity.height);
		contacts.setWidth(MainActivity.width);
		
		messages.setHeight(MainActivity.height);
		messages.setWidth(MainActivity.width);
		
		drive.setHeight(MainActivity.height);
		drive.setWidth(MainActivity.width);	
				
		return rootView;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("onActicityResult", "yes called");
		switch(requestCode){
		  //if requestCode is 1 restore contacts
		  case 1:
		     if(resultCode==Activity.RESULT_OK){
		        String vCardPath = data.getData().getPath();
		        Log.d("vCardPath", vCardPath);
		        
				restoreContactsFromPath(vCardPath);  		        
		     }
		   break;
		   
		  case 2:
			  //if resultCode is 2 restore messages
			  if(resultCode==Activity.RESULT_OK){
				  final String msgPath = data.getData().getPath();
				  Log.d("msgPath", msgPath);
				  
				  restoreMessagesFromPath(msgPath);	  
			  }
			  break;
		   
		  }
	}
	
	private ArrayList<ContentValues> parseXML(String path){
		Log.d("Test", "Inside parseXML");
		
		ArrayList<ContentValues> msgCvArray = new ArrayList<ContentValues>();
		ContentValues cv;
		  XmlPullParserFactory factoryObject;
		  XmlPullParser mParser;
		  try {
		      FileInputStream mFileInputStream = new FileInputStream(path);
		      
			  factoryObject = XmlPullParserFactory.newInstance();
			  mParser = factoryObject.newPullParser();
			
			  mParser.setInput(mFileInputStream, null);
			  
			  
			  int event = mParser.getEventType();

			  while(event!=XmlPullParser.END_DOCUMENT){
				  cv = new ContentValues();
				  
				  String name = mParser.getName();
				  switch(event){
				  case XmlPullParser.END_TAG:
					  if(name.equals("sms")){
						  for(int i=0;i<mParser.getAttributeCount();i++){
							  if(mParser.getAttributeName(i).equals("address")
									  ||mParser.getAttributeName(i).equals("body")
									  ||mParser.getAttributeName(i).equals("type")
									  ||mParser.getAttributeName(i).equals("date")
									  ||mParser.getAttributeName(i).equals("read"))
					          cv.put(mParser.getAttributeName(i), mParser.getAttributeValue(i));
						  }
						  msgCvArray.add(cv);
					  }
				  default: 
					  break;
				  }
				  
				  event = mParser.next();
			  }
			
		  } catch (XmlPullParserException e) {
			  e.printStackTrace();
		  } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		  
		  return msgCvArray;
	}
	
	private void restoreMessages(ArrayList<ContentValues> msgCvArray){
		 final String SENT_SMS_CONTENT_PROVIDER_URI_OLDER_API_19 = "content://sms/";

		 ArrayList<ContentValues> messagesToRestore = new ArrayList<ContentValues>();
		 
		 MainActivity.mProgressDialog.setMax(msgCvArray.size());
		 MainActivity.mProgressDialog.setProgress(0);
		 
		 for(int i=0; i<msgCvArray.size(); i++){
			 String selection = "address=? AND body=? AND date=?";
			 String[] selectionArgs = {msgCvArray.get(i).getAsString("address")
					 ,msgCvArray.get(i).getAsString("body")
					 ,msgCvArray.get(i).getAsString("date")};
			 
			 Cursor cr = MainActivity.context.getContentResolver().query(Uri.parse(SENT_SMS_CONTENT_PROVIDER_URI_OLDER_API_19)
					 , null, selection, selectionArgs, null); 
			 
			 
			 if(cr.getCount()==0){
				 Log.d(msgCvArray.get(i).getAsString("address"), msgCvArray.get(i).getAsString("body"));
	   			 messagesToRestore.add(msgCvArray.get(i));     				 
			 }
			 
			 if(cr!=null){
				 cr.close();
			 }
			 
			 MainActivity.mProgressDialog.setProgress(i);
				   
		 }  		
		 
		 for(int i=0; i<messagesToRestore.size(); i++){
			 MainActivity.context.getContentResolver().insert(Uri.parse(SENT_SMS_CONTENT_PROVIDER_URI_OLDER_API_19)
  					  , messagesToRestore.get(i)); 
		 }
	}
	
	public void restoreContacts(){
		//Select the file for restoring contacts
		//Intent getFilePath = new Intent(Intent.ACTION_GET_CONTENT);
		//getFilePath.setType("*/*");
		//startActivityForResult(Intent.createChooser(getFilePath, "Choose File to Restore from")
		//		, 1);		
		
		Intent intent = new Intent(getActivity(), FileExplorer.class);
		intent.putExtra("whoCalled", "RestoreFragment.restoreContacts");
		startActivity(intent);
	}
	
	public void restoreMessages(){
		//Intent getFilePath = new Intent(Intent.ACTION_GET_CONTENT);
		//getFilePath.setType("*/*");
		//startActivityForResult(Intent.createChooser(getFilePath, "Choose File to Restore fom")
		//		, 2);	
		
		Intent intent = new Intent(getActivity(), FileExplorer.class);
		intent.putExtra("whoCalled", "RestoreFragment.restoreMessages");
		startActivity(intent);
	}
	
	public void retrieveFromDrive(){
		Intent intent = MainActivity.context.getPackageManager()
				.getLaunchIntentForPackage("com.google.android.apps.docs");
		startActivity(intent);		
	}
	
	public  void restoreContactsFromPath(String path){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(path)), "text/x-vcard");
		getActivity().startActivity(intent); 
	}
	
	public void restoreMessagesFromPath(final String path){
		  
		  
		  MainActivity.mProgressDialog.setMessage("Restoring messages");
		  MainActivity.mProgressDialog.setTitle("Please Wait...");
		  MainActivity.mProgressDialog.setCancelable(false);
		  MainActivity.mProgressDialog.show();    				  
	  
		  new Thread(new Runnable(){
			  @Override
			  public void run(){
				  ArrayList<ContentValues> msgCvArray;
				  
				  msgCvArray = parseXML(path);
				  
				  restoreMessages(msgCvArray);  	
				  
				  MainActivity.mProgressDialog.dismiss();
				  
				  MainActivity.displayDialog("Done Restoring Messages!");
				  
			      Vibrator v = (Vibrator) MainActivity.context.getSystemService(Context.VIBRATOR_SERVICE);
			      // Vibrate for 500 milliseconds
			      v.vibrate(500);		    				  
			  }
		  }).start();		
	}
	
}    
