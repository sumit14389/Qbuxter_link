package com.service.adapter;

/**
 * @author Sumit Kumar Maji
 */

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.service.VO.GridDataVO;
import com.service.activity.R;
import com.service.constant.Constant;

public class OverviewTicketAdapter extends BaseAdapter
{
	public boolean isEnabletimer;
	private Context context;
	ArrayList<GridDataVO> gridData;
	private GridDataVO dataItem;
	public Timer1 timer1;
	public Timer2 timer2;
	private TextView tvMonitorNo;
	private TextView tvTicketNo;
	private TextView tvTicketNo1;
	private Typeface tf1;
	private Typeface tf2;


	public OverviewTicketAdapter(Context context,int customListItem, ArrayList<GridDataVO> gridData) {
		this.context=context;
		this.gridData=gridData;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return gridData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		dataItem=gridData.get(position);
		GridDataVO grid=gridData.get(position);
		View gridView;

		if (convertView == null) {

			gridView = new View(context);

			// get layout from android.xml
			gridView = inflater.inflate(R.layout.custom_gridview, null);

			//gridView.setPadding(10, 5, 10, 5);
			// set text
			tvMonitorNo = (TextView) gridView.findViewById(R.id.tvMonitorNo);
			tvTicketNo = (TextView) gridView.findViewById(R.id.tvTicketNo);
			if(position==0)
			{
				tvTicketNo1 = (TextView) gridView.findViewById(R.id.tvTicketNo);	
			}
		}
		else {
			gridView = (View) convertView;
			if (!grid.getIndex().equalsIgnoreCase("0")) {

			}
		}
		//		tvMonitorNo.setText("1");
		tf1 = Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaNeueLTPro-ThCn.otf");
		tf2 = Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaNeueLTPro-BlkCn.otf");
		tvTicketNo.setText(String.valueOf(dataItem.getTicket_id()));
		tvTicketNo.setTypeface(tf2);

		if (grid.getIndex().equalsIgnoreCase("0"))
		{
			tvTicketNo1.setText(String.valueOf(dataItem.getTicket_id()));			
			timerWhite();
			//	        Timer timing = new Timer();
			//	        timing.schedule(new Updater(tvTicketNo1), 2000, 2000);

		}
		return gridView;

	}

	//	private static class Updater extends TimerTask {
	//        private final TextView tvTicketNo1;
	//
	//        public Updater(TextView tvTicketNo1) {
	//            this.tvTicketNo1 = tvTicketNo1;
	//        }
	//
	//        @Override
	//        public void run() {
	//        	tvTicketNo1.post(new Runnable() {
	//
	//                public void run() {
	//                	tvTicketNo1.setTextColor(Color.parseColor("#FA8405"));
	//                }
	//            });
	//        }



	public void timerWhite()
	{

		if(!Constant.isEnableGridTimer)
		{	
			timer1 = new Timer1(300,400);// 300000 5minutes senddatacontinouse

			tvTicketNo1.setTextColor(Color.parseColor("#FFFFFF"));
			timer1.start();
			//			isEnabletimer=true;
		}

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
		if (!Constant.isEnableGridTimer)
		{
			timer2 = new Timer2(300,400);// 300000 5minutes senddatacontinouse
			tvTicketNo1.setTextColor(Color.parseColor("#FA8405"));
			timer2.start();
			//			isEnabletimer=true;
		}
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
	public void stopAllTimer()
	{
		if(timer2!=null)
		{
			timer2.cancel();
		}

		if(timer1!=null)
		{
			timer1.cancel();
		}
	}

}
