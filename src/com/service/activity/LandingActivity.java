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
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.service.VO.PairCodeVO;
import com.service.constant.Constant;
import com.service.network.HttpConnection;
import com.service.parser.PairCodeParser;
import com.service.util.CommonUtilities;
import com.service.util.CustomProgress;
import com.service.util.RegisterActivities;
import com.service.util.Util;

public class LandingActivity extends Activity implements OnClickListener {

	Button deskMonitor,overViewMonitor,nameTicket;
	Typeface tf;
	private TextView tvNote;
	private String regId;
	private Util utils;
	private Handler getPairDeviceHandler;
	private Dialog progress;
	private String service_type="";
	private PairCodeParser pairCodeParser;
	private PairCodeVO pairCodeVO;
	private String pair_code="";
	private String service_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_landing);
		RegisterActivities.registerActivity(this);
		utils=new Util(this);

		initview();

		registerDevice();
	}

	private void initview() {

		tf = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueLTPro-ThCn.otf");
		tvNote=(TextView)findViewById(R.id.tvNote);
		tvNote.setTypeface(tf);

		deskMonitor=(Button) findViewById(R.id.bDeskMonitor);
		deskMonitor.setOnClickListener(this);

		overViewMonitor=(Button) findViewById(R.id.bOverViewMonitor);
		overViewMonitor.setOnClickListener(this);

		nameTicket=(Button) findViewById(R.id.bNameTicket);
		nameTicket.setOnClickListener(this);


		getPairDeviceHandler = new Handler()
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
					pairCodeParser = new PairCodeParser(xmlData);
					Constant.codeArray=pairCodeParser.parse();
					if(Constant.codeArray!=null && Constant.codeArray.size()>0)
					{
						pairCodeVO = (PairCodeVO)Constant.codeArray.get(0);
						pair_code=pairCodeVO.getPairing_code();
						service_id=pairCodeVO.getService_id();
						System.out.println("value of pair code::::"+ pair_code);
						//						initview();
						if (pairCodeVO!=null) 
						{
							if (!pair_code.equalsIgnoreCase("null") && pair_code.length()>0) {
								finish();
								Intent intentpair = new Intent(LandingActivity.this,PairDeviceActivity.class);
								intentpair.putExtra("service_type", service_type);
								intentpair.putExtra("service_id", service_id);
								intentpair.putExtra("pair_code", pair_code);
								startActivity(intentpair);

							} 
							else
							{
								utils.showDialog("Unable to pair or already paired");

							}
						}
					}

					else 
					{
						Toast.makeText(LandingActivity.this, "No data found", Toast.LENGTH_SHORT).show();
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
		//		registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));
		regId = GCMRegistrar.getRegistrationId(this);

		if (regId.equals("")) 
		{		
			GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
		}
		regId = GCMRegistrar.getRegistrationId(this);
		Constant.REG_ID=regId;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.landing, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if(keyCode == event.KEYCODE_BACK)
		{
			new AlertDialog.Builder(LandingActivity.this)
			.setMessage("Do you want to exit?")			
			.setPositiveButton("YES", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					RegisterActivities.removeAllActivities();

				}
			})
			.setNegativeButton("NO", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

				}
			}).show();
		}

		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.bDeskMonitor:
			service_type="desk_monitor";
			callPairCodeHandler();
			break;

		case R.id.bOverViewMonitor:
			service_type="overview_monitor";
			callPairCodeHandler();
			break;

		case R.id.bNameTicket:
			service_type="name_ticket";
			callPairCodeHandler();
			break;
		}

	}

	private void callPairCodeHandler() 
	{
		if(!utils.isConnectionPossible())
		{
			new AlertDialog.Builder(LandingActivity.this)
			.setMessage("Please check your internet connection...")			
			.setTitle("No Internet Connection")
			.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					callPairCodeHandler();

				}
			})

			.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			}).show();
		} 
		else 
		{
			List<NameValuePair> nameValuePair = callPairCode();
			HttpConnection httpConnection = new HttpConnection(getPairDeviceHandler);
			httpConnection.createPost(1, Constant.CONNECTION_URL, nameValuePair);
			progress = new CustomProgress(LandingActivity.this,"Loading data.....");
		}
	}

	private List<NameValuePair> callPairCode() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("action", "connectDevice"));
		nameValuePairs.add(new BasicNameValuePair("device_service_type",service_type));
		nameValuePairs.add(new BasicNameValuePair("device_token", regId));
		nameValuePairs.add(new BasicNameValuePair("device_type","android"));
		return nameValuePairs;
	}

}
