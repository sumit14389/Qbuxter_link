package com.service.activity;

/**
 * @author Sumit Kumar Maji
 */

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.google.android.gcm.GCMRegistrar;
import com.service.constant.Constant;
import com.service.util.CommonUtilities;
import com.service.util.RegisterActivities;

public class SplashActivity extends Activity {

	Timer timer;
	private String regId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		RegisterActivities.registerActivity(this);
		registerDevice();
		timer = new Timer();

		startTimer();
	}

	public void registerDevice() 
	{
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);

		regId = GCMRegistrar.getRegistrationId(this);

		if (regId.equals("")) 
		{		
			GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
		}
		regId = GCMRegistrar.getRegistrationId(this);
		Constant.REG_ID=regId;
	}

	private void startTimer() 
	{
		timer.scheduleAtFixedRate(new TimerTask(){

			@Override
			public void run() {

				Intent intent = new Intent(SplashActivity.this,LandingActivity.class);
				startActivity(intent);
				timer.cancel();
				finish();
			}
		}, 3000, 2000);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if(keyCode == event.KEYCODE_BACK)
		{
			
		}

		return false;
	}
	@Override
	protected void onDestroy() {

		try {
			
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

}
