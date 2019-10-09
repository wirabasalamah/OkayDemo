package com.gyp.okaydemo.data;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonHandler {
	private static final String TAG = "HttpClient";
	static HttpURLConnection urlConnection;

	public static JSONObject SendHttpPost(String urel, JSONObject jsonObjSend) {
		StringBuilder result = new StringBuilder();
		try {
			URL url = new URL(urel);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(60000);
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setRequestProperty("Accept", "application/json");
			urlConnection.setRequestMethod("POST");

			OutputStream os = urlConnection.getOutputStream();
			os.write(jsonObjSend.toString().getBytes("UTF-8"));
			os.flush();
			os.close();

			int HttpResult = urlConnection.getResponseCode();
			if(HttpResult == HttpURLConnection.HTTP_OK){
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());

				BufferedReader reader = new BufferedReader(new InputStreamReader(in));

				String line;
				while ((line = reader.readLine()) != null) {
					result.append(line);
				}
				JSONObject jsonObjRecv = new JSONObject(result.toString());
				return jsonObjRecv;
			} else {
				return null;
			}

		} catch( Exception e) {
			return null;
		}
		finally {
			urlConnection.disconnect();
		}

	}


	public static JSONObject getJson(String urel) {
		StringBuilder result = new StringBuilder();

		try {
			URL url = new URL(urel);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(60000);
			int HttpResult = urlConnection.getResponseCode();
			if(HttpResult == HttpURLConnection.HTTP_OK){
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());

				BufferedReader reader = new BufferedReader(new InputStreamReader(in));

				String line;
				while ((line = reader.readLine()) != null) {
					result.append(line);
				}
				JSONObject jsonObjRecv = new JSONObject(result.toString());
				return jsonObjRecv;
			} else {
				return null;
			}

		} catch( Exception e) {
			return null;
		}
		finally {
			urlConnection.disconnect();
		}
	}

}
