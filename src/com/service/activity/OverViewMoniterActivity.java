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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.GridView;

import com.google.android.gcm.GCMRegistrar;
import com.service.VO.GridDataVO;
import com.service.adapter.OverviewTicketAdapter;
import com.service.constant.Constant;
import com.service.network.HttpConnection;
import com.service.parser.GridDataParser;
import com.service.util.CommonUtilities;
import com.service.util.CustomProgress;
import com.service.util.RegisterActivities;
import com.service.util.Util;

public class OverViewMoniterActivity extends Activity {

	private GridView gv;
	private Handler gridDataHandler;
	private Util utils;
	private String service_id="";
	//	private CustomProgress progress;
	private GridDataParser gridDataParser;
	private GridDataVO gridDataVO;
	private ArrayList<GridDataVO> gridData;
	private LoadTimertoContinousSendData loadTimerTosendData;
	private OverviewTicketAdapter overviewTicketAdapter;
	private String regId;
	private String pushTicket;
	private String pushMessage;
	private boolean isProgressTrue;
	private boolean isLoadDataEnableFirstTime;
	private CustomProgress progress;
	private boolean isLoadDataEnable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_over_view_moniter);
		RegisterActivities.registerActivity(this);
		utils=new Util(this);
		registerDevice();
		Intent iin= getIntent();
		Bundle bundle = iin.getExtras();
		if(bundle!=null && !bundle.isEmpty())
		{			
			service_id=(String)bundle.get("service_id");
		}

		gridDataHandler = new Handler()
		{
			public void handleMessage(Message message)
			{
				switch(message.what)                  
				{
				case HttpConnection.DID_START:
					if(!isProgressTrue){
						progress.show();
					}
					break;
				case HttpConnection.DID_SUCCEED:
					if(!isProgressTrue) {
						if(progress!=null && progress.isShowing())
							progress.dismiss();
					}
					String xmlData = (String)message.obj;
					gridDataParser = new GridDataParser(xmlData);
					Constant.gridDataArray=gridDataParser.parse();
					if(Constant.gridDataArray!=null)
					{						
						initView();
					}

					break;

				case HttpConnection.DID_UNSUCCESS:
					if(!isProgressTrue){
						progress.dismiss();
					}
					//utils.showDialog(SplashActivity.this, R.string.Authentication);
					break;

				case HttpConnection.DID_ERROR:
					if(!isProgressTrue){
						progress.dismiss();
					}
					utils.showDialog("Connection Not Possible");
					break;
				}
			}
		};

		if(Constant.gridDataArray==null || Constant.gridDataArray.size()==0)
		{
			CalGridDataHandler(service_id);
		}
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

	private void initView() {

		gv=(GridView)findViewById(R.id.gvTicket);

		gridData = new ArrayList<GridDataVO>();
		//gv.setPadding(15, 2, 0, 10);
		loadData();
		if (!isLoadDataEnableFirstTime) 
		{
			loadDataSendContinous(service_id);
			isLoadDataEnableFirstTime=true;
		}

	}

	public void loadData(){
		if(!isLoadDataEnable) {
			if(Constant.gridDataArray!=null && Constant.gridDataArray.size()>0)
			{
				for (int j = 0; j < 16; j++) 
				{
					gridDataVO = (GridDataVO)Constant.gridDataArray.get(j);
					gridData.add(gridDataVO);	
				}
				overviewTicketAdapter = new OverviewTicketAdapter(OverViewMoniterActivity.this, R.layout.custom_list_item,gridData);
				gv.setAdapter(overviewTicketAdapter);
				overviewTicketAdapter.notifyDataSetChanged();
			}
		}
	}

	private void CalGridDataHandler(final String service_id) {

		if(!utils.isConnectionPossible())
		{
			new AlertDialog.Builder(OverViewMoniterActivity.this)
			.setMessage("Please check your internet connection...")			
			.setTitle("No Internet Connection")
			.setPositiveButton("Try Again", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					CalGridDataHandler(service_id);
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
			HttpConnection httpConnection = new HttpConnection(gridDataHandler);
			httpConnection.createPost(1, Constant.CONNECTION_URL, nameValuePair);
			if(!isProgressTrue){

				progress = new CustomProgress(OverViewMoniterActivity.this,"Loading data.....");
			}
		}

	}
	private List<NameValuePair> callGridData()
	{
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("action", "topDisplay"));
		nameValuePairs.add(new BasicNameValuePair("device_id",service_id));

		return nameValuePairs;
	}

	public void loadDataSendContinous(String service_id)
	{
		loadTimerTosendData = new LoadTimertoContinousSendData(3000,400,service_id);// 300000 5minutes senddatacontinouse
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
			isProgressTrue=true;
			CalGridDataHandler(service_id);
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

		@Override
		public void onReceive(Context context, Intent intent) {


			pushTicket=intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
			pushMessage=intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);

			pushTicket.trim();
			pushMessage.trim();

			if (pushMessage.equalsIgnoreCase("Device removed")) 
			{
				isLoadDataEnable=true;
				stopTimer();
				calActivity();
			} 
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());
			WakeLocker.release();
		}

		private void stopTimer() 
		{
			Constant.isEnableGridTimer=true;
			
			if(loadTimerTosendData!=null)
			{
				isLoadDataEnableFirstTime=false;
				loadTimerTosendData.cancel();
			}
		}
	};


	private void calActivity()
	{
		isProgressTrue=false;
		isLoadDataEnable=true;
		if(Constant.gridDataArray!=null)
		{
			Constant.gridDataArray.clear();
		}
		finish();

		Intent intentland = new Intent(OverViewMoniterActivity.this,LandingActivity.class);
		startActivity(intentland);
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
