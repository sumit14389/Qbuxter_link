package com.service.activity;

/**
 * @author Sumit Kumar Maji
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.service.VO.CancelPairVO;
import com.service.constant.Constant;
import com.service.network.HttpConnection;
import com.service.parser.CancelPairParser;
import com.service.util.CommonUtilities;
import com.service.util.CustomProgress;
import com.service.util.RegisterActivities;
import com.service.util.Util;

public class PairDeviceActivity extends Activity {

	private Typeface tf1,tf2;
	private TextView tvPairNote1,tvPairNote2,tvPairNote3,tvPairCode;
	private Bundle bundle;

	private String regId,service_type,service_id,pair_code;
	private Util utils;
	private Handler cancelPairHandler;
	private CancelPairParser cancelPairParser;
	private CancelPairVO cancelPairVO;
	private CustomProgress progress;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pair_device);
		RegisterActivities.registerActivity(this);
		utils=new Util(this);
		initView();
		registerDevice();

		cancelPairHandler = new Handler()
		{
			public void handleMessage(Message message)
			{
				switch(message.what)                  
				{
				case HttpConnection.DID_START:
					progress.show();
					break;
				case HttpConnection.DID_SUCCEED:
					if(progress!=null && progress.isShowing())
						progress.dismiss();

					String xmlData = (String)message.obj;
					cancelPairParser = new CancelPairParser(xmlData);
					Constant.cancelPairArray=cancelPairParser.parse();
					if(Constant.cancelPairArray!=null && Constant.cancelPairArray.size()>0)
					{
						cancelPairVO = (CancelPairVO)Constant.cancelPairArray.get(0);
						String sucess=cancelPairVO.getIsCancelSucess();
						if(sucess.equalsIgnoreCase("Cancel Successful"))
						{
							finish();
							Intent intentpair = new Intent(PairDeviceActivity.this,LandingActivity.class);
							startActivity(intentpair);
						}
						else {
							Toast.makeText(PairDeviceActivity.this, "Unable to cancel", Toast.LENGTH_SHORT).show();
						}
					}
					else {
						Toast.makeText(PairDeviceActivity.this, "Unable to cancel", Toast.LENGTH_SHORT).show();
					}

					break;

				case HttpConnection.DID_UNSUCCESS:
					progress.dismiss();
					//utils.showDialog(SplashActivity.this, R.string.Authentication);
					break;

				case HttpConnection.DID_ERROR:
					progress.dismiss();
					utils.showDialog("Connection Not Possible");
					break;
				}
			}
		};



	}


	public void registerDevice() {
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));
		regId = GCMRegistrar.getRegistrationId(this);

		if (regId.equals("")) 
		{		
			GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
		}
		regId = GCMRegistrar.getRegistrationId(this);
		Constant.REG_ID=regId;
	}


	private void initView() {

		Intent iin= getIntent();
		Bundle bundle = iin.getExtras();
		if(bundle!=null && !bundle.isEmpty())
		{
			service_type = (String)bundle.get("service_type");
			service_id=(String)bundle.get("service_id");
			pair_code=(String)bundle.get("pair_code");
		}

		tf1 = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueLTPro-ThCn.otf");
		tf2 = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueLTPro-BlkCn.otf");
		tvPairNote1=(TextView)findViewById(R.id.tvPairTicketTest1);
		tvPairNote2=(TextView)findViewById(R.id.tvPairTicketTest2);
		tvPairNote3=(TextView)findViewById(R.id.tvPairTicketTest3);
		tvPairCode=(TextView)findViewById(R.id.tvPairCode);

		tvPairNote1.setTypeface(tf1);
		tvPairNote2.setTypeface(tf1);
		tvPairNote3.setTypeface(tf1);
		tvPairCode.setTypeface(tf2);
		//		ivPairDevice=(ImageView)findViewById(R.id.ivPairDevice);

		tvPairCode.setText(pair_code);

	}


	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		private String pushMessage;

		@Override
		public void onReceive(Context context, Intent intent) {

			pushMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
			pushMessage.trim();
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			/**
			 * Take appropriate action on this message
			 * depending upon your app requirement
			 * For now i am just displaying it on the screen
			 * */
			if(pushMessage.equalsIgnoreCase("Connect successfully"))
			{
				pushMessage="Device Paired";
				new AlertDialog.Builder(PairDeviceActivity.this)
				.setMessage(pushMessage)			
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						calActivity();
					}


				}).show();

			}
			else if(pushMessage.equalsIgnoreCase("Connection Cancelled"))
			{
				new AlertDialog.Builder(PairDeviceActivity.this)
				.setMessage(pushMessage)			
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						finish();
						Intent intentland = new Intent(PairDeviceActivity.this,LandingActivity.class);
						startActivity(intentland);
					}
				}).show();
				pushMessage="Unauthorized pairing";
			}



			WakeLocker.release();
		}
	};

	private void calActivity() 
	{
		if(service_type.equalsIgnoreCase("desk_monitor"))
		{
			finish();
			Intent intentDeskMonitor = new Intent(PairDeviceActivity.this,DeskMonitorActivity.class);
			startActivity(intentDeskMonitor);
		}
		else if (service_type.equalsIgnoreCase("overview_monitor"))
		{
			finish();
			Intent intentOverViewMoniter = new Intent(PairDeviceActivity.this,OverViewMoniterActivity.class);
			intentOverViewMoniter.putExtra("service_id", service_id);
			startActivity(intentOverViewMoniter);
		}
		else if (service_type.equalsIgnoreCase("name_ticket"))
		{
			Intent intentNameTicket = new Intent(PairDeviceActivity.this,NameTicketActivity.class);
			intentNameTicket.putExtra("service_id", service_id);
			startActivity(intentNameTicket);
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if(keyCode == event.KEYCODE_BACK)
		{
			new AlertDialog.Builder(PairDeviceActivity.this)
			.setMessage("Do you want to Cancel pairing?")			
			.setPositiveButton("YES", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					callCancelHandler();

				}

			})
			.setNegativeButton("NO", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {


				}
			}).show();
		}

		return super.onKeyDown(keyCode, event);
	}

	private void callCancelHandler() {

		if(!utils.isConnectionPossible())
		{
			new AlertDialog.Builder(PairDeviceActivity.this)
			.setMessage("Please check your internet connection...")			
			.setTitle("No Internet Connection")
			.setPositiveButton("Try Again", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					callCancelHandler();
				}
			})

			.setNegativeButton("EXIT", new DialogInterface.OnClickListener() 
			{
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			}).show();
		} 
		else 
		{
			List<NameValuePair> nameValuePair = callCancelPair();
			HttpConnection httpConnection = new HttpConnection(cancelPairHandler);
			httpConnection.createPost(1, Constant.CONNECTION_URL, nameValuePair);
			progress = new CustomProgress(PairDeviceActivity.this,"Loading data.....");
		}

	}


	private List<NameValuePair> callCancelPair()
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("action", "cancelDevice"));
		nameValuePairs.add(new BasicNameValuePair("device_id",service_id));

		return nameValuePairs;
	}
	
	@Override
	protected void onDestroy() {

		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

}
