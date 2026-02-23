package org.graylog.plugins.smseagle;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class SMSEagleClient {	

	private String url;
	private String accessToken;

	/**
	 * Constructor method
	 * 
	 * @param url
	 * @param accessToken
	 */
	public SMSEagleClient(String url, String accessToken) {
		this.url = url;
		this.accessToken = accessToken;
	}

	/**
	 * Method for sending a message using SMSEagle APIv2
	 * 
	 * @throws SMSEagleException
	 */
	public void contact(
	    String to,
	    String contacts,
	    String groups,
	    String message,
	    String contactType,
	    int ringDuration,
		int voiceId,
		String elevenLabsAPIKey
    ) throws SMSEagleException {
		ArrayList<String> tempToList = new ArrayList<>();
		if (to != null && !to.trim().isEmpty()) {
			for (String i : to.split("[\\.\\s,;]")) {
				if (!i.trim().isEmpty()) {
					tempToList.add(i.trim());
				}
			}
		}
		String[] toStrArr = tempToList.toArray(new String[0]);

		ArrayList<Integer> tempContactList = new ArrayList<>();
		if (contacts != null && !contacts.trim().isEmpty()) {
			for (String i : contacts.split("[\\.\\s,;]")) {
				if (!i.trim().isEmpty()) {
					tempContactList.add(Integer.parseInt(i.trim()));
				}
			}
		}
		Integer[] contactsIntArr = tempContactList.toArray(new Integer[0]);

		ArrayList<Integer> tempGroupList = new ArrayList<>();
		if (groups != null && !groups.trim().isEmpty()) {
			for (String i : groups.split("[\\.\\s,;]")) {
				if (!i.trim().isEmpty()) {
					tempGroupList.add(Integer.parseInt(i.trim()));
				}
			}
		}
		Integer[] groupsIntArr = tempGroupList.toArray(new Integer[0]);

		HttpURLConnection connection = null;
		OutputStreamWriter writer = null;
		
		JSONObject parameters = new JSONObject();
		parameters.put("to", toStrArr);
		parameters.put("contacts", contactsIntArr);
		parameters.put("groups", groupsIntArr);
		
		String sendSMSUrl = this.url;
		sendSMSUrl += sendSMSUrl.endsWith("/") ? "api/v2/" : "/api/v2/";
		
		try {

			switch (contactType) {
				case "SMS":
                    sendSMSUrl += "messages/sms";
                    parameters.put("text", message);
                break;
                
				case "FLASHSMS":
                    sendSMSUrl += "messages/sms";
                    parameters.put("text", message);
                    parameters.put("flash", true);
                break;
                
				case "RING":
                    sendSMSUrl += "calls/ring";
					parameters.put("duration", ringDuration);
                break;

				case "SIGNAL":
					sendSMSUrl += "messages/signal";
					parameters.put("text", message);
                break;

				case "WHATSAPP":
					sendSMSUrl += "messages/whatsapp";
					parameters.put("text", message);
                break;

				case "TTS":
					sendSMSUrl += "calls/tts";
					parameters.put("text", message);
					parameters.put("duration", ringDuration);
                break;

				case "TTS_ADV":
					sendSMSUrl += "calls/tts_advanced";
					parameters.put("text", message);
					parameters.put("duration", ringDuration);
					parameters.put("voice_id", voiceId);
                break;

				case "ELEVENLABS":
					sendSMSUrl += "calls/elevenlabs";
					parameters.put("text", message);
					parameters.put("duration", ringDuration);
					parameters.put("voice_id", voiceId);
                break;

				case "ELEVENLABS_DIRECT":
					sendSMSUrl += "calls/elevenlabs_direct";
					parameters.put("text", message);
					parameters.put("duration", ringDuration);
					parameters.put("voice_id", voiceId);
					parameters.put("api_key", elevenLabsAPIKey);
                break;
			}
			
			connection = this.createConnection(sendSMSUrl);
			
			writer = new OutputStreamWriter(connection.getOutputStream(), Charset.defaultCharset());
			writer.write(parameters.toString());
			writer.close();

			Object response = new JSONParser(JSONParser.MODE_PERMISSIVE).parse(connection.getInputStream());
			
			String result = "";
			
			if (response != null && response instanceof JSONObject) {
				result = ((JSONObject) response).getAsString("result");
			} else {
				throw new SMSEagleException( "Error parsing response for API or it is null - " + response != null ? response.toString() : "response is null");
			}
			
			if (!result.contains("OK"))
				throw new SMSEagleException("Error in method: " + result);
		} catch (Exception e) {
			throw new SMSEagleException(e.getMessage(), e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}

	private HttpURLConnection createConnection(String gateURL) throws SMSEagleException {
		try {
			URI serverURI = new URI(gateURL);
			URL serverAddress = serverURI.toURL();
			HttpURLConnection connection = (HttpURLConnection) serverAddress.openConnection();
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("access-token", this.accessToken);
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.connect();

			return connection;
		} catch (Exception e) {
			throw new SMSEagleException(e.getMessage(), e);
		}
	}
}
