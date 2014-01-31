package com.service.constant;

/**
 * @author Sumit Kumar Maji
 */

import java.util.ArrayList;

import com.service.VO.AddTicketVO;
import com.service.VO.CancelPairVO;
import com.service.VO.GridDataVO;
import com.service.VO.NameTicketVO;
import com.service.VO.PairCodeVO;

public class Constant {



	public static ArrayList<PairCodeVO> authenticationArrayList;
	public static String REG_ID="";
	public static String CONNECTION_URL="http://174.136.1.35/dev/Qbuxter/api.php";
	public static ArrayList<PairCodeVO> codeArray;
	public static ArrayList<CancelPairVO> cancelPairArray;
	public static ArrayList<GridDataVO> gridDataArray;
	public static ArrayList<NameTicketVO> nameTicketArray ;

	public static ArrayList<AddTicketVO> addTicketArray ;
	public static boolean isEnableGridTimer;




}
