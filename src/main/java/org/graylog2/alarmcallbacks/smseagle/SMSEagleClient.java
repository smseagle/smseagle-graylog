package org.graylog2.alarmcallbacks.smseagle;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class SMSEagleClient {
	
	private String url;
	private String accessToken;

	/**
	 * Constructor method
	 * @param url
	 * @param accessToken
	 */
	public SMSEagleClient(String url, String accessToken) {
		this.url = url;
		this.accessToken = accessToken;
	}

	/**
	 * Method for sending a SMS using JSON-RPC
	 * @throws SMSEagleException 
	 */
	public void sendSMS(String to, String groupName, String message) throws SMSEagleException {
		HttpURLConnection connection = null;
		OutputStreamWriter writer = null;
		URL serverAddress = null;
		try {
			String sendSMSUrl = this.url;
			sendSMSUrl += sendSMSUrl.endsWith("/") ? "jsonrpc/sms" : "/jsonrpc/sms";
			serverAddress = new URL(sendSMSUrl);
			connection = (HttpURLConnection) serverAddress.openConnection();
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.connect();
			
			writer = new OutputStreamWriter(connection.getOutputStream());
			JSONObject parameters = new JSONObject();
			parameters.put("access_token", this.accessToken);
			if ( groupName != null && groupName.trim().length() > 0 ) {
				parameters.put("groupname", groupName);
			} else {
				parameters.put("to", to);
			}
			parameters.put("message", message);
			JSONObject json = new JSONObject();
			if ( groupName != null && groupName.trim().length() > 0 ) {
				json.put("method", "sms.send_togroup");
			} else {
				json.put("method", "sms.send_sms");
			}
			json.put("params", parameters);
			
			writer.write(json.toString());
			writer.close();
			
			Object response = new JSONParser(JSONParser.MODE_PERMISSIVE).parse(connection.getInputStream());
			String result = "";
			if ( response != null && response instanceof JSONObject ) {
				result = ((JSONObject)response).getAsString("result");
			} else {
				throw new SMSEagleException("Error parsing response for SMS API or it is null - " + response.toString());
			}
			if ( !result.contains("OK") ) 
				throw new SMSEagleException("Error in Send SMS method: " + result); 
		} catch (Exception e) {
			throw new SMSEagleException(e.getMessage(), e);
		} finally {
			if ( connection != null ) connection.disconnect();
		}
	}

}
