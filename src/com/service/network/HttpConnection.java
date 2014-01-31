package com.service.network;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.HttpConnectionParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;


/**
 * Asynchronous HTTP connections
 * @author Sumit Kumar Maji
 */
public class HttpConnection implements Runnable {

	public static final int DID_START = 0;
	public static final int DID_ERROR = 1;
	public static final int DID_SUCCEED = 2;
	public static final int DID_UNSUCCESS = 3;

	private static final int GET = 0;
	private static final int POST = 1;
	private static final int PUT = 2;
	private static final int DELETE = 3;
	private static final int BITMAP = 4;
	private static final int SOAP = 5;

	private String url;
	private int method;
	private final Handler handler;
	private String data;
	private String action;

	private HttpClient httpClient;
	private Message message = null;
	private int responseCode;
	private Map<String, SoftReference<Bitmap>> artCache;
	private int position;
	private int sampleSize;
	private List<? extends NameValuePair> nameValuePair;
	private String userId;
	private String password;
	private ByteArrayOutputStream makeRequest;


	public HttpConnection() {
		this(new Handler());
	}

	public HttpConnection(Handler _handler) {
		handler = _handler;
	}
	public void createPost(int method, String url, List<? extends NameValuePair> nameValuePair) {
		this.method = method;
		this.url = url;
		this.nameValuePair = nameValuePair;
		ConnectionManager.getInstance().push(this);
		System.out.println("Url is:::"+url);
	}
	public void create(int method, String url, String data) {
		this.method = method;
		this.url = url;
		this.data = data;
		ConnectionManager.getInstance().push(this);
		//System.out.println("Url is:::" + url);
	}
	public void createSoap(int method, String url,ByteArrayOutputStream makeRequest)
	{
		this.method = method;
		this.url = url;
		this.makeRequest = makeRequest;
		ConnectionManager.getInstance().push(this);
		//System.out.println("Url is:::" + url);

	}

	public void get(String url, int method) {
		create(GET, url, null);

	}

	public void post(String url, List<NameValuePair> nameValuePair) {
		createPost(POST, url, nameValuePair);
	}


	public void soap(String url,ByteArrayOutputStream makeRequest)
	{
		createSoap(SOAP,url,makeRequest);

	}

	public void put(String url, String data) {
		create(PUT, url, data);
	}

	public void delete(String url) {
		create(DELETE, url, null);
	}

	public void bitmap(String _url) {

		create(BITMAP, _url, null);
	}

	public void bitmap(String _url, int pos, int sampleSize) {
		this.position = pos;
		this.sampleSize = sampleSize;
		create(BITMAP, _url, null);

	}

	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		@Override
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	@Override
	public void run() {
		handler.sendMessage(Message.obtain(handler, HttpConnection.DID_START));
		httpClient = new SSLHttpClient();
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), 25000);
		try {
			HttpResponse response = null;
			switch (method) {
			case GET:				
				response = httpClient.execute(new HttpGet(url));
				//				System.out.println("Url is::" + url);
				//				System.out.println("Response Code::"
				//						+ response.getStatusLine().getStatusCode());
				//				System.out.println("Response Data::" + response);
				responseCode = response.getStatusLine().getStatusCode();
				processStreamEntity(response.getEntity());
				break;
			case POST:
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
				//				httpPost.setHeader("Accept", "application/json");
				//				httpPost.setHeader("Content-type", "application/json");				
				response = httpClient.execute(httpPost);
				System.out.println("Url is::" + url);
				System.out.println("XML Data is:::" + data);
				//				System.out.println("Response Code::"
				//						+ response.getStatusLine().getStatusCode());
				//				System.out.println("Response Data::" + response);
				responseCode = response.getStatusLine().getStatusCode();
				processStreamEntity(response.getEntity());

				break;
				//			case SOAP:
				//				InputStream inputStream = null;				
				//				SoapConnection con = new SoapConnection();
				//				HttpURLConnection connection = con.getConnection(this.makeRequest.size());
				//				con.writeData(this.makeRequest, connection);
				//				responseCode =  connection.getResponseCode();
				//				//System.out.println("Response code :::::::: "+connection.getResponseCode());
				//				inputStream = connection.getInputStream();
				//				processSoapStreamEntitiy(inputStream);				
				//				break;
			case PUT:
				HttpPut httpPut = new HttpPut(url);
				httpPut.setEntity(new StringEntity(data));
				response = httpClient.execute(httpPut);
				//				System.out.println("Url is::" + url);
				//				System.out.println("Json Data is:::" + data);
				//				System.out.println("Response Code::"
				//						+ response.getStatusLine().getStatusCode());
				//				System.out.println("Response Data::" + response);
				responseCode = response.getStatusLine().getStatusCode();
				processStreamEntity(response.getEntity());
				break;
			case DELETE:
				response = httpClient.execute(new HttpDelete(url));
				//System.out.println("Url is::" + url);
				break;
			case BITMAP:
				response = httpClient.execute(new HttpGet(url));
				responseCode = response.getStatusLine().getStatusCode();
				processBitmapEntity(response.getEntity());
				//System.out.println("Url is::" + url);
				break;
			}

		} catch (Exception e) {
			//			//System.out.println("Exception is:::" + e.toString());
			//			TakeTicketVO errorData = new TakeTicketVO();
			//			errorData.setErrorCode(String.valueOf(responseCode));
			//			errorData.setErrorUrl(url);
			//			handler.sendMessage(Message.obtain(handler,
			//					HttpConnection.DID_ERROR, errorData));
		}
		ConnectionManager.getInstance().didComplete(this);
	}

	private void processStreamEntity(HttpEntity entity)
			throws IllegalStateException, IOException {

		InputStream is = entity.getContent();

		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line, result = "";
		StringBuilder resultBuilder = new StringBuilder();
		while ((line = br.readLine()) != null)
			resultBuilder.append(line);

		if (responseCode == 200) {
			message = Message.obtain(handler, DID_SUCCEED,
					resultBuilder.toString());
			handler.sendMessage(message);
		} else {
			//			TakeTicketVO errorData = new TakeTicketVO();
			//			errorData.setErrorCode(String.valueOf(responseCode));
			//			errorData.setErrorUrl(url);
			//			message = Message.obtain(handler, DID_UNSUCCESS, errorData);
			//			handler.sendMessage(message);
		}

	}

	private void processSoapStreamEntitiy(InputStream is)
	{	


		if (responseCode == 200) {

			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line, result = "";
			StringBuilder resultBuilder = new StringBuilder();
			try {
				while ((line = br.readLine()) != null)
					resultBuilder.append(line);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			message = Message.obtain(handler, DID_SUCCEED,
					resultBuilder.toString());
			handler.sendMessage(message);
		} else {
			//			TakeTicketVO errorData = new TakeTicketVO();
			//			errorData.setErrorCode(String.valueOf(responseCode));
			//			errorData.setErrorUrl(url);
			//			message = Message.obtain(handler, DID_UNSUCCESS, errorData);
			//			handler.sendMessage(message);
		}


	}

	private void processBitmapEntity(HttpEntity entity) throws IOException {

		if (responseCode == 200) {
			BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);

			InputStream inputStream = bufHttpEntity.getContent();

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = -1;

			Bitmap bm = BitmapFactory.decodeStream(inputStream, new Rect(-1,
					-1, -1, -1), options);

			//			ImageDataVO imageData = new ImageDataVO();
			//			imageData.setPosition(this.position);
			//			imageData.setImageUrl(this.url);
			//			imageData.setBitmap(bm);
			//			handler.sendMessage(Message.obtain(handler, DID_SUCCEED, imageData));
			//			inputStream.close();
		} else {
			//			TakeTicketVO errorData = new TakeTicketVO();
			//			errorData.setErrorCode(String.valueOf(responseCode));
			//			errorData.setErrorUrl(url);
			//			message = Message.obtain(handler, DID_UNSUCCESS, errorData);
		}

	}

	//	public ByteArrayOutputStream createSoapXml(SOAPMessage secureHeader,
	//			String username, String password){
	//		
	//		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
	//
	//		try{
	//			// Create soap body
	//			SOAPFactory soapFactory = SOAPFactory.newInstance();
	//			SOAPBody body = secureHeader.getSOAPBody();
	//			Name bodyName = soapFactory.createName("a:authenticate");
	//			SOAPBodyElement bodyElement = body.addBodyElement(bodyName);
	//
	//			SOAPElement symbolUN = bodyElement.addChildElement("loginID");
	//			symbolUN.addTextNode(username);
	//
	//			SOAPElement symbolCName = bodyElement
	//					.addChildElement("password");
	//			symbolCName.addTextNode(password);
	//
	//			// Display the header XML			
	//			secureHeader.writeTo(bOut);
	//		}catch(Exception ex){
	//			 
	//		}
	//
	//		return bOut;
	//	}
}
