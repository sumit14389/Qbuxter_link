package com.service.util;

/**
 * @author Sumit Kumar Maji
 */

import java.io.ByteArrayInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Util {
	
	Activity mContext;
	public Util(Context context)
	{
		this.mContext = (Activity) context;
	}
	
	
	/**
	 * checks if internet connection is possible. checks radio signal presence
	 * and airplane mode.
	 * 
	 * @return true if device is connect able.
	 */

	public boolean isConnectionPossible() {

		ConnectivityManager cm = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return (netInfo != null && netInfo.isConnected());
		// if (netInfo != null && netInfo.isConnected()) {
		// return true;
		// }
		// else{
		// return false;
		// }
	}
//	public void callSound(){
//		MediaPlayer mPlayer = MediaPlayer.create(null, R.raw.call);
//
//        try {
//            mPlayer.prepare();
//        } catch (IllegalStateException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        mPlayer.start();
//	}
	public void showDialog(String msg)

	{
		new AlertDialog.Builder(mContext)
		.setMessage(msg)
		.setCancelable(false)
		.setPositiveButton("OK", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		}).show();
			
		
	}
	public void showDialog(int msg)

	{
		new AlertDialog.Builder(mContext)
		.setMessage(msg)
		.setCancelable(false)
		.setPositiveButton("OK", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		}).show();
			
		
	}
	
	public static Document getDocument(byte []data)

	{
		try{
	
				ByteArrayInputStream bais = new ByteArrayInputStream(data);
	
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	
				DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	
				docBuilder.isValidating(); 
	
				return docBuilder.parse(bais); 
	
			}
	
		catch(Exception ex)	
		{	
			return null;
	
		}

	}
	
	public boolean isEmailValid(String email) 
	{
	       boolean isValid = false;

	       String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	       CharSequence inputStr = email;

	       Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	       Matcher matcher = pattern.matcher(inputStr);
	       if (matcher.matches()) {
	           isValid = true;
	       }
	       return isValid;
	   }
	

}
