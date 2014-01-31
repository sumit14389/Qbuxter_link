package com.service.activity;

import com.service.util.RegisterActivities;
import com.service.util.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

public class NameTicketLineActivity extends Activity {

	private Util utils;
	private String service_id;
	private ListView lvLine;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_name_ticket_line);
		RegisterActivities.registerActivity(this);
		utils=new Util(this);
		registerDevice();
		initview();
		Intent iin= getIntent();
		Bundle bundle = iin.getExtras();
		if(bundle!=null && !bundle.isEmpty())
		{			
			service_id=(String)bundle.get("service_id");
		}
		
		
		
	}

	private void initview() {
		lvLine=(ListView)findViewById(R.id.lvLine);
		
	}

	private void registerDevice() {
		// TODO Auto-generated method stub
		
	}



}
