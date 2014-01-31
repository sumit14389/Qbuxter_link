package com.service.parser;

/**
 * @author Sumit Kumar Maji
 */

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.service.VO.NameTicketVO;

public class NameTicketParser {

	String jSonData;
	private NameTicketVO nameTicketVO;

	public NameTicketParser(String data)
	{
		this.jSonData = data;
	}

	public ArrayList<NameTicketVO> parse()
	{

		ArrayList<NameTicketVO> queueArray = new ArrayList<NameTicketVO>();

		try {
			JSONObject jObject = new JSONObject(jSonData);

			JSONObject response = jObject.getJSONObject("response");

			String isSucessCheck=response.getString("message");

			if(isSucessCheck.equalsIgnoreCase("Successful"))
			{
				nameTicketVO = new NameTicketVO();

				String queueNo = response.getString("queue");
				nameTicketVO.setQueueNo(queueNo);

				String location = response.getString("location");
				nameTicketVO.setLocation(location);
				
				queueArray.add(nameTicketVO);
			}

		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return queueArray;
	}
}
