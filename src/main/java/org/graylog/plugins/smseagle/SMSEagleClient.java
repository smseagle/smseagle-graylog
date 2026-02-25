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
		int ttsVoiceId,
		int elevenlabsVoiceId,
		String elevenlabsDirectVoiceId,
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
					parameters.put("voice_id", ttsVoiceId);
                break;

				case "ELEVENLABS":
					sendSMSUrl += "calls/elevenlabs";
					parameters.put("text", message);
					parameters.put("duration", ringDuration);
					parameters.put("voice_id", elevenlabsVoiceId);
                break;

				case "ELEVENLABS_DIRECT":
					sendSMSUrl += "calls/elevenlabs_direct";
					parameters.put("text", message);
					parameters.put("duration", ringDuration);
					parameters.put("voice_id", elevenlabsDirectVoiceId);
					parameters.put("api_key", elevenLabsAPIKey);
                break;
			}
			
			connection = this.createConnection(sendSMSUrl);
			
			writer = new OutputStreamWriter(connection.getOutputStream(), Charset.defaultCharset());
			writer.write(parameters.toString());
			writer.close();

			int responseCode = connection.getResponseCode();
			java.io.InputStream responseStream = (responseCode >= 200 && responseCode < 300)
					? connection.getInputStream()
					: connection.getErrorStream();

			if (responseStream == null) {
				throw new SMSEagleException("SMSEagle API returned HTTP " + responseCode + " with no response body");
			}

			Object response = new JSONParser(JSONParser.MODE_PERMISSIVE).parse(responseStream);

			if (response == null) {
				throw new SMSEagleException("Empty response from SMSEagle API");
			}

			String responseStr = response.toString();

			if (response instanceof JSONObject) {
				String result = ((JSONObject) response).getAsString("result");
				if (result == null || !result.contains("OK")) {
					throw new SMSEagleException(responseStr);
				}
			} else if (response instanceof net.minidev.json.JSONArray) {
				boolean hasOk = false;
				for (Object item : (net.minidev.json.JSONArray) response) {
					if (item instanceof JSONObject) {
						String msg = ((JSONObject) item).getAsString("message");
						if ("OK".equals(msg)) {
							hasOk = true;
							break;
						}
					}
				}
				if (!hasOk) {
					throw new SMSEagleException(responseStr);
				}
			} else {
				throw new SMSEagleException(responseStr);
			}
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
