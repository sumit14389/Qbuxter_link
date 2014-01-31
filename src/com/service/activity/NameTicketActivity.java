package com.service.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.service.VO.AddTicketVO;
import com.service.VO.NameTicketVO;
import com.service.constant.Constant;
import com.service.network.HttpConnection;
import com.service.parser.AddSucessParser;
import com.service.parser.NameTicketParser;
import com.service.util.CommonUtilities;
import com.service.util.CustomProgress;
import com.service.util.RegisterActivities;
import com.service.util.Util;

public class NameTicketActivity extends Activity implements OnClickListener{

	private Util utils;
	private String service_id;
	private Typeface tf,tfb;
	private TextView tvLocation,tvQueue,tvPeopleWaiting,tvEstimateServiceTime,tvTime,tvPleaseEnter;
	private Handler nameTicketGetQueueHandler;
	private boolean isTrue;
	private CustomProgress progress;
	private LoadTimertoContinousSendData loadTimerTosendData;
	private String regId;
	private NameTicketParser nameTicketParser;			
	private NameTicketVO nameTicketVO;
	private String location,queue;
	private Button enter;
	private EditText nameFeild;
	private Handler addTicket;
	private boolean isEnableFirst;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_name_ticket);

		prepareTexts();
		RegisterActivities.registerActivity(this);
		utils=new Util(this);
		registerDevice();

		Intent iin= getIntent();
		Bundle bundle = iin.getExtras();
		if(bundle!=null && !bundle.isEmpty())
		{			
			service_id=(String)bundle.get("service_id");
		}

		nameTicketGetQueueHandler = new Handler()
		{
			public void handleMessage(Message message)
			{
				switch(message.what)                  
				{
				case HttpConnection.DID_START:
					if(!isTrue){
						progress.show();
					}
					break;
				case HttpConnection.DID_SUCCEED:
					if(!isTrue) {
						if(progress!=null && progress.isShowing())
							progress.dismiss();
					}
					String xmlData = (String)message.obj;
					nameTicketParser = new NameTicketParser(xmlData);
					if (Constant.nameTicketArray!=null)
					{
						Constant.nameTicketArray.clear();
					}

					Constant.nameTicketArray=nameTicketParser.parse();
					if(Constant.nameTicketArray!=null && Constant.nameTicketArray.size()>0)
					{	
						nameTicketVO=(NameTicketVO)Constant.nameTicketArray.get(0);
						location=nameTicketVO.getLocation();
						queue=nameTicketVO.getQueueNo();
						initview();
					}

					break;

				case HttpConnection.DID_UNSUCCESS:
					if(!isTrue){
						progress.dismiss();
					}
					//utils.showDialog(SplashActivity.this, R.string.Authentication);
					break;

				case HttpConnection.DID_ERROR:
					if(!isTrue){
						progress.dismiss();
					}
					utils.showDialog("Connection Not Possible");
					break;
				}
			}
		};


		addTicket = new Handler()
		{
			private AddSucessParser addSucessParser;
			private AddTicketVO addTicketVO;

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
					addSucessParser = new AddSucessParser(xmlData);
					Constant.addTicketArray=addSucessParser.parse();
					if(Constant.addTicketArray!=null && Constant.addTicketArray.size()>0)
					{	
						addTicketVO=(AddTicketVO)Constant.addTicketArray.get(0);
						if(addTicketVO.getIsSucess().equalsIgnoreCase("Successful"));
						{
							nameFeild.setText("");
							CalQueueHandler(service_id);
							loadTimerTosendData.start();
							Toast.makeText(NameTicketActivity.this, "Ticket added :"+addTicketVO.getTicketId(), Toast.LENGTH_SHORT).show();
							initview();
						}
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


		if(Constant.nameTicketArray==null)
		{
			CalQueueHandler(service_id);
		}
	}


	private void registerDevice()
	{
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

	private void prepareTexts() 
	{
		enter=(Button)findViewById(R.id.bEnter);
		enter.setOnClickListener(this);

		nameFeild=(EditText)findViewById(R.id.edName);

		tf = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueLTPro-ThCn.otf");
		tfb = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueLTPro-BlkCn.otf");

		tvLocation=(TextView)findViewById(R.id.tvLocation);
		tvLocation.setTypeface(tfb);

		tvQueue=(TextView)findViewById(R.id.tvQueue);
		tvQueue.setTypeface(tfb);

		tvPeopleWaiting=(TextView)findViewById(R.id.tvPeopleWaiting);
		tvPeopleWaiting.setTypeface(tf);

		tvEstimateServiceTime=(TextView)findViewById(R.id.tvEstimateServiceTime);
		tvEstimateServiceTime.setTypeface(tf);

		tvTime=(TextView)findViewById(R.id.tvTime);
		tvTime.setTypeface(tfb);

		tvPleaseEnter=(TextView)findViewById(R.id.tvPleaseEnter);
		tvPleaseEnter.setTypeface(tf);
	}


	private void initview()
	{
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		String time=String.valueOf(today.hour)+":"+String.valueOf(today.minute);
		SimpleDateFormat dfprevious = new SimpleDateFormat("HH:mm");
		SimpleDateFormat dfold = new SimpleDateFormat("HH:mm aa");

		Date datenew = null;
		try {
			datenew = dfprevious.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String hr_min = dfold.format(datenew);

		tvQueue.setText(queue);
		tvLocation.setText(location);		
		tvTime.setText(hr_min);
		if(!isEnableFirst)
		{
			loadDataSendContinous(service_id);
			isEnableFirst=true;
		}


	}

	private void CalQueueHandler(final String service_id) {

		if(!utils.isConnectionPossible())
		{
			new AlertDialog.Builder(NameTicketActivity.this)
			.setMessage("Please check your internet connection...")			
			.setTitle("No Internet Connection")
			.setPositiveButton("Try Again", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					CalQueueHandler(service_id);
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
			List<NameValuePair> nameValuePair = callGridData();
			HttpConnection httpConnection = new HttpConnection(nameTicketGetQueueHandler);
			httpConnection.createPost(1, Constant.CONNECTION_URL, nameValuePair);
			if(!isTrue)
			{
				progress = new CustomProgress(NameTicketActivity.this,"Loading data.....");
			}
		}
	}


	private List<NameValuePair> callGridData()
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("action", "nameTicketQueue"));
		nameValuePairs.add(new BasicNameValuePair("device_id",service_id));
		return nameValuePairs;
	}

	public void loadDataSendContinous(String service_id)
	{
		loadTimerTosendData = new LoadTimertoContinousSendData(7000,2000,service_id);// 300000 5minutes senddatacontinouse
		loadTimerTosendData.start();
	}

	public class LoadTimertoContinousSendData extends CountDownTimer
	{
		String service_id;

		public LoadTimertoContinousSendData(long startTime, long interval,String service_id)
		{
			super(startTime, interval);
			System.out.println("-----LoadTimertoContinousSendData-----1-----"+startTime);
			this.service_id=service_id;
		}

		@Override
		public void onFinish()
		{  
			loadTimerTosendData.start();
			isTrue=true;
			CalQueueHandler(service_id);
			System.out.println("--continous-send data---1--");
		} 

		@Override
		public void onTick(long millisUntilFinished)
		{
			System.out.println("--continous-send data--2---"+millisUntilFinished);
		}
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//			moveTaskToBack(true);
		}
		return false;
	}


	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

		private String pushMessage;

		@Override
		public void onReceive(Context context, Intent intent)
		{
			pushMessage=intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
			pushMessage.trim();

			if (pushMessage.equalsIgnoreCase("Device removed")) 
			{
				stopTimer();
				calActivity();
			} 
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());
			WakeLocker.release();
		}

		private void stopTimer() 
		{
			isEnableFirst=false;
			if(loadTimerTosendData!=null)
			{
				loadTimerTosendData.cancel();
			}
		}
	};

	private void calActivity()
	{
		isTrue=false;
		finish();
		if(loadTimerTosendData!=null)
		{
			loadTimerTosendData.cancel();
		}
		Intent intentland = new Intent(NameTicketActivity.this,LandingActivity.class);
		startActivity(intentland);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.bEnter:

			callAddTicket();
			break;
		}		
	}

	private void callAddTicket()
	{
		loadTimerTosendData.cancel();
		String name=nameFeild.getText().toString();
		if(!name.equalsIgnoreCase(""))
		{
			callAddNameHandler(name,service_id);
			nameFeild.setText("");
		}		
		else {
			Toast.makeText(NameTicketActivity.this,"Please Enter Name" ,Toast.LENGTH_SHORT).show();
		}
	}


	private void callAddNameHandler(String name, final String service_id) 
	{
		if(!utils.isConnectionPossible())
		{
			new AlertDialog.Builder(NameTicketActivity.this)
			.setMessage("Please check your internet connection...")			
			.setTitle("No Internet Connection")
			.setPositiveButton("Try Again", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					CalQueueHandler(service_id);
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
			List<NameValuePair> nameValuePair = callSendName(service_id,name);
			HttpConnection httpConnection = new HttpConnection(addTicket);
			httpConnection.createPost(1, Constant.CONNECTION_URL, nameValuePair);
			progress = new CustomProgress(NameTicketActivity.this,"Loading data.....");
		}
	}


	private List<NameValuePair> callSendName(String service_id, String name)
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("action", "nameTicketTakeTicket"));
		nameValuePairs.add(new BasicNameValuePair("user_name",name));
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
