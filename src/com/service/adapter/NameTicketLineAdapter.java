package com.service.adapter;

/**
 * @author Sumit Kumar Maji
 */

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.service.VO.GridDataVO;
import com.service.activity.R;

public class NameTicketLineAdapter extends BaseAdapter
{
	private Context context;
	ArrayList<GridDataVO> gridData;
	private GridDataVO dataItem;
//	public Timer1 timer1;
//	public Timer2 timer2;
	private TextView tvMonitorNo;
	private TextView tvTicketNo;


	public NameTicketLineAdapter(Context context,int customListItem, ArrayList<GridDataVO> gridData) {
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
		View listView;

		if (convertView == null) {

			listView = new View(context);

			listView = inflater.inflate(R.layout.custom_list_item, null);

			tvMonitorNo = (TextView) listView.findViewById(R.id.tvMonitorNo);
			tvTicketNo = (TextView) listView.findViewById(R.id.tvTicketNo);
//			if(position==0)
//			{
//				tvTicketNo1 = (TextView) gridView.findViewById(R.id.tvTicketNo);	
//			}	

		}
		else {
			listView = (View) convertView;
//			if (!grid.getIndex().equalsIgnoreCase("0")) {
//
//			}
		}
		tvMonitorNo.setText("1");
		tvTicketNo.setText(String.valueOf(dataItem.getTicket_id()));
//		if (grid.getIndex().equalsIgnoreCase("0"))
//		{
//			tvTicketNo1.setText(String.valueOf(dataItem.getTicket_id()));			
//			timerWhite();
//
//		}
		return listView;

	}


//	public void timerWhite()
//	{
//		timer1 = new Timer1(300,400);// 300000 5minutes senddatacontinouse
//
//		tvTicketNo1.setTextColor(Color.parseColor("#FFFFFF"));
//		timer1.start();
//
//	}
//
//	public class Timer1 extends CountDownTimer
//	{
//		public Timer1(long startTime, long interval)
//		{
//			super(startTime, interval);
//			System.out.println("-----LoadTimertoContinousSendData-----1-----"+startTime);
//		}
//
//		@Override
//		public void onFinish()
//		{  
//			if(timer1!=null)
//			{
//				timer1.cancel();
//			}
//			timerBlack();
//
//		} 
//		@Override
//		public void onTick(long millisUntilFinished)
//		{
//
//		}
//	}
//
//	public void timerBlack()
//	{
//		timer2 = new Timer2(300,400);// 300000 5minutes senddatacontinouse
//		tvTicketNo1.setTextColor(Color.parseColor("#FA8405"));
//		timer2.start();
//	}
//
//	public class Timer2 extends CountDownTimer
//	{
//		public Timer2(long startTime, long interval)
//		{
//			super(startTime, interval);
//			System.out.println("-----LoadTimertoContinousSendData-----1-----"+startTime);
//
//		}
//
//		@Override
//		public void onFinish()
//		{  
//			if(timer2!=null)
//			{
//				timer2.cancel();
//			}
//			timerWhite();
//
//		} 
//		@Override
//		public void onTick(long millisUntilFinished)
//		{
//
//		}
//	}

}
