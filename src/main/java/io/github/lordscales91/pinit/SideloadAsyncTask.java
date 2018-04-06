package io.github.lordscales91.pinit;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import io.github.lordscales91.pinit.utils.CallbackReceiver;
import android.os.AsyncTask;

public class SideloadAsyncTask extends AsyncTask<String, Void, String> {

	private static final MediaType JSON = MediaType.parse("application/json");
	private static final String TARGET_URL = "https://api.imgur.com/3/image";
	private static final String URL_FORMAT = "https://imgur.com/%s";

	private CallbackReceiver receiver;
	private String sourceUrl;
	private String tag;
	private String clientId;
	private boolean isBlacklisted;
	private Exception lastException;

	public SideloadAsyncTask(CallbackReceiver receiver, String sourceUrl,
			String tag, String clientId, boolean isBlacklisted) {
		this.receiver = receiver;
		this.sourceUrl = sourceUrl;
		this.tag = tag;
		this.clientId = clientId;
		this.isBlacklisted = isBlacklisted;
	}

	@Override
	protected String doInBackground(String... params) {
		String result = null;
		try {
			JSONObject obj = new JSONObject();
			obj.put("image", params[0]);
			obj.put("type", "url");
			OkHttpClient client = new OkHttpClient();
			Request req = new Request.Builder()
					.addHeader("Authorization", "Client-ID " + clientId)
					.url(TARGET_URL)
					.post(RequestBody.create(JSON, obj.toString())).build();
			Response resp = client.newCall(req).execute();
			if(resp.isSuccessful()) {
				obj = new JSONObject(resp.body().string());
				if(obj.has("data")) {
					JSONObject data = obj.getJSONObject("data");
					if(data.has("id") && data.has("link")) {
						result = (isBlacklisted)?String.format(URL_FORMAT, data.getString("id")):sourceUrl;
						result += ";" + data.getString("link");
					}
				}
			}
			if(result == null) {
				throw new IOException("Unable to sideload image. Response code: "+resp.code());
			}
		} catch (JSONException e) {
			this.lastException = e;
		} catch (IOException e) {
			this.lastException = e;
		}
		return result;
	}
	
	@Override
	protected void onPostExecute(String result) {
		if(lastException == null) {
			this.receiver.receiveData(result, tag);			
		} else {
			this.receiver.receiveData(lastException, tag);
		}
	}

}
