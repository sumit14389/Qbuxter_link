package com.service.util;

/**
 * @author Sumit Kumar Maji
 */

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {
	
	// give your server registration url here
  public  static final String SERVER_URL = "http://174.136.1.35/dev/Qbuxter/call.php"; //"http://10.0.2.2/gcm_server_php/register.php"; 

    // Google project id
  public static final String SENDER_ID = "126123057453";
  
  public static final String TAG = "Qbuxter Link";
  public  static final String DISPLAY_MESSAGE_ACTION = "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";
  public  static final String EXTRA_MESSAGE = "msg";
  public  static final String TICKET_NO = "ticket";
  public  static void displayMessage(Context context, String message)
  {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
   }
}
