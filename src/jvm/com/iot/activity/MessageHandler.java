package com.iot.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

public class MessageHandler {
	// this.collectResults(0, predictedClass);

	public int[] result;
	public int resultCount;
	public static double lat = 37.343021;
	public static double lan = -121.995744;

	public MessageHandler() {
		this.result = new int[7];
		for (int i = 0; i < 7; i++) {
			result[i] = 0;
		}
		resultCount = 0;
	}

	public void collectResults(int user, int predictedClass) {
		resultCount++;
		result[predictedClass] = result[predictedClass] + 1;

		if (resultCount % 700 == 0) {
			notifyPubnub(user);
			for (int i = 0; i < 7; i++) {
				result[i] = 0;
			}
		}
	}

	private void notifyPubnub(int user) {
		// replace pub-key sub-key with your pubnub account keys
		final Pubnub pubnub = new Pubnub(
				"pub-key",
				"sub-key");
		pubnub.setAuthKey("");
		Callback callback = new Callback() {
			public void successCallback(String channel, Object response) {
				//System.out.println(response.toString());
			}

			public void errorCallback(String channel, PubnubError error) {
				System.out.println(error.toString());
			}
		};

		JSONObject obj = new JSONObject();
		try {
			int currentActivity = 0;
			int maxVal = 0;
			for (int i = 1; i < result.length; i++) {
				if (maxVal < result[i]) {
					maxVal = result[i];
					currentActivity = i;
				}
				obj.put("A" + i, result[i]);
			}
			obj.put("CA", this.activityMap(currentActivity));
			obj.put("CAVal", maxVal/7);
			
			// Track location for user 2
			if(user == 2){
				obj.put("lat", lat+ 0.00400);
				obj.put("lan", lan );
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONObject uObj = new JSONObject();
		try {
			uObj.putOpt("User" + user, obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(uObj.toString());
		pubnub.publish("my_channel", uObj, callback);
	}

	public String activityMap(int id) {
		switch (id) {
		case 1:
			return "Lying";
		case 2:
			return "Sitting";
		case 3:
			return "Standing";
		case 4:
			return "Walking";
		case 5:
			return "Running";
		case 6:
			return "Cycling";
		default:
			return "Lying";
		}
	}

}
