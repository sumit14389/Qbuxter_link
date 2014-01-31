package com.service.parser;

/**
 * @author Sumit Kumar Maji
 */

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.service.VO.AddTicketVO;

public class AddSucessParser {

	String jSonData;
	private AddTicketVO addTicketVO;

	public AddSucessParser(String data)
	{
		this.jSonData = data;
	}

	public ArrayList<AddTicketVO> parse()
	{

		ArrayList<AddTicketVO> addTicketArray = new ArrayList<AddTicketVO>();

		try {
			JSONObject jObject = new JSONObject(jSonData);

			JSONObject response = jObject.getJSONObject("response");

			String isSucessCheck=response.getString("message");

			if(isSucessCheck.equalsIgnoreCase("Successful"))
			{
				addTicketVO = new AddTicketVO();

				addTicketVO.setIsSucess(isSucessCheck);

				String ticketNo = response.getString("result");
				addTicketVO.setTicketId(ticketNo);

				addTicketArray.add(addTicketVO);
			}

		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return addTicketArray;
	}
}
