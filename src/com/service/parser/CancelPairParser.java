package com.service.parser;

/**
 * @author Sumit Kumar Maji
 */

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.service.VO.CancelPairVO;
import com.service.VO.PairCodeVO;
import com.service.constant.Constant;

public class CancelPairParser {

	String jSonData;
	private CancelPairVO cancelPairVO;

	public CancelPairParser(String data)
	{
		this.jSonData = data;
	}

	public ArrayList<CancelPairVO> parse()
	{

		ArrayList<CancelPairVO> cancelArray = new ArrayList<CancelPairVO>();

		try {
			JSONObject jObject = new JSONObject(jSonData);

			JSONObject response = jObject.getJSONObject("response");

			String isSucessCheck=response.getString("message");

			if(isSucessCheck.equalsIgnoreCase("Cancel Successful"))
			{
				cancelPairVO = new CancelPairVO();

				cancelPairVO.setIsCancelSucess("Cancel Successful");

				cancelArray.add(cancelPairVO);
			}

		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return cancelArray;
	}
}
