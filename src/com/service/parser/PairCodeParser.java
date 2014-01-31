package com.service.parser;

/**
 * @author Sumit Kumar Maji
 */

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.service.VO.PairCodeVO;

public class PairCodeParser {

	String jSonData;
	private PairCodeVO pairCodeVO;

	public PairCodeParser(String data)
	{
		this.jSonData = data;
	}

	public ArrayList<PairCodeVO> parse()
	{

		ArrayList<PairCodeVO> pairArray = new ArrayList<PairCodeVO>();

		try {
			JSONObject jObject = new JSONObject(jSonData);

			JSONObject response = jObject.getJSONObject("response");

			String isSucessCheck=response.getString("message");

			if(isSucessCheck.equalsIgnoreCase("Connect Successful"))
			{
				pairCodeVO = new PairCodeVO();

				String pairCode = response.getString("pairing_code");
				pairCodeVO.setPairing_code(pairCode);

				String service_id = response.getString("device_id");
				pairCodeVO.setService_id(service_id);

				pairArray.add(pairCodeVO);
			}

		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return pairArray;
	}
}
