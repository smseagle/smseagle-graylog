package org.graylog2.Notifications.smseagle;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.JsonObject;

import edu.emory.mathcs.backport.java.util.Arrays;
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
	 * Method for sending a SMS using SMSEagle api v2
	 * 
	 * @throws SMSEagleException
	 */
	@SuppressWarnings("null")
	public void contact(String to, String contacts, String groups, String message, String contactType, int ringDuration,
			int voiceId, String elevenLabsAPIKey) throws SMSEagleException {
		ArrayList<String> tempToList = new ArrayList<>();
		for (String i : to.split("[\\.\\s,;]")) {
			tempToList.add(i);
		}
		String[] toStrArr = (String[]) tempToList.toArray();

		ArrayList<Integer> tempContactList = new ArrayList<>();
		for (String i : contacts.split("[\\.\\s,;]")) {
			tempContactList.add(Integer.parseInt(i));
		}
		Integer[] contactsIntArr = (Integer[]) tempContactList.toArray();

		ArrayList<Integer> tempGroupList = new ArrayList<>();
		for (String i : groups.split("[\\.\\s,;]")) {
			tempGroupList.add(Integer.parseInt(i));
		}
		Integer[] groupsIntArr = (Integer[]) tempGroupList.toArray();

		HttpURLConnection connection = null;
		OutputStreamWriter writer = null;
		JSONObject parameters = new JSONObject();
		parameters.put("to", toStrArr);
		parameters.put("contacts", contactsIntArr);
		parameters.put("groups", groupsIntArr);
		parameters.put("text", message);
		String sendSMSUrl = this.url;
		sendSMSUrl += sendSMSUrl.endsWith("/") ? "api/v2/" : "/api/v2/";
		try {

			switch (contactType) {
				case "SMS":
				case "FLASHSMS":
				case "MULTICHANNEL":
				case "RING":
					sendSMSUrl += "messages/multichannel";
					JSONObject sms = new JSONObject();
					if (contactType != "RING") {
						sms.put("flash", contactType == "FLASHSMS");
						parameters.put("sms", sms);
					}
					if (!Pattern.matches(".*SMS", contactType)) {
						parameters.put("ring", new JSONObject().put("duration", ringDuration));
					}
					break;


				case "SIGNAL":
					sendSMSUrl += "messages/signal";
					break;


				case "WHATSAPP":
					sendSMSUrl += "messages/whatsapp";
					break;


				case "TTS":
					sendSMSUrl += "calls/tts";
					parameters.put("duration", ringDuration);
					break;


				case "TTS_ADV":
					sendSMSUrl += "calls/tts_advanced";
					parameters.put("duration", ringDuration);
					parameters.put("voice_id", voiceId);
					break;


				case "ELEVENLABS":
					sendSMSUrl += "calls/elevenlabs";
					parameters.put("duration", ringDuration);
					parameters.put("voice_id", voiceId);
					break;


				case "ELEVENLABS_REMOTE":
					sendSMSUrl += "calls/elevenlabs_direct";
					parameters.put("duration", ringDuration);
					parameters.put("voice_id", voiceId);
					parameters.put("api_key", elevenLabsAPIKey);
				default:
					break;
			}
			connection = this.genCon(sendSMSUrl);
			writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(parameters.toString());
			writer.close();

			Object response = new JSONParser(JSONParser.MODE_PERMISSIVE).parse(connection.getInputStream());
			String result = "";
			if (response != null && response instanceof JSONObject) {
				result = ((JSONObject) response).getAsString("result");
			} else {
				throw new SMSEagleException( "Error parsing response for SMS API or it is null - " + response != null ? response.toString() : "response is null");
			}
			if (!result.contains("OK"))
				throw new SMSEagleException("Error in Send SMS method: " + result);
		} catch (Exception e) {
			throw new SMSEagleException(e.getMessage(), e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}

	private HttpURLConnection genCon(String gateURL) throws SMSEagleException {
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
