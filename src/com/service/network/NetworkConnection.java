package com.service.network;

/**
 * @author Sumit Kumar Maji
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class NetworkConnection {
	
	private String urlString;
	private boolean hasErrorOccured = false;
	private String errorString;
	private int errorCode = -1;
	
	public NetworkConnection(String url) {		
		this.urlString = url;		
	}
	
	public boolean hasErrorOccured(){
		return this.hasErrorOccured;
	}
	
	public String getErrorMessage(){
		return (this.errorString != null) ? this.errorString : "";
	}
	
	public String getErrorCode(){
		return (this.errorCode != -1) ? String.valueOf(this.errorCode) : "";
	}
	
	public InputStream getInputStream(){
		
		InputStream feedStream = null;

		  if (urlString != null && !(urlString.trim()).equals("")){

			  try {				  
				  URL url = new URL(urlString);
				  HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
				  int responseCode = urlConn.getResponseCode();
				  if(responseCode == HttpURLConnection.HTTP_OK){
					  feedStream = urlConn.getInputStream();	
				  }
				  else{
					  hasErrorOccured = true;
					  errorCode = responseCode;
				  }		  
				  return feedStream;
				  
			  } catch (IOException e) {
				  e.printStackTrace();
				  hasErrorOccured = true;
				  errorString = e.getMessage();
			  }
		  }
		  
		  return feedStream;
	}
	/**
	 * Post Data to the specified URL using the POST method.
	 * @param nameValuePairs : the data to be posted to the server in the form of name value pair.
	 * @return : The server response against the data posted. 
	 */
	public String postDataToServer(List<NameValuePair> nameValuePairs){
		
		String result = null;
		HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = null;
	   // String url = this.urlString.replace(" ", "%20");
	    httppost = new HttpPost(this.urlString.replace(" ", ""));
	    
	    /*
		try {
			//httppost = new HttpPost(URLEncoder.encode(urlString, "UTF-8"));
			httppost = new HttpPost(this.urlString.replace(" ", "%20"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
	    System.out.println(nameValuePairs);

	    try {
	        // Adding name value pair data	        
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        System.out.println("Posting Code::::"+response.getStatusLine().getStatusCode());
	        
	        BufferedReader responseReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer();
	        
	        String line = "";
			while ((line = responseReader.readLine()) != null) {
				sb.append(line);
				
			}	        
            result = sb.toString();
            System.out.println("Posting response is:::"+result);
	        
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	        hasErrorOccured = true;
	        errorString = e.getMessage();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    	hasErrorOccured = true;
	    	errorString = e.getMessage();
	    }
		
		return result;
	}
	/**
	 * 
	 * @param feedStream : InputStream from the the data needs to be fetched.
	 * @return : the response, the server sends.
	 */
	public String postDataToServer(InputStream feedStream){		
		
		StringBuffer strBuff = new StringBuffer();
		if(feedStream != null){
			int len = 0;
			
			byte[] data1 = new byte[64];
			try {
				while ( -1 != (len = feedStream.read(data1)) )
					strBuff.append(new String(data1, 0, len));
				
			} catch (IOException e) {
				e.printStackTrace();
				hasErrorOccured = true;
				errorString = e.getMessage();
			}
		}
		else{
			Log.e("Log String", "InputStream is NULL");
		}
		return strBuff.toString();
	}
}
