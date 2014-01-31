package com.service.parser;

/**
 * @author Sumit Kumar Maji
 */

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.service.VO.GridDataVO;

public class GridDataParser {

	String jSonData;
	private GridDataVO gridDataVO;

	public GridDataParser(String data)
	{
		this.jSonData = data;
	}

	public ArrayList<GridDataVO> parse()
	{

		ArrayList<GridDataVO> gridDataArray = new ArrayList<GridDataVO>();

		try {
			JSONObject jObject = new JSONObject(jSonData);

			JSONObject response = jObject.getJSONObject("response");

			String isSucessCheck=response.getString("message");

			if(isSucessCheck.equalsIgnoreCase("Successful"))
			{

				JSONArray processDataArray = response.getJSONArray("result");

				int len = processDataArray.length();


				for (int i = 0; i < len; i++) {
					
					gridDataVO = new GridDataVO();

					JSONObject resultArray = processDataArray.getJSONObject(i);
					String ticket_id = resultArray.getString("ticket_id");
					gridDataVO.setTicket_id(ticket_id);
					
					gridDataVO.setIndex(i+"");
//					String date_time = resultArray.getString("date_time");
//					gridDataVO.setTime_of_ticket(date_time);
//
//					String first_queue = resultArray.getString("first_queue");
//					gridDataVO.setQueue_no(first_queue);
//					
//					String status = resultArray.getString("status");
//					gridDataVO.setStatus(status);

					
					gridDataArray.add(gridDataVO);
				}
			}

		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return gridDataArray;
	}
}
