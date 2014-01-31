package com.service.activity;

/**
 * @author Sumit Kumar Maji
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.service.constant.Constant;
import com.service.util.CommonUtilities;
import com.service.util.RegisterActivities;

public class DeskMonitorActivity extends Activity {

	private TextView tvCallTicketNo;
	private Typeface tf;
	private String regId;
	private String pushMessage;
	private Timer1 timer1;
	private Timer2 timer2;
	private String pushTicket;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_desk_monitor);
		RegisterActivities.registerActivity(this);
		intitView();
		registerDevice();

	}

	private void registerDevice() {
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//			moveTaskToBack(true);
		}
		return false;
	}

	private void intitView() {

		tvCallTicketNo=(TextView)findViewById(R.id.tvCallTicketNo);
		tf = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueLTPro-BlkCn.otf");
		tvCallTicketNo.setTypeface(tf);
		//		if (pushTicket.equalsIgnoreCase("") && pushTicket!=null) {
		//			tvCallTicketNo.setText(pushTicket);
		//		} else {
		//			tvCallTicketNo.setText(000);
		//		}

	}

	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {


		@Override
		public void onReceive(Context context, Intent intent) {


			pushTicket=intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
			pushMessage=intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);

			pushTicket.trim();
			pushMessage.trim();

			if (pushMessage.equalsIgnoreCase("Device removed")) 
			{
				if(timer1!=null)
				{
					timer1.cancel();
				}
				if(timer2!=null)
				{
					timer2.cancel();
				}
				calActivity();
			} 
			else
			{
				if (pushTicket.matches("\\d+")) 
				{
					tvCallTicketNo.setText(pushTicket);
					timerBlack();
				}

				
			}


			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			WakeLocker.release();
		}
	};


	public void timerWhite()
	{
		timer1 = new Timer1(300,400);// 300000 5minutes senddatacontinouse
		tvCallTicketNo.setTextColor(Color.parseColor("#FFFFFF"));
		timer1.start();
	}

	public class Timer1 extends CountDownTimer
	{
		public Timer1(long startTime, long interval)
		{
			super(startTime, interval);
			System.out.println("-----LoadTimertoContinousSendData-----1-----"+startTime);
		}

		@Override
		public void onFinish()
		{  
			if(timer1!=null)
			{
				timer1.cancel();
			}
			timerBlack();

		} 
		@Override
		public void onTick(long millisUntilFinished)
		{

		}
	}

	public void timerBlack()
	{
		timer2 = new Timer2(300,400);// 300000 5minutes senddatacontinouse
		tvCallTicketNo.setTextColor(Color.parseColor("#FA8405"));
		timer2.start();
	}

	public class Timer2 extends CountDownTimer
	{
		public Timer2(long startTime, long interval)
		{
			super(startTime, interval);
			System.out.println("-----LoadTimertoContinousSendData-----1-----"+startTime);

		}

		@Override
		public void onFinish()
		{  
			if(timer2!=null)
			{
				timer2.cancel();
			}
			timerWhite();

		} 
		@Override
		public void onTick(long millisUntilFinished)
		{

		}
	}


	private void calActivity() {

		finish();
		Intent intentland = new Intent(DeskMonitorActivity.this,LandingActivity.class);
		startActivity(intentland);
	}
}
